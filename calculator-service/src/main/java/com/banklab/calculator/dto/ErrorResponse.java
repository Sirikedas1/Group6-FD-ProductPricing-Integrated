package com.banklab.calculator.dto;

import java.util.List;

/**
 * Error format used by every error answer of this service.
 * Same shape as your product-service spec: { code, message, details[] }.
 */
public record ErrorResponse(String code, String message, List<String> details) {
}
