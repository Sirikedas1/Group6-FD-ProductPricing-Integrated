package com.banklab.calculator.model;

import java.util.Locale;

/**
 * The two interest methods required by Lab L6 and the evaluation plan
 * ("FD Calculator (Simple & Compound)").
 */
public enum InterestType {
    SIMPLE,
    COMPOUND;

    /**
     * Converts text such as "simple", "Compound" or "CI" into an InterestType.
     * If nothing is sent we use SIMPLE, because the manual's sample request
     * (Lab L6 section 3.1) does not contain an interest type.
     * Returns null when the text is not recognised.
     */
    public static InterestType parse(String text) {
        if (text == null || text.isBlank()) {
            return SIMPLE;
        }
        String t = text.trim().toUpperCase(Locale.ROOT);
        switch (t) {
            case "SIMPLE", "SI":
                return SIMPLE;
            case "COMPOUND", "CI":
                return COMPOUND;
            default:
                return null;
        }
    }
}
