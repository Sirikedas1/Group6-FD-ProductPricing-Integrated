package com.bank.product.service;

import com.bank.product.exception.BadRequestException;
import java.time.LocalDate;
import java.util.Locale;

final class SubmissionRules {
    private SubmissionRules() {}

    static String code(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    static String optionalCode(String value) {
        return value == null || value.isBlank() ? null : code(value);
    }

    static void sameId(String path, String body, String name) {
        if (!code(path).equals(code(body))) throw new BadRequestException(name + " in path and body must match");
    }

    static void dates(LocalDate effective, LocalDate expiry) {
        if (effective != null && expiry != null && expiry.isBefore(effective))
            throw new BadRequestException("EXPIRY_DATE cannot precede EFFECTIVE_DATE");
    }
}
