package com.bank.product.dto;

import java.util.List;

public record FirstSubmissionErrorResponse(String code, String message, List<String> details, String traceId) {}
