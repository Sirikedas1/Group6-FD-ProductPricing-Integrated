package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class ProductRateDtos {
    private ProductRateDtos() {}

    public record ProductRateRequest(
            @JsonProperty("RATE_ID") @NotBlank @Size(max = 20) String rateId,
            @JsonProperty("PRODUCT_CODE") @NotBlank @Size(max = 20) String productCode,
            @JsonProperty("CATEGORY_CODE") @Size(max = 20) String categoryCode,
            @JsonProperty("MIN_TENURE_MONTHS") @Min(0) Integer minTenureMonths,
            @JsonProperty("MAX_TENURE_MONTHS") @Min(0) Integer maxTenureMonths,
            @JsonProperty("MIN_AMOUNT") @DecimalMin("0.00") BigDecimal minAmount,
            @JsonProperty("MAX_AMOUNT") @DecimalMin("0.00") BigDecimal maxAmount,
            @JsonProperty("BASE_RATE") @NotNull @DecimalMin("0.00") BigDecimal baseRate,
            @JsonProperty("ADDITIONAL_RATE_PERCENT") @DecimalMin("0.00") BigDecimal additionalRatePercent,
            @JsonProperty("EFFECTIVE_RATE") @DecimalMin("0.00") BigDecimal effectiveRate,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("EXPIRY_DATE") LocalDate expiryDate) {}

    public record ProductRateResponse(
            @JsonProperty("RATE_ID") String rateId,
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("CATEGORY_CODE") String categoryCode,
            @JsonProperty("MIN_TENURE_MONTHS") Integer minTenureMonths,
            @JsonProperty("MAX_TENURE_MONTHS") Integer maxTenureMonths,
            @JsonProperty("MIN_AMOUNT") BigDecimal minAmount,
            @JsonProperty("MAX_AMOUNT") BigDecimal maxAmount,
            @JsonProperty("BASE_RATE") BigDecimal baseRate,
            @JsonProperty("ADDITIONAL_RATE_PERCENT") BigDecimal additionalRatePercent,
            @JsonProperty("EFFECTIVE_RATE") BigDecimal effectiveRate,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("EXPIRY_DATE") LocalDate expiryDate,
            @JsonProperty("AUD_EFF_DATE") java.time.LocalDate auditEffectiveDate,
            @JsonProperty("AUD_CRUD") String auditCrud,
            @JsonProperty("AUD_USER_ID") String auditUserId,
            @JsonProperty("AUD_WS_ID") String auditWorkstationId,
            @JsonProperty("AUD_PRGM_ID") String auditProgramId,
            @JsonProperty("AUD_HOST_TS") OffsetDateTime auditHostTimestamp,
            @JsonProperty("AUD_LOCAL_TS") OffsetDateTime auditLocalTimestamp,
            @JsonProperty("AUD_ACPT_TS") OffsetDateTime auditAcceptanceTimestamp,
            @JsonProperty("AUD_ACPT_TS_OFST") OffsetDateTime auditAcceptanceOffsetTimestamp,
            @JsonProperty("AUD_UUID") String auditUuid) {}
}
