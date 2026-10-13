package com.bank.product.model;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import lombok.*; import java.util.*;
@Entity @Table(name="customer_category") @Getter @Setter @NoArgsConstructor public class CustomerCategory extends AuditableEntity {
 @Id @Column(name="category_code",length=20) private String categoryCode; @Column(name="category_name",nullable=false,length=50) private String categoryName;
 @Column(length=200) private String description; @JsonIgnore @OneToMany(mappedBy="category") private List<ProductFeeRate> feeRates=new ArrayList<>();
}
