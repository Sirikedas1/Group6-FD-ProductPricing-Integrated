package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="product_role") @Getter @Setter @NoArgsConstructor public class ProductRole extends AuditableEntity {
 @Id @Column(name="role_id",length=20) private String roleId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @Column(name="role_type",nullable=false,length=30) private String roleType; @Column(name="is_mandatory",nullable=false) private boolean mandatory;
}
