package com.banklab.calculator.model;

import java.util.Locale;

/**
 * How often interest is added to the balance in compound interest.
 * periodsPerYear is the "n" in the formula M = P x (1 + r/n)^(n x t).
 */
public enum CompoundingFrequency {
    MONTHLY(12),
    QUARTERLY(4),
    HALF_YEARLY(2),
    ANNUALLY(1);

    private final int periodsPerYear;

    CompoundingFrequency(int periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
    }

    public int getPeriodsPerYear() {
        return periodsPerYear;
    }

    /**
     * Converts text such as "quarterly" or "yearly" into a frequency.
     * If nothing is sent we use QUARTERLY (the usual Indian bank practice).
     * Returns null when the text is not recognised.
     */
    public static CompoundingFrequency parse(String text) {
        if (text == null || text.isBlank()) {
            return QUARTERLY;
        }
        String t = text.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        switch (t) {
            case "MONTHLY", "MONTH":
                return MONTHLY;
            case "QUARTERLY", "QUARTER":
                return QUARTERLY;
            case "HALF_YEARLY", "HALFYEARLY", "SEMI_ANNUALLY", "SEMI_ANNUAL":
                return HALF_YEARLY;
            case "ANNUALLY", "ANNUAL", "YEARLY", "YEAR":
                return ANNUALLY;
            default:
                return null;
        }
    }
}
