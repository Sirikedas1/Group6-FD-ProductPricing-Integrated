package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class CustomerCategoryDtos {
    private CustomerCategoryDtos() {}

    public record CustomerCategoryRequest(
            @JsonProperty("CATEGORY_CODE") @NotBlank @Size(max = 20) String categoryCode,
            @JsonProperty("CATEGORY_NAME") @NotBlank @Size(max = 50) String categoryName,
            @JsonProperty("DESCRIPTION") @Size(max = 200) String description) {}

    public record CustomerCategoryResponse(
            @JsonProperty("CATEGORY_CODE") String categoryCode,
            @JsonProperty("CATEGORY_NAME") String categoryName,
            @JsonProperty("DESCRIPTION") String description,
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
