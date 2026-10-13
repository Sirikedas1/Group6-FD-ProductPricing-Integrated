package com.bank.product.controller;

import com.bank.product.dto.ProductRateDtos.ProductRateRequest;
import com.bank.product.dto.ProductRateDtos.ProductRateResponse;
import com.bank.product.service.ProductRateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-rate")
@RequiredArgsConstructor
public class ProductRateController {
    private final ProductRateService service;

    @GetMapping
    public com.bank.product.dto.RecordPage<ProductRateResponse> list(
            @RequestParam(defaultValue = "50") int limit, @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Create a ProductRate record")
    public ResponseEntity<ProductRateResponse> create(@Valid @RequestBody ProductRateRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, authentication.getName()));
    }

    @GetMapping("/{RATE_ID}")
    @Operation(summary = "Get a ProductRate record by RATE_ID")
    public ProductRateResponse get(@PathVariable("RATE_ID") String rateId) { return service.get(rateId); }

    @PutMapping("/{RATE_ID}")
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Update a ProductRate record")
    public ProductRateResponse update(@PathVariable("RATE_ID") String rateId, @Valid @RequestBody ProductRateRequest request, Authentication authentication) { return service.update(rateId, request, authentication.getName()); }

    @DeleteMapping("/{RATE_ID}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a ProductRate record")
    public ResponseEntity<Void> delete(@PathVariable("RATE_ID") String rateId) { service.delete(rateId); return ResponseEntity.noContent().build(); }

    @GetMapping("/product/{PRODUCT_CODE}")
    @Operation(summary = "List rate-matrix rows for a product")
    public List<ProductRateResponse> byProduct(@PathVariable("PRODUCT_CODE") String productCode) { return service.byProduct(productCode); }
}
