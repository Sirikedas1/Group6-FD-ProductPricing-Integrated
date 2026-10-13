package com.bank.product.service;

import com.bank.product.dto.PricingQuoteResponse;
import com.bank.product.dto.ProductFeeDtos.ProductFeeResponse;
import com.bank.product.exception.BadRequestException;
import com.bank.product.exception.NotFoundException;
import com.bank.product.model.*;
import com.bank.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service @RequiredArgsConstructor
public class PricingQuoteService {
    private final ProductRepository products;
    private final ProductRateRepository rates;
    private final ProductFeeRepository fees;
    private final CustomerCategoryRepository categories;

    @Transactional(readOnly = true)
    public PricingQuoteResponse quote(String code, String customerCategory, BigDecimal amount,
                                      int tenureMonths, LocalDate requestedDate) {
        String productCode = SubmissionRules.code(code);
        String categoryCode = SubmissionRules.optionalCode(customerCategory);
        LocalDate date = requestedDate == null ? LocalDate.now() : requestedDate;
        if (amount == null || amount.signum() <= 0 || tenureMonths <= 0)
            throw new BadRequestException("AMOUNT and TENURE_MONTHS must be positive");
        Product product = products.findAggregate(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found: " + code));
        if (product.getStatus() != ProductStatus.ACTIVE || product.getEffectiveDate().isAfter(date)
                || (product.getExpiryDate() != null && product.getExpiryDate().isBefore(date)))
            throw new BadRequestException("Product is not active on " + date);
        if ((product.getMinAmount() != null && amount.compareTo(product.getMinAmount()) < 0)
                || (product.getMaxAmount() != null && amount.compareTo(product.getMaxAmount()) > 0))
            throw new BadRequestException("AMOUNT is outside the product limits");
        if ((product.getMinTermMonths() != null && product.getMinTermMonths() > 0 && tenureMonths < product.getMinTermMonths())
                || (product.getMaxTermMonths() != null && product.getMaxTermMonths() > 0 && tenureMonths > product.getMaxTermMonths()))
            throw new BadRequestException("TENURE_MONTHS is outside the product limits");
        if (categoryCode != null && !categories.existsById(categoryCode))
            throw new BadRequestException("Unknown CATEGORY_CODE: " + categoryCode);

        ProductRate selected = rates.findByProduct_ProductCode(productCode).stream()
                .filter(r -> matchesCategory(r.getCategory(), categoryCode))
                .filter(r -> within(tenureMonths, r.getMinTenureMonths(), r.getMaxTenureMonths()))
                .filter(r -> within(amount, r.getMinAmount(), r.getMaxAmount()))
                .filter(r -> within(date, r.getEffectiveDate(), r.getExpiryDate()))
                .sorted(Comparator.comparing((ProductRate r) -> r.getCategory() == null ? 1 : 0)
                        .thenComparing(ProductRate::getRateId))
                .findFirst().orElseThrow(() -> new NotFoundException("No applicable product rate"));
        BigDecimal additional = selected.getAdditionalRatePercent() == null ? BigDecimal.ZERO : selected.getAdditionalRatePercent();
        BigDecimal effective = selected.getEffectiveRate() == null ? selected.getBaseRate().add(additional) : selected.getEffectiveRate();
        if (product.getMaxRate() != null && effective.compareTo(product.getMaxRate()) > 0)
            throw new BadRequestException("Effective rate exceeds the product MAX_RATE");
        List<ProductFeeResponse> applicableFees = fees.findByProduct_ProductCode(productCode).stream()
                .filter(f -> matchesCategory(f.getCategory(), categoryCode))
                .filter(f -> within(date, f.getEffectiveDate(), f.getExpiryDate()))
                .sorted(Comparator.comparing(ProductFeeRate::getFeeRateId))
                .map(this::feeResponse).toList();
        return new PricingQuoteResponse(productCode, categoryCode, amount, tenureMonths, date,
                selected.getRateId(), selected.getBaseRate(), additional, effective, applicableFees);
    }

    private boolean matchesCategory(CustomerCategory category, String requested) {
        return category == null || category.getCategoryCode().equals(requested);
    }

    private static <T extends Comparable<T>> boolean within(T value, T min, T max) {
        return (min == null || value.compareTo(min) >= 0) && (max == null || value.compareTo(max) <= 0);
    }

    private ProductFeeResponse feeResponse(ProductFeeRate f) {
        return new ProductFeeResponse(f.getFeeRateId(), f.getProduct().getProductCode(),
                f.getCategory() == null ? null : f.getCategory().getCategoryCode(), f.getFeeType(), f.getFeeName(),
                f.getFeeAmount(), f.getFeePercent(), f.getTriggerEvent(), f.getEffectiveDate(), f.getExpiryDate(),
                f.getAuditEffectiveDate(), f.getAuditCrud(), f.getAuditUserId(), f.getAuditWorkstationId(),
                f.getAuditProgramId(), f.getAuditHostTimestamp(), f.getAuditLocalTimestamp(),
                f.getAuditAcceptanceTimestamp(), f.getAuditAcceptanceOffsetTimestamp(), f.getAuditUuid());
    }
}
