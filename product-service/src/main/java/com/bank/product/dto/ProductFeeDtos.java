package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class ProductFeeDtos {
    private ProductFeeDtos() {}

    public record ProductFeeRequest(
            @JsonProperty("FEE_RATE_ID") @NotBlank @Size(max = 20) String feeRateId,
            @JsonProperty("PRODUCT_CODE") @NotBlank @Size(max = 20) String productCode,
            @JsonProperty("CATEGORY_CODE") @Size(max = 20) String categoryCode,
            @JsonProperty("FEE_TYPE") @NotBlank @Size(max = 30) String feeType,
            @JsonProperty("FEE_NAME") @NotBlank @Size(max = 50) String feeName,
            @JsonProperty("FEE_AMOUNT") @DecimalMin("0.00") BigDecimal feeAmount,
            @JsonProperty("FEE_PERCENT") @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal feePercent,
            @JsonProperty("TRIGGER_EVENT") @Size(max = 50) String triggerEvent,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("EXPIRY_DATE") LocalDate expiryDate) {}

    public record ProductFeeResponse(
            @JsonProperty("FEE_RATE_ID") String feeRateId,
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("CATEGORY_CODE") String categoryCode,
            @JsonProperty("FEE_TYPE") String feeType,
            @JsonProperty("FEE_NAME") String feeName,
            @JsonProperty("FEE_AMOUNT") BigDecimal feeAmount,
            @JsonProperty("FEE_PERCENT") BigDecimal feePercent,
            @JsonProperty("TRIGGER_EVENT") String triggerEvent,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("EXPIRY_DATE") LocalDate expiryDate,
            @JsonProperty("AUD_EFF_DATE") LocalDate auditEffectiveDate,
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
