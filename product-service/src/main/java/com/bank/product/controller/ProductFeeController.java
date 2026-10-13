package com.bank.product.controller;

import com.bank.product.dto.ProductFeeDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.service.ProductFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/product-fee") @RequiredArgsConstructor
public class ProductFeeController {
    private final ProductFeeService service;

    @GetMapping
    public RecordPage<ProductFeeResponse> list(@RequestParam(defaultValue = "50") int limit,
                                               @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public ResponseEntity<ProductFeeResponse> create(@Valid @RequestBody ProductFeeRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @GetMapping("/{FEE_RATE_ID}")
    public ProductFeeResponse get(@PathVariable("FEE_RATE_ID") String id) { return service.get(id); }

    @PutMapping("/{FEE_RATE_ID}") @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public ProductFeeResponse update(@PathVariable("FEE_RATE_ID") String id,
                                     @Valid @RequestBody ProductFeeRequest request, Authentication auth) {
        return service.update(id, request, auth.getName());
    }

    @DeleteMapping("/{FEE_RATE_ID}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("FEE_RATE_ID") String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
