package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="product_business_rule") @Getter @Setter @NoArgsConstructor public class ProductBusinessRule extends AuditableEntity {
 @Id @Column(name="rule_id",length=20) private String ruleId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @Column(name="rule_code",nullable=false,length=40) private String ruleCode; @Column(name="rule_value",nullable=false,length=200) private String ruleValue;
 @Column(nullable=false) private boolean enabled; @Column(length=200) private String description;
}
