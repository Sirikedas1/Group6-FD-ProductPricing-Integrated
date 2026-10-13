package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="product_txn_type") @Getter @Setter @NoArgsConstructor public class ProductTransactionType extends AuditableEntity {
 @Id @Column(name="txn_type_id",length=20) private String transactionTypeId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @Column(name="txn_code",nullable=false,length=20) private String transactionCode; @Column(name="txn_name",nullable=false,length=50) private String transactionName;
 @Column(name="dc_indicator",nullable=false,length=1,columnDefinition="CHAR(1)") private String debitCreditIndicator;
}
