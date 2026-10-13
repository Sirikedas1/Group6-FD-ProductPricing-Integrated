package com.bank.product.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Entity @Table(name="products") @Getter @Setter @NoArgsConstructor
public class Product extends AuditableEntity {
    @Id @Column(name="product_code",length=20) private String productCode;
    @Column(name="product_id",nullable=false,unique=true,length=20) private String productId;
    @Column(name="product_name",nullable=false,length=100) private String productName;
    @Column(name="market_name",nullable=false,length=100) private String marketName;
    @Column(name="product_line",nullable=false,length=30) private String productLine;
    @Column(name="product_type",nullable=false,length=30) private String productType;
    @Column(name="product_subtype",length=30) private String productSubtype;
    @Column(name="product_group",length=30) private String productGroup;
    @Column(nullable=false,length=3) private String currency;
    @Column(name="min_amount",precision=15,scale=2) private BigDecimal minAmount;
    @Column(name="max_amount",precision=15,scale=2) private BigDecimal maxAmount;
    @Column(name="min_age") private Integer minAge;
    @Column(name="default_compounding_frequency",length=20) private String defaultCompoundingFrequency;
    @Column(name="default_payout_frequency",length=20) private String defaultPayoutFrequency;
    @Column(name="supported_frequencies",length=200) private String supportedFrequencies;
    @Column(name="default_maturity_instruction",length=30) private String defaultMaturityInstruction;
    @Column(name="tds_rate",precision=9,scale=6) private BigDecimal tdsRate;
    @Column(name="tds_exemption_threshold",precision=15,scale=2) private BigDecimal tdsExemptionThreshold;
    @Column(name="min_term_months",nullable=false) private Integer minTermMonths;
    @Column(name="max_term_months",nullable=false) private Integer maxTermMonths;
    @Column(name="min_rate",precision=9,scale=6) private BigDecimal minRate;
    @Column(name="max_rate",precision=9,scale=6) private BigDecimal maxRate;
    @Column(name="effective_date",nullable=false) private LocalDate effectiveDate;
    @Column(name="expiry_date") private LocalDate expiryDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private ProductStatus status;
    @Column(name="approval_status",nullable=false,length=20) private String approvalStatus;
    @Column(name="created_at",nullable=false,updatable=false) private OffsetDateTime createdAt;
    @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductRole> roles=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductBalanceType> balanceTypes=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductTransactionType> transactionTypes=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductFeeRate> feeRates=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductTermRule> termRules=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<ProductBusinessRule> businessRules=new ArrayList<>();
    @OneToMany(mappedBy="product",cascade=CascadeType.ALL,orphanRemoval=true) private List<CustomerCommunication> communications=new ArrayList<>();
    @PrePersist void timestamps(){createdAt=OffsetDateTime.now();updatedAt=createdAt;} @PreUpdate void updated(){updatedAt=OffsetDateTime.now();}
}
