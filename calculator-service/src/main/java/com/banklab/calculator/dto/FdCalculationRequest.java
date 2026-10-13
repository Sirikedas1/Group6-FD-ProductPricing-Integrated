package com.banklab.calculator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * What the frontend sends to POST /api/fd/calculate.
 * The first four fields are exactly the Lab L6 payload:
 *   principal, term, baseRate, categories
 * The other fields are optional extras (interest type, compounding, product).
 */
public class FdCalculationRequest {

    @NotNull(message = "Deposit amount (principal) is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Deposit amount must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Deposit amount can have at most 2 decimal places")
    private BigDecimal principal;

    @NotNull(message = "Term is required (value and unit)")
    @Valid
    private Term term;

    @DecimalMin(value = "0.00", inclusive = false, message = "Base rate must be greater than 0")
    @Digits(integer = 3, fraction = 2, message = "Base rate can have at most 2 decimal places")
    private BigDecimal baseRate;

    private List<String> categories = new ArrayList<>();

    /** SIMPLE or COMPOUND. Empty means SIMPLE (the manual's sample has no interest type). */
    private String interestType;

    /** MONTHLY, QUARTERLY, HALF_YEARLY or ANNUALLY. Used only for COMPOUND. Empty means QUARTERLY. */
    private String compoundingFrequency;

    /** Product code such as FD001. Needed only when rates come from product-service (Phase C). */
    private String productCode;

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(String compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
