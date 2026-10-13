package com.banklab.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * What the backend sends back after a calculation.
 * maturityAmount, effectiveRate and interestEarned are the three outputs
 * named in Lab L6 section 3.1; the rest explain HOW the result was reached.
 */
public record FdCalculationResponse(
        BigDecimal principal,
        int termValue,
        String termUnit,
        String interestType,
        String compoundingFrequency,
        String calculationMethod,
        String productCode,
        BigDecimal baseRate,
        List<String> categories,
        Map<String, BigDecimal> categoryAddOns,
        BigDecimal requestedAddOn,
        BigDecimal appliedAddOn,
        BigDecimal addOnCap,
        boolean capApplied,
        BigDecimal maxEffectiveRate,
        boolean maxRateCapApplied,
        BigDecimal effectiveRate,
        BigDecimal interestEarned,
        BigDecimal maturityAmount,
        BigDecimal apy,
        LocalDate startDate,
        LocalDate maturityDate,
        String currencyCode,
        String rateSource) {
}
