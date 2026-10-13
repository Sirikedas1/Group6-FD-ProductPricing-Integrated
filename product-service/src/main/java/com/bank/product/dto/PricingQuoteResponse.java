package com.bank.product.dto;

import com.bank.product.dto.ProductFeeDtos.ProductFeeResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PricingQuoteResponse(
        @JsonProperty("PRODUCT_CODE") String productCode,
        @JsonProperty("CATEGORY_CODE") String categoryCode,
        @JsonProperty("AMOUNT") BigDecimal amount,
        @JsonProperty("TENURE_MONTHS") int tenureMonths,
        @JsonProperty("AS_OF") LocalDate asOf,
        @JsonProperty("RATE_ID") String rateId,
        @JsonProperty("BASE_RATE") BigDecimal baseRate,
        @JsonProperty("ADDITIONAL_RATE_PERCENT") BigDecimal additionalRatePercent,
        @JsonProperty("EFFECTIVE_RATE") BigDecimal effectiveRate,
        @JsonProperty("FEES") List<ProductFeeResponse> fees) {}
