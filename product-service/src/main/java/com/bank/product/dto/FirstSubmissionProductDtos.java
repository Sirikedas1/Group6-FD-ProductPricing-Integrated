package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class FirstSubmissionProductDtos {
    private FirstSubmissionProductDtos() {}

    public record ProductRequest(
            @JsonProperty("PRODUCT_CODE") @NotBlank @Size(max = 20) String productCode,
            @JsonProperty("PRODUCT_ID") @NotBlank @Size(max = 20) String productId,
            @JsonProperty("PRODUCT_NAME") @NotBlank @Size(max = 100) String productName,
            @JsonProperty("PRODUCT_TYPE") @NotBlank @Size(max = 30) String productType,
            @JsonProperty("CURRENCY") @NotBlank @Size(max = 3) String currency,
            @JsonProperty("MIN_AMOUNT") @DecimalMin("0.00") BigDecimal minAmount,
            @JsonProperty("MAX_AMOUNT") @DecimalMin("0.00") BigDecimal maxAmount,
            @JsonProperty("MIN_AGE") @Min(0) Integer minAge,
            @JsonProperty("MIN_RATE") @DecimalMin("0.00") BigDecimal minRate,
            @JsonProperty("MAX_RATE") @DecimalMin("0.00") BigDecimal maxRate,
            @JsonProperty("DEFAULT_COMPOUNDING_FREQUENCY") @Size(max = 20) String defaultCompoundingFrequency,
            @JsonProperty("DEFAULT_PAYOUT_FREQUENCY") @Size(max = 20) String defaultPayoutFrequency,
            @JsonProperty("SUPPORTED_FREQUENCIES") @Size(max = 200) String supportedFrequencies,
            @JsonProperty("DEFAULT_MATURITY_INSTRUCTION") @Size(max = 30) String defaultMaturityInstruction,
            @JsonProperty("TDS_RATE") @DecimalMin("0.00") BigDecimal tdsRate,
            @JsonProperty("TDS_EXEMPTION_THRESHOLD") @DecimalMin("0.00") BigDecimal tdsExemptionThreshold,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("STATUS") @Size(max = 20) String status) {}

    public record ProductResponse(
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("PRODUCT_ID") String productId,
            @JsonProperty("PRODUCT_NAME") String productName,
            @JsonProperty("PRODUCT_TYPE") String productType,
            @JsonProperty("CURRENCY") String currency,
            @JsonProperty("MIN_AMOUNT") BigDecimal minAmount,
            @JsonProperty("MAX_AMOUNT") BigDecimal maxAmount,
            @JsonProperty("MIN_AGE") Integer minAge,
            @JsonProperty("MIN_RATE") BigDecimal minRate,
            @JsonProperty("MAX_RATE") BigDecimal maxRate,
            @JsonProperty("DEFAULT_COMPOUNDING_FREQUENCY") String defaultCompoundingFrequency,
            @JsonProperty("DEFAULT_PAYOUT_FREQUENCY") String defaultPayoutFrequency,
            @JsonProperty("SUPPORTED_FREQUENCIES") String supportedFrequencies,
            @JsonProperty("DEFAULT_MATURITY_INSTRUCTION") String defaultMaturityInstruction,
            @JsonProperty("TDS_RATE") BigDecimal tdsRate,
            @JsonProperty("TDS_EXEMPTION_THRESHOLD") BigDecimal tdsExemptionThreshold,
            @JsonProperty("EFFECTIVE_DATE") LocalDate effectiveDate,
            @JsonProperty("STATUS") String status,
            @JsonProperty("CREATED_AT") OffsetDateTime createdAt,
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

    public record ProductPage(List<ProductResponse> items, int limit, int offset, long total) {}

    public record StatusHistoryResponse(
            @JsonProperty("HISTORY_ID") String historyId,
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("PREVIOUS_STATUS") String previousStatus,
            @JsonProperty("NEW_STATUS") String newStatus,
            @JsonProperty("CHANGED_BY") String changedBy,
            @JsonProperty("CHANGED_AT") java.time.LocalDateTime changedAt,
            @JsonProperty("REMARKS") String remarks) {}
}
