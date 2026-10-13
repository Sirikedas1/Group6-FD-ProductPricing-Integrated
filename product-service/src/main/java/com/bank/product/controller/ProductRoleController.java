package com.bank.product.controller;

import com.bank.product.dto.ProductRoleDtos.ProductRoleRequest;
import com.bank.product.dto.ProductRoleDtos.ProductRoleResponse;
import com.bank.product.service.ProductRoleService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-role")
@RequiredArgsConstructor
public class ProductRoleController {
    private final ProductRoleService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    public com.bank.product.dto.RecordPage<ProductRoleResponse> list(
            @RequestParam(defaultValue = "50") int limit, @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Create a ProductRole record")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Record created"), @ApiResponse(responseCode = "400", description = "Validation error"), @ApiResponse(responseCode = "409", description = "Duplicate role")})
    public ResponseEntity<ProductRoleResponse> create(@Valid @RequestBody ProductRoleRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, authentication.getName()));
    }

    @GetMapping("/{ROLE_ID}")
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Get a ProductRole record by ROLE_ID")
    public ProductRoleResponse get(@PathVariable("ROLE_ID") String roleId) {
        return service.get(roleId);
    }

    @PutMapping("/{ROLE_ID}")
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Update a ProductRole record")
    public ProductRoleResponse update(@PathVariable("ROLE_ID") String roleId, @Valid @RequestBody ProductRoleRequest request, Authentication authentication) {
        return service.update(roleId, request, authentication.getName());
    }

    @DeleteMapping("/{ROLE_ID}")
    @PreAuthorize("hasAnyRole('BANK_OFFICER','ADMIN')")
    @Operation(summary = "Delete a ProductRole record")
    public ResponseEntity<Void> delete(@PathVariable("ROLE_ID") String roleId) {
        service.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}
