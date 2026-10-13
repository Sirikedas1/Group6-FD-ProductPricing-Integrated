package com.banklab.calculator.model;

import java.util.Locale;

/**
 * Units in which an FD term (tenure) can be entered.
 * unitsPerYear converts the term into years: 12 MONTHS = 12/12 = 1 year.
 * min and max are the allowed term range for that unit
 * (7 days to 10 years, the usual range for Indian bank FDs).
 */
public enum TermUnit {
    DAYS(365, 7, 3650),
    MONTHS(12, 1, 120),
    YEARS(1, 1, 10);

    private final int unitsPerYear;
    private final int min;
    private final int max;

    TermUnit(int unitsPerYear, int min, int max) {
        this.unitsPerYear = unitsPerYear;
        this.min = min;
        this.max = max;
    }

    public int getUnitsPerYear() {
        return unitsPerYear;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    /** One-letter code used in the L11 CSV report, e.g. "12M". */
    public String shortCode() {
        return name().substring(0, 1);
    }

    /** Whole months in the term (used to find the right row in a rate matrix). */
    public int toWholeMonths(int value) {
        return (int) ((long) value * 12 / unitsPerYear);
    }

    /**
     * Converts text such as "months", "Month" or "M" into a TermUnit.
     * Returns null when the text is not recognised.
     */
    public static TermUnit parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String t = text.trim().toUpperCase(Locale.ROOT);
        switch (t) {
            case "DAYS", "DAY", "D":
                return DAYS;
            case "MONTHS", "MONTH", "M":
                return MONTHS;
            case "YEARS", "YEAR", "Y", "YR", "YRS":
                return YEARS;
            default:
                return null;
        }
    }
}
