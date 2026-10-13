package com.bank.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_status_history")
@Getter @Setter @NoArgsConstructor
public class ProductStatusHistory {
    @Id
    @Column(name = "history_id", length = 36)
    private String historyId;

    @Column(name = "product_code", nullable = false, length = 20)
    private String productCode;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", nullable = false, length = 30)
    private String newStatus;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
