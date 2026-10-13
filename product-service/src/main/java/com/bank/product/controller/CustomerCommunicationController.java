package com.bank.product.controller;

import com.bank.product.dto.CustomerCommunicationDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.service.CustomerCommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/customer-communication") @RequiredArgsConstructor
public class CustomerCommunicationController {
    private final CustomerCommunicationService service;

    @GetMapping
    public RecordPage<CustomerCommunicationResponse> list(@RequestParam(defaultValue = "50") int limit,
                                                            @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public ResponseEntity<CustomerCommunicationResponse> create(@Valid @RequestBody CustomerCommunicationRequest request,
                                                                 Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @GetMapping("/{COMMUNICATION_ID}")
    public CustomerCommunicationResponse get(@PathVariable("COMMUNICATION_ID") String id) { return service.get(id); }

    @PutMapping("/{COMMUNICATION_ID}") @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public CustomerCommunicationResponse update(@PathVariable("COMMUNICATION_ID") String id,
                                                @Valid @RequestBody CustomerCommunicationRequest request, Authentication auth) {
        return service.update(id, request, auth.getName());
    }

    @DeleteMapping("/{COMMUNICATION_ID}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("COMMUNICATION_ID") String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
