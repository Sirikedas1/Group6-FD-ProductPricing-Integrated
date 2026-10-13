package com.bank.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass @Getter @Setter
public abstract class AuditableEntity {
    @Column(name="aud_eff_date", nullable=false) private LocalDate auditEffectiveDate;
    @Column(name="aud_crud", nullable=false, length=1, columnDefinition="CHAR(1)") private String auditCrud;
    @Column(name="aud_user_id", nullable=false, length=100) private String auditUserId;
    @Column(name="aud_ws_id", nullable=false, length=100) private String auditWorkstationId;
    @Column(name="aud_prgm_id", nullable=false, length=100) private String auditProgramId;
    @Column(name="aud_host_ts", nullable=false) private OffsetDateTime auditHostTimestamp;
    @Column(name="aud_local_ts", nullable=false) private OffsetDateTime auditLocalTimestamp;
    @Column(name="aud_acpt_ts", nullable=false) private OffsetDateTime auditAcceptanceTimestamp;
    @Column(name="aud_acpt_ts_ofst", nullable=false) private OffsetDateTime auditAcceptanceOffsetTimestamp;
    @Column(name="aud_uuid", nullable=false, unique=true, length=100) private String auditUuid;

    @PrePersist void createAudit() {
        OffsetDateTime now=OffsetDateTime.now();
        if(auditEffectiveDate==null) auditEffectiveDate=now.toLocalDate(); if(auditCrud==null) auditCrud="C";
        if(auditUserId==null) auditUserId="SYSTEM"; if(auditWorkstationId==null) auditWorkstationId="API";
        if(auditProgramId==null) auditProgramId="product-service"; auditHostTimestamp=now; auditLocalTimestamp=now;
        auditAcceptanceTimestamp=now; auditAcceptanceOffsetTimestamp=now; if(auditUuid==null) auditUuid=UUID.randomUUID().toString();
    }
    @PreUpdate void updateAudit() { auditCrud="U"; auditHostTimestamp=OffsetDateTime.now(); auditLocalTimestamp=auditHostTimestamp; }
}
