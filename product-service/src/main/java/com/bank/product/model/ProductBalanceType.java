package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="product_balance_type") @Getter @Setter @NoArgsConstructor public class ProductBalanceType extends AuditableEntity {
 @Id @Column(name="balance_type_id",length=20) private String balanceTypeId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @Column(name="balance_code",nullable=false,length=20) private String balanceCode; @Column(nullable=false,length=30) private String classification;
}
