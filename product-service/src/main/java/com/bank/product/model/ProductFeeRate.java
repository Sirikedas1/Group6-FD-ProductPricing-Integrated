package com.bank.product.model;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="product_fee_matrix") @Getter @Setter @NoArgsConstructor public class ProductFeeRate extends AuditableEntity {
 @Id @Column(name="fee_rate_id",length=20) private String feeRateId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_code") private CustomerCategory category;
 @Column(name="fee_type",nullable=false,length=30) private String feeType; @Column(name="fee_name",nullable=false,length=50) private String feeName;
 @Column(name="fee_amount",precision=15,scale=2) private BigDecimal feeAmount; @Column(name="fee_percent",precision=9,scale=6) private BigDecimal feePercent;
 @Column(name="trigger_event",length=50) private String triggerEvent; @Column(name="additional_rate_percent",precision=9,scale=6) private BigDecimal additionalRatePercent;
 @Column(name="penalty_percent",precision=9,scale=6) private BigDecimal penaltyPercent; @Column(name="effective_date",nullable=false) private LocalDate effectiveDate; @Column(name="expiry_date") private LocalDate expiryDate;
}
