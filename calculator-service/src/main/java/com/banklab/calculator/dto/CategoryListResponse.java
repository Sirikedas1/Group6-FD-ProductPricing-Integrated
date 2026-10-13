package com.banklab.calculator.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Answer of GET /api/fd/categories.
 * rateSource     : LOCAL (built-in table) or PRODUCT (product-service)
 * categorySource : REQUEST (user picks categories) or PROFILE (taken from the logged-in customer)
 * addOnCap       : maximum combined add-on in percent (2.00)
 */
public record CategoryListResponse(
        String rateSource,
        String categorySource,
        BigDecimal addOnCap,
        List<CategoryInfo> categories) {
}
