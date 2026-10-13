package com.banklab.calculator.exception;

import com.banklab.calculator.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Catches every error and turns it into the same friendly JSON shape:
 * { "code": "...", "message": "...", "details": ["...", "..."] }
 * so the Angular page can always show a clear message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Our own errors (validation, product not found, service down, ...). */
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> handleCalculation(CalculationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(ex.getCode(), ex.getMessage(), ex.getDetails()));
    }

    /** Errors from annotations such as @NotNull on the request classes. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.add(fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Please correct the highlighted values.", details));
    }

    /** Body missing or not valid JSON, e.g. "principal": "abc". */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        String msg = "The request body is missing or has a wrong format "
                + "(for example text where a number is expected, or a decimal term value).";
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_JSON", msg, List.of(msg)));
    }

    /** e.g. sending GET to an address that only accepts POST. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("METHOD_NOT_ALLOWED", ex.getMessage(), List.of()));
    }

    /** Unknown address. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", "No such address: /" + ex.getResourcePath(), List.of()));
    }

    /** Anything unexpected: log the full error for the developer, show a short message to the user. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR",
                        "Something went wrong on the server: " + ex.getMessage(), List.of()));
    }
}
