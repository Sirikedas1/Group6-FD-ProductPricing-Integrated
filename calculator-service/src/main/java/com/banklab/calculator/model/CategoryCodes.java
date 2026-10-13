package com.banklab.calculator.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns category names typed in different ways into one standard code.
 * The manual writes "Senior Citizen" in Lab L6 but "SENIOR" in Lab L10/L11,
 * so both must mean the same thing.
 */
public final class CategoryCodes {

    private static final Map<String, String> ALIASES = Map.of(
            "SENIOR_CITIZEN", "SENIOR",
            "SR_CITIZEN", "SENIOR",
            "SUPER_SENIOR_CITIZEN", "SUPER_SENIOR",
            "STAFF", "EMPLOYEE",
            "BANK_EMPLOYEE", "EMPLOYEE",
            "PREMIUM", "PREMIUM_USER",
            "REGULAR", "GENERAL",
            "NORMAL", "GENERAL");

    private CategoryCodes() {
    }

    /** "Senior Citizen" becomes "SENIOR", "premium user" becomes "PREMIUM_USER". */
    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String t = raw.trim().toUpperCase(Locale.ROOT).replaceAll("[\\s\\-]+", "_");
        return ALIASES.getOrDefault(t, t);
    }

    /** Normalises a whole list, removing blanks and duplicates but keeping the order. */
    public static List<String> normalizeAll(List<String> raw) {
        Set<String> result = new LinkedHashSet<>();
        if (raw != null) {
            for (String r : raw) {
                String code = normalize(r);
                if (code != null) {
                    result.add(code);
                }
            }
        }
        return new ArrayList<>(result);
    }
}
