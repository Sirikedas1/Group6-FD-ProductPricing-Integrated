package com.bank.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "currency")
@Getter @Setter @NoArgsConstructor
public class Currency extends AuditableEntity {
    @Id @Column(name = "currency_code", length = 3) private String currencyCode;
    @Column(name = "currency_name", nullable = false, length = 50) private String currencyName;
    @Column(name = "currency_symbol", length = 5) private String currencySymbol;
    @Column(name = "decimal_places", nullable = false) private Integer decimalPlaces;
    @Column(name = "is_active", nullable = false) private boolean active;
}
