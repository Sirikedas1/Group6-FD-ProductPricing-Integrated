package com.banklab.calculator.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Our own error type. Whenever something is wrong (bad input, missing product,
 * a service that is down) we throw this, and GlobalExceptionHandler turns it
 * into a friendly JSON answer with the right HTTP status code.
 */
public class CalculationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<String> details;

    public CalculationException(HttpStatus status, String code, String message, List<String> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    /** 400 - one or more input values are wrong. */
    public static CalculationException validation(List<String> details) {
        return new CalculationException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Please correct the highlighted values.", details);
    }

    /** 400 - a single input problem. */
    public static CalculationException badRequest(String code, String message) {
        return new CalculationException(HttpStatus.BAD_REQUEST, code, message, List.of(message));
    }

    /** 401 - not logged in / wrong password. */
    public static CalculationException unauthorized(String code, String message) {
        return new CalculationException(HttpStatus.UNAUTHORIZED, code, message, List.of());
    }

    /** 404 - something that was asked for does not exist. */
    public static CalculationException notFound(String code, String message) {
        return new CalculationException(HttpStatus.NOT_FOUND, code, message, List.of());
    }

    /** 422 - the input is understood but breaks a business rule (e.g. product not active). */
    public static CalculationException unprocessable(String code, String message) {
        return new CalculationException(HttpStatus.UNPROCESSABLE_ENTITY, code, message, List.of(message));
    }

    /** 502 - another service answered with an error. */
    public static CalculationException badGateway(String code, String message) {
        return new CalculationException(HttpStatus.BAD_GATEWAY, code, message, List.of());
    }

    /** 503 - another service (product/customer) is not reachable. */
    public static CalculationException serviceUnavailable(String code, String message) {
        return new CalculationException(HttpStatus.SERVICE_UNAVAILABLE, code, message, List.of());
    }

    /** 500 - something failed inside this service. */
    public static CalculationException internal(String code, String message, List<String> details) {
        return new CalculationException(HttpStatus.INTERNAL_SERVER_ERROR, code, message, details);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<String> getDetails() {
        return details;
    }
}
