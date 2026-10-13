package com.banklab.calculator.dto;

import java.math.BigDecimal;

/**
 * One customer category shown in the dropdown, e.g. SENIOR, "Senior Citizen", +1.00%.
 * additionalRatePercent can be null when the add-on depends on a product (Phase C).
 */
public record CategoryInfo(String code, String name, BigDecimal additionalRatePercent) {
}
