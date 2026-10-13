package com.bank.product.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="customer_communication") @Getter @Setter @NoArgsConstructor public class CustomerCommunication extends AuditableEntity {
 @Id @Column(name="communication_id",length=20) private String communicationId; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_code",nullable=false) private Product product;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_code",nullable=false) private CustomerCategory category;
 @Column(name="communication_type",nullable=false,length=30) private String communicationType; @Column(nullable=false,length=20) private String channel;
 @Column(nullable=false,length=20) private String frequency; @Column(name="template_name",nullable=false,length=50) private String templateName; @Column(name="is_active",nullable=false) private boolean active;
}
