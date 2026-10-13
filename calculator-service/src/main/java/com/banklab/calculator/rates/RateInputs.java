package com.banklab.calculator.rates;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Everything the calculator needs to know about rates for ONE calculation.
 *
 * baseRate         : base yearly rate in percent (e.g. 6.50)
 * addOns           : add-on percent for each selected category (e.g. SENIOR=1.00, EMPLOYEE=1.00)
 * addOnCap         : maximum combined add-on (e.g. 2.00)
 * maxEffectiveRate : product-defined maximum rate (Lab L11); null when there is no product
 * source           : where the numbers came from, e.g. "LOCAL" or "PRODUCT:FD001"
 */
public record RateInputs(
        BigDecimal baseRate,
        Map<String, BigDecimal> addOns,
        BigDecimal addOnCap,
        BigDecimal maxEffectiveRate,
        String source) {
}
