package com.banklab.calculator.dto;

import java.math.BigDecimal;

/**
 * A deposit product from product-service (used only in Phase C when rates come from products).
 */
public record ProductInfo(
        String code,
        String name,
        BigDecimal minRate,
        BigDecimal maxRate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String defaultCompoundingFrequency) {
}
