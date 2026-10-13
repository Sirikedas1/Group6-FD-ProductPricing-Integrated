package com.bank.product.exception;

import com.bank.product.dto.FirstSubmissionErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {
        com.bank.product.controller.FirstSubmissionProductController.class,
        com.bank.product.controller.ProductRoleController.class,
        com.bank.product.controller.ProductRateController.class,
        com.bank.product.controller.CurrencyController.class,
        com.bank.product.controller.CustomerCategoryResourceController.class,
        com.bank.product.controller.ProductFeeController.class,
        com.bank.product.controller.CustomerCommunicationController.class
})
public class FirstSubmissionExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<FirstSubmissionErrorResponse> notFound(NotFoundException ex) { return body(404, "NOT_FOUND", ex.getMessage(), List.of()); }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<FirstSubmissionErrorResponse> conflict(ConflictException ex) { return body(409, "CONFLICT", ex.getMessage(), List.of()); }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<FirstSubmissionErrorResponse> badRequest(BadRequestException ex) { return body(400, "VALIDATION_ERROR", ex.getMessage(), List.of()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<FirstSubmissionErrorResponse> validation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
        return body(400, "VALIDATION_ERROR", "Request validation failed", details);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<FirstSubmissionErrorResponse> forbidden(AccessDeniedException ex) {
        return body(403, "FORBIDDEN", "Insufficient role", List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<FirstSubmissionErrorResponse> generic(Exception ex) { return body(500, "INTERNAL_ERROR", "Unexpected server error", List.of()); }

    private ResponseEntity<FirstSubmissionErrorResponse> body(int status, String code, String message, List<String> details) {
        return ResponseEntity.status(status).body(new FirstSubmissionErrorResponse(code, message, details, UUID.randomUUID().toString()));
    }
}
