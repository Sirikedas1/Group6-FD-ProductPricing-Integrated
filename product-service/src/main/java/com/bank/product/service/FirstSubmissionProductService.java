package com.bank.product.service;

import com.bank.product.dto.FirstSubmissionProductDtos.ProductPage;
import com.bank.product.dto.FirstSubmissionProductDtos.ProductRequest;
import com.bank.product.dto.FirstSubmissionProductDtos.ProductResponse;
import com.bank.product.exception.BadRequestException;
import com.bank.product.exception.ConflictException;
import com.bank.product.exception.NotFoundException;
import com.bank.product.model.Product;
import com.bank.product.model.ProductStatus;
import com.bank.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FirstSubmissionProductService {
    private final ProductRepository products;
    private final com.bank.product.repository.CurrencyRepository currencies;
    private final com.bank.product.repository.ProductStatusHistoryRepository history;
    private final com.bank.product.repository.ProductRateRepository rates;

    @Transactional
    public ProductResponse create(ProductRequest request, String user) {
        if (request.status() != null && !request.status().isBlank()
                && !normalize(request.status()).equals(ProductStatus.DRAFT.name()))
            throw new BadRequestException("New products must start in DRAFT status");
        String code = normalize(request.productCode());
        if (products.existsById(code)) throw new ConflictException("Product code already exists: " + code);
        if (products.existsByProductId(request.productId().trim())) throw new ConflictException("PRODUCT_ID already exists");
        Product product = new Product();
        apply(product, request, user);
        Product saved = products.saveAndFlush(product);
        com.bank.product.model.ProductStatusHistory entry = new com.bank.product.model.ProductStatusHistory();
        entry.setHistoryId(java.util.UUID.randomUUID().toString());
        entry.setProductCode(saved.getProductCode());
        entry.setNewStatus(ProductStatus.DRAFT.name());
        entry.setChangedBy(user);
        entry.setChangedAt(java.time.LocalDateTime.now());
        entry.setRemarks("Product created");
        history.save(entry);
        return response(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(String code) {
        return response(find(code));
    }

    @Transactional
    public ProductResponse update(String code, ProductRequest request, String user) {
        String pathCode = normalize(code);
        if (!pathCode.equals(normalize(request.productCode()))) {
            throw new BadRequestException("PRODUCT_CODE in path and body must match");
        }
        Product product = find(pathCode);
        if (products.existsByProductIdAndProductCodeNot(request.productId().trim(), pathCode))
            throw new ConflictException("PRODUCT_ID already exists");
        if (request.status() != null && !request.status().isBlank()
                && !normalize(request.status()).equals(product.getStatus().name()))
            throw new BadRequestException("Change STATUS through the lifecycle endpoint");
        apply(product, request, user);
        return response(products.saveAndFlush(product));
    }

    @Transactional
    public void delete(String code) {
        Product product = find(code);
        if (product.getStatus() != ProductStatus.DRAFT && product.getStatus() != ProductStatus.UNDER_ESTABLISHMENT)
            throw new ConflictException("Only draft products may be deleted");
        rates.deleteAll(rates.findByProduct_ProductCode(product.getProductCode()));
        products.delete(product);
    }

    @Transactional(readOnly = true)
    public ProductPage search(String productType, String status, LocalDate startDate, LocalDate endDate,
                              int limit, int offset, String sortBy, Sort.Direction direction) {
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 50 : limit, 500));
        int safeOffset = Math.max(0, offset);
        String normalizedType = blank(productType);
        String normalizedStatus = blank(status);
        List<Product> filtered = products.findAll().stream()
                .filter(p -> normalizedType == null || normalizedType.equals(p.getProductType()))
                .filter(p -> normalizedStatus == null || normalizedStatus.equals(statusOf(p)))
                .filter(p -> startDate == null || !p.getEffectiveDate().isBefore(startDate))
                .filter(p -> endDate == null || !p.getEffectiveDate().isAfter(endDate))
                .sorted(comparator(sortBy, direction))
                .toList();
        int from = Math.min(safeOffset, filtered.size());
        int to = Math.min(from + safeLimit, filtered.size());
        return new ProductPage(filtered.subList(from, to).stream().map(this::response).toList(), safeLimit, safeOffset, filtered.size());
    }

    private void apply(Product product, ProductRequest request, String user) {
        validate(request);
        product.setProductCode(normalize(request.productCode()));
        product.setProductId(request.productId().trim());
        product.setProductName(request.productName().trim());
        product.setProductType(normalize(request.productType()));
        product.setCurrency(normalize(request.currency()));
        product.setMinAmount(request.minAmount());
        product.setMaxAmount(request.maxAmount());
        product.setMinAge(request.minAge());
        product.setMinRate(request.minRate());
        product.setMaxRate(request.maxRate());
        product.setDefaultCompoundingFrequency(normalizeNullable(request.defaultCompoundingFrequency()));
        product.setDefaultPayoutFrequency(normalizeNullable(request.defaultPayoutFrequency()));
        product.setSupportedFrequencies(request.supportedFrequencies());
        product.setDefaultMaturityInstruction(normalizeNullable(request.defaultMaturityInstruction()));
        product.setTdsRate(request.tdsRate());
        product.setTdsExemptionThreshold(request.tdsExemptionThreshold());
        product.setEffectiveDate(request.effectiveDate() == null ? LocalDate.now() : request.effectiveDate());
        if (product.getStatus() == null) product.setStatus(ProductStatus.DRAFT);
        product.setApprovalStatus(product.getApprovalStatus() == null ? "DRAFT" : product.getApprovalStatus());
        product.setAuditUserId(user);

        // These legacy columns are non-null in the original V1 migration. Keep them populated
        // while the first-submission fields remain the public contract.
        product.setMarketName(product.getMarketName() == null ? product.getProductName() : product.getMarketName());
        product.setProductLine(product.getProductLine() == null ? product.getProductType() : product.getProductLine());
        product.setMinTermMonths(product.getMinTermMonths() == null ? 0 : product.getMinTermMonths());
        product.setMaxTermMonths(product.getMaxTermMonths() == null ? product.getMinTermMonths() : product.getMaxTermMonths());
    }

    private void validate(ProductRequest request) {
        if (request.minAmount() != null && request.maxAmount() != null && request.minAmount().compareTo(request.maxAmount()) > 0) {
            throw new BadRequestException("MIN_AMOUNT cannot exceed MAX_AMOUNT");
        }
        if (request.minRate() != null && request.maxRate() != null && request.minRate().compareTo(request.maxRate()) > 0) {
            throw new BadRequestException("MIN_RATE cannot exceed MAX_RATE");
        }
        if (request.tdsRate() != null && request.tdsRate().compareTo(new BigDecimal("100")) > 0) {
            throw new BadRequestException("TDS_RATE cannot exceed 100");
        }
        if (request.effectiveDate() != null && request.effectiveDate().isAfter(LocalDate.now().plusYears(100))) {
            throw new BadRequestException("EFFECTIVE_DATE is outside the supported range");
        }
        com.bank.product.model.Currency currency = currencies.findById(normalize(request.currency()))
                .orElseThrow(() -> new BadRequestException("Unknown CURRENCY: " + request.currency()));
        if (!currency.isActive()) throw new BadRequestException("CURRENCY is inactive: " + request.currency());
    }

    private Product find(String code) {
        return products.findAggregate(normalize(code))
                .orElseThrow(() -> new NotFoundException("Product not found: " + code));
    }

    private ProductResponse response(Product p) {
        return new ProductResponse(p.getProductCode(), p.getProductId(), p.getProductName(), p.getProductType(), p.getCurrency(),
                p.getMinAmount(), p.getMaxAmount(), p.getMinAge(), p.getMinRate(), p.getMaxRate(), p.getDefaultCompoundingFrequency(),
                p.getDefaultPayoutFrequency(), p.getSupportedFrequencies(), p.getDefaultMaturityInstruction(), p.getTdsRate(),
                p.getTdsExemptionThreshold(), p.getEffectiveDate(), statusOf(p), p.getCreatedAt(), p.getAuditEffectiveDate(),
                p.getAuditCrud(), p.getAuditUserId(), p.getAuditWorkstationId(), p.getAuditProgramId(), p.getAuditHostTimestamp(),
                p.getAuditLocalTimestamp(), p.getAuditAcceptanceTimestamp(), p.getAuditAcceptanceOffsetTimestamp(), p.getAuditUuid());
    }

    private Comparator<Product> comparator(String sortBy, Sort.Direction direction) {
        String key = sortBy == null ? "PRODUCT_CODE" : sortBy.trim().toUpperCase(Locale.ROOT);
        Function<Product, Comparable<?>> getter = switch (key) {
            case "PRODUCT_NAME" -> Product::getProductName;
            case "PRODUCT_TYPE" -> Product::getProductType;
            case "EFFECTIVE_DATE" -> Product::getEffectiveDate;
            case "STATUS" -> FirstSubmissionProductService::statusOf;
            default -> Product::getProductCode;
        };
        Comparator<Product> result = (left, right) -> {
            Comparable<?> leftValue = getter.apply(left);
            Comparable<?> rightValue = getter.apply(right);
            if (leftValue == null && rightValue == null) return 0;
            if (leftValue == null) return 1;
            if (rightValue == null) return -1;
            @SuppressWarnings("unchecked") int compared = ((Comparable<Object>) leftValue).compareTo(rightValue);
            return compared;
        };
        return direction == Sort.Direction.DESC ? result.reversed() : result;
    }

    private static String statusOf(Product p) { return p.getStatus() == null ? null : p.getStatus().name(); }
    private static ProductStatus statusOf(String value, ProductStatus fallback) {
        if (value == null || value.isBlank()) return fallback;
        try { return ProductStatus.valueOf(normalize(value)); }
        catch (IllegalArgumentException ex) { throw new BadRequestException("Unsupported STATUS: " + value); }
    }
    private static BigDecimal defaultRate(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private static String normalize(String value) { return value == null ? null : value.trim().toUpperCase(Locale.ROOT); }
    private static String normalizeNullable(String value) { return value == null ? null : normalize(value); }
    private static String blank(String value) { return value == null || value.isBlank() ? null : normalize(value); }
}
