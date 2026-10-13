package com.banklab.calculator.rates;

import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * A "plug" for rate data. The calculator does not care WHERE rates come from;
 * it just asks a RateProvider.
 *
 *  - LocalRateProvider   : built-in table (Phase A/B)
 *  - ProductRateProvider : your teammates' product-service (Phase C)
 *
 * Only one of them is switched on at a time, chosen by the setting
 * calculator.rates.source in application.properties.
 * (Same "pluggable" idea as the AccountNumberGenerator interface in Lab L12.)
 */
public interface RateProvider {

    /** "LOCAL" or "PRODUCT". */
    String sourceName();

    /** True when the base rate must be typed by the user (LOCAL). False when it comes from a product. */
    boolean usesRequestBaseRate();

    /** Maximum combined category add-on in percent. */
    BigDecimal addOnCap();

    /** Deposit products the user can pick from (empty list in LOCAL mode). */
    List<ProductInfo> listProducts();

    /** Categories for the dropdown, with their add-on percent. */
    List<CategoryInfo> listCategories(String productCode);

    /**
     * Rates for one calculation.
     *
     * @param productCode       product such as FD001 (may be null in LOCAL mode)
     * @param requestedBaseRate base rate typed by the user (may be null in PRODUCT mode)
     * @param categoryCodes     normalised category codes, e.g. [SENIOR, EMPLOYEE]
     * @param principal         deposit amount (products can have amount bands)
     * @param tenureMonths      term in whole months (products can have term bands)
     */
    RateInputs getRateInputs(String productCode,
                             BigDecimal requestedBaseRate,
                             List<String> categoryCodes,
                             BigDecimal principal,
                             int tenureMonths);
}
