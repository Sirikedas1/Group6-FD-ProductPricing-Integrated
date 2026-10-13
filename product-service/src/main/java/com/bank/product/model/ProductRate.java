package com.bank.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_rate_matrix")
@Getter @Setter @NoArgsConstructor
public class ProductRate extends AuditableEntity {
    @Id @Column(name = "rate_id", length = 20) private String rateId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_code", nullable = false) private Product product;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_code") private CustomerCategory category;
    @Column(name = "min_tenure_months") private Integer minTenureMonths;
    @Column(name = "max_tenure_months") private Integer maxTenureMonths;
    @Column(name = "min_amount", precision = 15, scale = 2) private BigDecimal minAmount;
    @Column(name = "max_amount", precision = 15, scale = 2) private BigDecimal maxAmount;
    @Column(name = "base_rate", nullable = false, precision = 9, scale = 6) private BigDecimal baseRate;
    @Column(name = "additional_rate_percent", precision = 9, scale = 6) private BigDecimal additionalRatePercent;
    @Column(name = "effective_rate", precision = 9, scale = 6) private BigDecimal effectiveRate;
    @Column(name = "effective_date") private LocalDate effectiveDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
}
