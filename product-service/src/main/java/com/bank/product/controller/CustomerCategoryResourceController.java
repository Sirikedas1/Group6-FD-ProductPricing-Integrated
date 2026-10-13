package com.bank.product.controller;

import com.bank.product.dto.CustomerCategoryDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.service.CustomerCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/customer-category") @RequiredArgsConstructor
public class CustomerCategoryResourceController {
    private final CustomerCategoryService service;

    @GetMapping
    public RecordPage<CustomerCategoryResponse> list(@RequestParam(defaultValue = "50") int limit,
                                                      @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public ResponseEntity<CustomerCategoryResponse> create(@Valid @RequestBody CustomerCategoryRequest request,
                                                           Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @GetMapping("/{CATEGORY_CODE}")
    public CustomerCategoryResponse get(@PathVariable("CATEGORY_CODE") String code) { return service.get(code); }

    @PutMapping("/{CATEGORY_CODE}") @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public CustomerCategoryResponse update(@PathVariable("CATEGORY_CODE") String code,
                                           @Valid @RequestBody CustomerCategoryRequest request, Authentication auth) {
        return service.update(code, request, auth.getName());
    }

    @DeleteMapping("/{CATEGORY_CODE}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("CATEGORY_CODE") String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
