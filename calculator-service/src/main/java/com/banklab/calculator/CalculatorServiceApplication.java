package com.banklab.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the FD Calculator backend.
 * Running this class starts a small web server on port 8082.
 */
@SpringBootApplication
public class CalculatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculatorServiceApplication.class, args);
    }
}
