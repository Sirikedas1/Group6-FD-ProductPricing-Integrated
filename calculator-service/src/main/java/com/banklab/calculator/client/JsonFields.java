package com.banklab.calculator.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Small helpers for reading JSON from other teams' services.
 * Your product spec uses UPPER_SNAKE names (BASE_RATE) while other code often
 * uses camelCase (baseRate), so every helper accepts several possible names.
 */
public final class JsonFields {

    private JsonFields() {
    }

    public static JsonNode field(JsonNode node, String... names) {
        if (node == null) {
            return null;
        }
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                return value;
            }
        }
        return null;
    }

    public static String text(JsonNode node, String... names) {
        JsonNode v = field(node, names);
        return v == null ? null : v.asText();
    }

    public static BigDecimal decimal(JsonNode node, String... names) {
        JsonNode v = field(node, names);
        if (v == null || v.asText().isBlank()) {
            return null;
        }
        return v.isNumber() ? v.decimalValue() : new BigDecimal(v.asText().trim());
    }

    public static Integer integer(JsonNode node, String... names) {
        BigDecimal d = decimal(node, names);
        return d == null ? null : d.intValue();
    }

    public static LocalDate date(JsonNode node, String... names) {
        String t = text(node, names);
        if (t == null || t.isBlank()) {
            return null;
        }
        return LocalDate.parse(t.length() > 10 ? t.substring(0, 10) : t);
    }

    /** Accepts a plain array, or a wrapper such as { "items": [...] } or { "content": [...] }. */
    public static List<JsonNode> items(JsonNode node) {
        List<JsonNode> list = new ArrayList<>();
        if (node == null) {
            return list;
        }
        JsonNode array = node.isArray() ? node : field(node, "items", "content", "data");
        if (array != null && array.isArray()) {
            array.forEach(list::add);
        }
        return list;
    }
}
