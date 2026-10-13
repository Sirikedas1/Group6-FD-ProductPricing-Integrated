package com.bank.product.controller;

import com.bank.product.dto.CurrencyDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/currencies") @RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService service;

    @GetMapping
    public RecordPage<CurrencyResponse> list(@RequestParam(defaultValue = "50") int limit,
                                             @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @GetMapping("/{CURRENCY_CODE}")
    public CurrencyResponse get(@PathVariable("CURRENCY_CODE") String code) { return service.get(code); }

    @PutMapping("/{CURRENCY_CODE}") @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public CurrencyResponse update(@PathVariable("CURRENCY_CODE") String code,
                                   @Valid @RequestBody CurrencyRequest request, Authentication auth) {
        return service.update(code, request, auth.getName());
    }

    @DeleteMapping("/{CURRENCY_CODE}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("CURRENCY_CODE") String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
