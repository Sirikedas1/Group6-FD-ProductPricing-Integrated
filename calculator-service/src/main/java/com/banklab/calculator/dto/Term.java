package com.banklab.calculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * FD term exactly as written in Lab L6: "term": { "value": 12, "unit": "months" }
 */
public class Term {

    @NotNull(message = "Term value is required")
    @Positive(message = "Term value must be greater than 0")
    private Integer value;

    @NotBlank(message = "Term unit is required: DAYS, MONTHS or YEARS")
    private String unit;

    public Term() {
    }

    public Term(Integer value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
