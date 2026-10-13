package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class CustomerCommunicationDtos {
    private CustomerCommunicationDtos() {}

    public record CustomerCommunicationRequest(
            @JsonProperty("COMMUNICATION_ID") @NotBlank @Size(max = 20) String communicationId,
            @JsonProperty("PRODUCT_CODE") @NotBlank @Size(max = 20) String productCode,
            @JsonProperty("CATEGORY_CODE") @NotBlank @Size(max = 20) String categoryCode,
            @JsonProperty("COMMUNICATION_TYPE") @NotBlank @Size(max = 30) String communicationType,
            @JsonProperty("CHANNEL") @NotBlank @Size(max = 20) String channel,
            @JsonProperty("FREQUENCY") @NotBlank @Size(max = 20) String frequency,
            @JsonProperty("TEMPLATE_NAME") @NotBlank @Size(max = 50) String templateName,
            @JsonProperty("IS_ACTIVE") Boolean active) {}

    public record CustomerCommunicationResponse(
            @JsonProperty("COMMUNICATION_ID") String communicationId,
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("CATEGORY_CODE") String categoryCode,
            @JsonProperty("COMMUNICATION_TYPE") String communicationType,
            @JsonProperty("CHANNEL") String channel,
            @JsonProperty("FREQUENCY") String frequency,
            @JsonProperty("TEMPLATE_NAME") String templateName,
            @JsonProperty("IS_ACTIVE") boolean active,
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
