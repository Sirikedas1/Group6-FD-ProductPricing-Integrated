package com.bank.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class ProductRoleDtos {
    private ProductRoleDtos() {}

    public record ProductRoleRequest(
            @JsonProperty("ROLE_ID") @NotBlank @Size(max = 20) String roleId,
            @JsonProperty("PRODUCT_CODE") @NotBlank @Size(max = 20) String productCode,
            @JsonProperty("ROLE_TYPE") @NotBlank @Size(max = 30) String roleType,
            @JsonProperty("IS_MANDATORY") Boolean mandatory) {}

    public record ProductRoleResponse(
            @JsonProperty("ROLE_ID") String roleId,
            @JsonProperty("PRODUCT_CODE") String productCode,
            @JsonProperty("ROLE_TYPE") String roleType,
            @JsonProperty("IS_MANDATORY") boolean mandatory,
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
