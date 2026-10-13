package com.bank.product.controller;

import com.bank.product.dto.FirstSubmissionProductDtos.*;
import com.bank.product.model.ProductStatus;
import com.bank.product.model.ProductStatusHistory;
import com.bank.product.service.FirstSubmissionProductService;
import com.bank.product.service.ProductLifecycleService;
import com.bank.product.service.PricingQuoteService;
import com.bank.product.dto.PricingQuoteResponse;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/products", "/api/product"})
@RequiredArgsConstructor
public class FirstSubmissionProductController {
    private final FirstSubmissionProductService products;
    private final ProductLifecycleService lifecycle;
    private final PricingQuoteService pricing;

    @Operation(summary = "List Product records", description = "First-submission product list with limit/offset, filters, sorting, and pagination compatibility.")
    @GetMapping({"", "/search"})
    public ProductPage list(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false, name = "PRODUCT_TYPE") String productType,
            @RequestParam(required = false, name = "STATUS") String status,
            @RequestParam(required = false, name = "EFFECTIVE_DATE_FROM") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, name = "EFFECTIVE_DATE_TO") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "PRODUCT_CODE") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        int actualLimit = size == null ? limit : size;
        int actualOffset = page == null ? offset : Math.max(0, page) * Math.max(1, actualLimit);
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return products.search(productType, status, startDate, endDate, actualLimit, actualOffset, sortBy, direction);
    }

    @Operation(summary = "Create a Product record")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Record created"), @ApiResponse(responseCode = "400", description = "Validation error"), @ApiResponse(responseCode = "409", description = "Duplicate product code")})
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BANK_OFFICER')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(products.create(request, authentication.getName()));
    }

    @Operation(summary = "Get a Product record by PRODUCT_CODE")
    @GetMapping("/{PRODUCT_CODE}")
    public ProductResponse get(@PathVariable("PRODUCT_CODE") String productCode) {
        return products.get(productCode);
    }

    @Operation(summary = "Update a Product record")
    @PutMapping("/{PRODUCT_CODE}")
    @PreAuthorize("hasAnyRole('ADMIN','BANK_OFFICER')")
    public ProductResponse update(@PathVariable("PRODUCT_CODE") String productCode, @Valid @RequestBody ProductRequest request, Authentication authentication) {
        return products.update(productCode, request, authentication.getName());
    }

    @Operation(summary = "Delete a Product record")
    @DeleteMapping("/{PRODUCT_CODE}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("PRODUCT_CODE") String productCode) {
        products.delete(productCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change product lifecycle status", description = "Allowed transitions include DRAFT -> ACTIVE -> WITHDRAWN.")
    @PatchMapping("/{PRODUCT_CODE}/status")
    @PreAuthorize("hasAnyRole('ADMIN','BANK_OFFICER')")
    public ProductResponse changeStatus(@PathVariable("PRODUCT_CODE") String productCode, @RequestParam("STATUS") ProductStatus status,
                                        @RequestParam(required = false, name = "REMARKS") String remarks, Authentication authentication) {
        return products.get(lifecycle.transition(productCode, status, authentication.getName(), remarks).getProductCode());
    }

    @Operation(summary = "List product status history")
    @GetMapping("/{PRODUCT_CODE}/status-history")
    public List<StatusHistoryResponse> history(@PathVariable("PRODUCT_CODE") String productCode) {
        return lifecycle.history(productCode).stream().map(this::historyResponse).toList();
    }

    @GetMapping("/{PRODUCT_CODE}/pricing")
    @Operation(summary = "Resolve the active rate matrix row and fees for an FD opening")
    public PricingQuoteResponse pricing(@PathVariable("PRODUCT_CODE") String productCode,
                                        @RequestParam(required = false, name = "CATEGORY_CODE") String categoryCode,
                                        @RequestParam(name = "AMOUNT") BigDecimal amount,
                                        @RequestParam(name = "TENURE_MONTHS") int tenureMonths,
                                        @RequestParam(required = false, name = "AS_OF")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        return pricing.quote(productCode, categoryCode, amount, tenureMonths, asOf);
    }

    private StatusHistoryResponse historyResponse(ProductStatusHistory row) {
        return new StatusHistoryResponse(row.getHistoryId(), row.getProductCode(), row.getPreviousStatus(), row.getNewStatus(), row.getChangedBy(), row.getChangedAt(), row.getRemarks());
    }
}
