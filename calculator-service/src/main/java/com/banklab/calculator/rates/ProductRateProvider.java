package com.banklab.calculator.rates;

import com.banklab.calculator.client.JsonFields;
import com.banklab.calculator.client.ProductClient;
import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Rates from your teammates' product-service (Lab L6: "integrate with Product and
 * Pricing module for fetching base rates"; Lab L11: "cap the effective rate based
 * on product-defined maximums").
 *
 * Uses the tables from your Product & Pricing ERD:
 *   PRODUCTS            -> STATUS, MIN_AMOUNT, MAX_AMOUNT, MAX_RATE
 *   PRODUCT_RATE_MATRIX -> PRODUCT_CODE, CATEGORY_CODE, MIN/MAX_TENURE_MONTHS,
 *                          MIN/MAX_AMOUNT, BASE_RATE, ADDITIONAL_RATE_PERCENT,
 *                          EFFECTIVE_DATE, EXPIRY_DATE
 *   CUSTOMER_CATEGORY   -> CATEGORY_CODE, CATEGORY_NAME
 *
 * Active only when calculator.rates.source=PRODUCT.
 */
@Component
@ConditionalOnProperty(name = "calculator.rates.source", havingValue = "PRODUCT")
public class ProductRateProvider implements RateProvider {

    private static final ZoneId INDIA = ZoneId.of("Asia/Kolkata");

    private final ProductClient productClient;
    private final BigDecimal addOnCap;

    public ProductRateProvider(ProductClient productClient,
                               @Value("${calculator.rates.add-on-cap:2.00}") BigDecimal addOnCap) {
        this.productClient = productClient;
        this.addOnCap = addOnCap;
    }

    @Override
    public String sourceName() {
        return "PRODUCT";
    }

    @Override
    public boolean usesRequestBaseRate() {
        return false;
    }

    @Override
    public BigDecimal addOnCap() {
        return addOnCap;
    }

    @Override
    public List<ProductInfo> listProducts() {
        List<ProductInfo> result = new ArrayList<>();
        for (JsonNode p : productClient.listProducts()) {
            String type = JsonFields.text(p, "PRODUCT_TYPE", "productType");
            String status = JsonFields.text(p, "STATUS", "status");
            boolean isFd = type == null || type.toUpperCase(Locale.ROOT).contains("FD")
                    || type.toUpperCase(Locale.ROOT).contains("FIXED");
            boolean active = status == null || status.equalsIgnoreCase("ACTIVE");
            if (isFd && active) {
                result.add(toProductInfo(p));
            }
        }
        return result;
    }

    @Override
    public List<CategoryInfo> listCategories(String productCode) {
        Map<String, String> names = new LinkedHashMap<>();
        for (JsonNode c : productClient.listCategories()) {
            String code = CategoryCodes.normalize(JsonFields.text(c, "CATEGORY_CODE", "categoryCode", "code"));
            if (code != null) {
                names.put(code, JsonFields.text(c, "CATEGORY_NAME", "categoryName", "name"));
            }
        }
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        if (productCode != null && !productCode.isBlank()) {
            for (JsonNode row : activeRows(productCode)) {
                String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
                if (code != null && !addOns.containsKey(code)) {
                    BigDecimal add = JsonFields.decimal(row, "ADDITIONAL_RATE_PERCENT", "additionalRatePercent");
                    addOns.put(code, add == null ? BigDecimal.ZERO : add);
                }
            }
        }
        List<CategoryInfo> result = new ArrayList<>();
        names.forEach((code, name) -> result.add(new CategoryInfo(code, name == null ? code : name, addOns.get(code))));
        return result;
    }

    @Override
    public RateInputs getRateInputs(String productCode,
                                    BigDecimal requestedBaseRate,
                                    List<String> categoryCodes,
                                    BigDecimal principal,
                                    int tenureMonths) {
        if (productCode == null || productCode.isBlank()) {
            throw CalculationException.badRequest("PRODUCT_REQUIRED",
                    "Please choose an FD product (rates come from product-service).");
        }
        JsonNode product = productClient.getProduct(productCode);

        String status = JsonFields.text(product, "STATUS", "status");
        if (status != null && !status.equalsIgnoreCase("ACTIVE")) {
            throw CalculationException.unprocessable("PRODUCT_NOT_ACTIVE",
                    "Product " + productCode + " is not active (status " + status + ").");
        }
        BigDecimal minAmount = JsonFields.decimal(product, "MIN_AMOUNT", "minAmount");
        BigDecimal maxAmount = JsonFields.decimal(product, "MAX_AMOUNT", "maxAmount");
        if (minAmount != null && principal.compareTo(minAmount) < 0) {
            throw CalculationException.unprocessable("AMOUNT_OUT_OF_RANGE",
                    "Minimum deposit for " + productCode + " is " + minAmount.toPlainString() + ".");
        }
        if (maxAmount != null && principal.compareTo(maxAmount) > 0) {
            throw CalculationException.unprocessable("AMOUNT_OUT_OF_RANGE",
                    "Maximum deposit for " + productCode + " is " + maxAmount.toPlainString() + ".");
        }

        // Rows for this product whose date window, term band and amount band fit this deposit
        List<JsonNode> rows = new ArrayList<>();
        for (JsonNode row : activeRows(productCode)) {
            if (fits(row, tenureMonths, principal)) {
                rows.add(row);
            }
        }
        if (rows.isEmpty()) {
            throw CalculationException.unprocessable("NO_RATE_CONFIGURED",
                    "No interest rate is configured in product " + productCode + " for "
                            + tenureMonths + " months and this amount.");
        }

        // Base rate: the GENERAL (or category-less) row; otherwise the first matching row
        JsonNode baseRow = rows.get(0);
        for (JsonNode row : rows) {
            String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
            if (code == null || code.equals("GENERAL")) {
                baseRow = row;
                break;
            }
        }
        BigDecimal baseRate = JsonFields.decimal(baseRow, "BASE_RATE", "baseRate");

        // Add-on for every selected category
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        List<String> notOffered = new ArrayList<>();
        for (String category : categoryCodes) {
            if (category.equals("GENERAL")) {
                addOns.put(category, BigDecimal.ZERO);
                continue;
            }
            BigDecimal add = null;
            boolean found = false;
            for (JsonNode row : rows) {
                String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
                if (category.equals(code)) {
                    add = JsonFields.decimal(row, "ADDITIONAL_RATE_PERCENT", "additionalRatePercent");
                    found = true;
                    break;
                }
            }
            if (found) {
                addOns.put(category, add == null ? BigDecimal.ZERO : add);
            } else {
                notOffered.add(category);
            }
        }
        if (!notOffered.isEmpty()) {
            throw CalculationException.unprocessable("CATEGORY_NOT_OFFERED",
                    "Product " + productCode + " has no rate for category " + String.join(", ", notOffered)
                            + " for this term and amount.");
        }

        BigDecimal maxRate = JsonFields.decimal(product, "MAX_RATE", "maxRate");
        return new RateInputs(baseRate, addOns, addOnCap, maxRate, "PRODUCT:" + productCode);
    }

    private List<JsonNode> activeRows(String productCode) {
        LocalDate today = LocalDate.now(INDIA);
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode row : productClient.listRates()) {
            String code = JsonFields.text(row, "PRODUCT_CODE", "productCode");
            if (code == null || !code.equalsIgnoreCase(productCode)) {
                continue;
            }
            LocalDate from = JsonFields.date(row, "EFFECTIVE_DATE", "effectiveDate");
            LocalDate to = JsonFields.date(row, "EXPIRY_DATE", "expiryDate");
            if ((from == null || !from.isAfter(today)) && (to == null || !to.isBefore(today))) {
                result.add(row);
            }
        }
        return result;
    }

    private static boolean fits(JsonNode row, int tenureMonths, BigDecimal principal) {
        Integer minT = JsonFields.integer(row, "MIN_TENURE_MONTHS", "minTenureMonths");
        Integer maxT = JsonFields.integer(row, "MAX_TENURE_MONTHS", "maxTenureMonths");
        BigDecimal minA = JsonFields.decimal(row, "MIN_AMOUNT", "minAmount");
        BigDecimal maxA = JsonFields.decimal(row, "MAX_AMOUNT", "maxAmount");
        return (minT == null || tenureMonths >= minT)
                && (maxT == null || tenureMonths <= maxT)
                && (minA == null || principal.compareTo(minA) >= 0)
                && (maxA == null || principal.compareTo(maxA) <= 0);
    }

    private static ProductInfo toProductInfo(JsonNode p) {
        return new ProductInfo(
                JsonFields.text(p, "PRODUCT_CODE", "productCode"),
                JsonFields.text(p, "PRODUCT_NAME", "productName"),
                JsonFields.decimal(p, "MIN_RATE", "minRate"),
                JsonFields.decimal(p, "MAX_RATE", "maxRate"),
                JsonFields.decimal(p, "MIN_AMOUNT", "minAmount"),
                JsonFields.decimal(p, "MAX_AMOUNT", "maxAmount"),
                JsonFields.text(p, "DEFAULT_COMPOUNDING_FREQUENCY", "defaultCompoundingFrequency"));
    }
}
