package com.bank.product.dto;

import java.util.List;

public record RecordPage<T>(List<T> items, int limit, int offset, long total) {
    public static <T> RecordPage<T> of(List<T> records, int limit, int offset) {
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 50 : limit, 500));
        int safeOffset = Math.max(0, offset);
        int start = Math.min(safeOffset, records.size());
        int end = Math.min(start + safeLimit, records.size());
        return new RecordPage<>(records.subList(start, end), safeLimit, safeOffset, records.size());
    }
}
