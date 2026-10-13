package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="product_term_rule") @Getter @Setter @NoArgsConstructor public class ProductTermRule extends AuditableEntity {
 @Id @Column(name="term_rule_id",length=20) private String termRuleId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @Column(name="duration_unit",nullable=false,length=10) private String durationUnit; @Column(name="min_value",nullable=false) private Integer minValue;
 @Column(name="max_value",nullable=false) private Integer maxValue; @Column(name="default_value",nullable=false) private Integer defaultValue;
}
