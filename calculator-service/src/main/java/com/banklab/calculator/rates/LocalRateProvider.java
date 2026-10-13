package com.banklab.calculator.rates;

import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.exception.CalculationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in rate table, used until product-service is live
 * (your WBS task A3.2 "Stub product data provider for offline dev").
 *
 * Add-on values:
 *   SENIOR       +1.00  -> stated in Lab L6 ("Senior Citizen +1%")
 *   EMPLOYEE     +1.00  -> inferred so that Lab L11 row 1 gives 8.5%
 *   PREMIUM_USER +2.00  -> inferred so that Lab L11 row 2 shows +3.0% before the cap
 *   SUPER_SENIOR +1.50  -> not in the manual; your team's WBS seeds this category, and
 *                          1.50 keeps the senior:super-senior step used by the Credexa
 *                          reference project (0.50 -> 0.75). CONFIRM the value with Lavanya.
 *   GENERAL       0.00  -> ordinary customer, no bonus
 * The base rate is typed by the user, exactly like the Lab L6 payload ("baseRate": 6.5).
 *
 * Active only when calculator.rates.source=LOCAL (or when the setting is missing).
 */
@Component
@ConditionalOnProperty(name = "calculator.rates.source", havingValue = "LOCAL", matchIfMissing = true)
public class LocalRateProvider implements RateProvider {

    private static final Map<String, CategoryInfo> TABLE = new LinkedHashMap<>();

    static {
        add("GENERAL", "General (Regular)", "0.00");
        add("SENIOR", "Senior Citizen", "1.00");
        add("EMPLOYEE", "Bank Employee", "1.00");
        add("PREMIUM_USER", "Premium User", "2.00");
        add("SUPER_SENIOR", "Super Senior Citizen (80+)", "1.50");
    }

    private static void add(String code, String name, String addOn) {
        TABLE.put(code, new CategoryInfo(code, name, new BigDecimal(addOn)));
    }

    private final BigDecimal addOnCap;

    public LocalRateProvider(@Value("${calculator.rates.add-on-cap:2.00}") BigDecimal addOnCap) {
        this.addOnCap = addOnCap;
    }

    @Override
    public String sourceName() {
        return "LOCAL";
    }

    @Override
    public boolean usesRequestBaseRate() {
        return true;
    }

    @Override
    public BigDecimal addOnCap() {
        return addOnCap;
    }

    @Override
    public List<ProductInfo> listProducts() {
        return List.of();
    }

    @Override
    public List<CategoryInfo> listCategories(String productCode) {
        return new ArrayList<>(TABLE.values());
    }

    @Override
    public RateInputs getRateInputs(String productCode,
                                    BigDecimal requestedBaseRate,
                                    List<String> categoryCodes,
                                    BigDecimal principal,
                                    int tenureMonths) {
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        List<String> unknown = new ArrayList<>();
        for (String code : categoryCodes) {
            CategoryInfo info = TABLE.get(code);
            if (info == null) {
                unknown.add(code);
            } else {
                addOns.put(code, info.additionalRatePercent());
            }
        }
        if (!unknown.isEmpty()) {
            throw CalculationException.validation(List.of(
                    "Unknown customer category: " + String.join(", ", unknown)
                            + ". Allowed: " + String.join(", ", TABLE.keySet()) + "."));
        }
        return new RateInputs(requestedBaseRate, addOns, addOnCap, null, "LOCAL");
    }
}
