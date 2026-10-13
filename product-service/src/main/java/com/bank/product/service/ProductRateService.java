package com.bank.product.service;

import com.bank.product.dto.ProductRateDtos.ProductRateRequest;
import com.bank.product.dto.ProductRateDtos.ProductRateResponse;
import com.bank.product.exception.BadRequestException;
import com.bank.product.exception.ConflictException;
import com.bank.product.exception.NotFoundException;
import com.bank.product.model.CustomerCategory;
import com.bank.product.model.Product;
import com.bank.product.model.ProductRate;
import com.bank.product.repository.CustomerCategoryRepository;
import com.bank.product.repository.ProductRateRepository;
import com.bank.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductRateService {
    private final ProductRateRepository rates;
    private final ProductRepository products;
    private final CustomerCategoryRepository categories;

    @Transactional(readOnly = true)
    public com.bank.product.dto.RecordPage<ProductRateResponse> list(int limit, int offset) {
        return com.bank.product.dto.RecordPage.of(rates.findAll().stream()
                .sorted(java.util.Comparator.comparing(ProductRate::getRateId))
                .map(this::response).toList(), limit, offset);
    }

    @Transactional
    public ProductRateResponse create(ProductRateRequest request, String user) {
        String id = normalize(request.rateId());
        if (rates.existsById(id)) throw new ConflictException("RATE_ID already exists: " + id);
        ProductRate rate = new ProductRate();
        apply(rate, request, user);
        return response(rates.save(rate));
    }

    @Transactional(readOnly = true)
    public ProductRateResponse get(String id) {
        return response(find(id));
    }

    @Transactional
    public ProductRateResponse update(String id, ProductRateRequest request, String user) {
        String pathId = normalize(id);
        if (!pathId.equals(normalize(request.rateId()))) throw new BadRequestException("RATE_ID in path and body must match");
        ProductRate rate = find(pathId);
        apply(rate, request, user);
        return response(rates.saveAndFlush(rate));
    }

    @Transactional
    public void delete(String id) { rates.delete(find(id)); }

    @Transactional(readOnly = true)
    public List<ProductRateResponse> byProduct(String productCode) {
        return rates.findByProduct_ProductCodeOrderByMinTenureMonthsAsc(normalize(productCode)).stream().map(this::response).toList();
    }

    private void apply(ProductRate rate, ProductRateRequest request, String user) {
        validate(request);
        Product product = products.findAggregate(normalize(request.productCode()))
                .orElseThrow(() -> new NotFoundException("Product not found: " + request.productCode()));
        CustomerCategory category = request.categoryCode() == null || request.categoryCode().isBlank() ? null : categories.findById(normalize(request.categoryCode()))
                .orElseThrow(() -> new BadRequestException("Unknown CATEGORY_CODE: " + request.categoryCode()));
        BigDecimal additional = request.additionalRatePercent() == null ? BigDecimal.ZERO : request.additionalRatePercent();
        BigDecimal calculated = request.baseRate().add(additional);
        if (request.effectiveRate() != null && request.effectiveRate().compareTo(calculated) != 0)
            throw new BadRequestException("EFFECTIVE_RATE must equal BASE_RATE + ADDITIONAL_RATE_PERCENT");
        if (product.getMinRate() != null && request.baseRate().compareTo(product.getMinRate()) < 0)
            throw new BadRequestException("BASE_RATE is below the product MIN_RATE");
        if (product.getMaxRate() != null && calculated.compareTo(product.getMaxRate()) > 0)
            throw new BadRequestException("EFFECTIVE_RATE exceeds the product MAX_RATE");
        String categoryCode = category == null ? null : category.getCategoryCode();
        for (ProductRate existing : rates.findByProduct_ProductCode(product.getProductCode())) {
            if (existing.getRateId().equals(normalize(request.rateId()))) continue;
            String existingCategory = existing.getCategory() == null ? null : existing.getCategory().getCategoryCode();
            if (java.util.Objects.equals(existingCategory, categoryCode)
                    && overlaps(existing.getMinTenureMonths(), existing.getMaxTenureMonths(), request.minTenureMonths(), request.maxTenureMonths())
                    && overlaps(existing.getMinAmount(), existing.getMaxAmount(), request.minAmount(), request.maxAmount())
                    && overlaps(existing.getEffectiveDate(), existing.getExpiryDate(), request.effectiveDate(), request.expiryDate()))
                throw new ConflictException("Overlapping rate matrix row: " + existing.getRateId());
        }
        rate.setRateId(normalize(request.rateId()));
        rate.setProduct(product);
        rate.setCategory(category);
        rate.setMinTenureMonths(request.minTenureMonths());
        rate.setMaxTenureMonths(request.maxTenureMonths());
        rate.setMinAmount(request.minAmount());
        rate.setMaxAmount(request.maxAmount());
        rate.setBaseRate(request.baseRate());
        rate.setAdditionalRatePercent(request.additionalRatePercent());
        rate.setEffectiveRate(calculated);
        rate.setEffectiveDate(request.effectiveDate());
        rate.setExpiryDate(request.expiryDate());
        rate.setAuditUserId(user);
    }

    private void validate(ProductRateRequest request) {
        if (request.minTenureMonths() != null && request.maxTenureMonths() != null && request.minTenureMonths() > request.maxTenureMonths()) throw new BadRequestException("MIN_TENURE_MONTHS cannot exceed MAX_TENURE_MONTHS");
        if (request.minAmount() != null && request.maxAmount() != null && request.minAmount().compareTo(request.maxAmount()) > 0) throw new BadRequestException("MIN_AMOUNT cannot exceed MAX_AMOUNT");
        if (request.expiryDate() != null && request.effectiveDate() != null && request.expiryDate().isBefore(request.effectiveDate())) throw new BadRequestException("EXPIRY_DATE cannot precede EFFECTIVE_DATE");
    }

    private static <T extends Comparable<T>> boolean overlaps(T firstMin, T firstMax, T secondMin, T secondMax) {
        return (firstMax == null || secondMin == null || firstMax.compareTo(secondMin) >= 0)
                && (secondMax == null || firstMin == null || secondMax.compareTo(firstMin) >= 0);
    }

    private ProductRate find(String id) { return rates.findById(normalize(id)).orElseThrow(() -> new NotFoundException("Product rate not found: " + id)); }

    private ProductRateResponse response(ProductRate rate) {
        return new ProductRateResponse(rate.getRateId(), rate.getProduct().getProductCode(), rate.getCategory() == null ? null : rate.getCategory().getCategoryCode(), rate.getMinTenureMonths(), rate.getMaxTenureMonths(), rate.getMinAmount(), rate.getMaxAmount(), rate.getBaseRate(), rate.getAdditionalRatePercent(), rate.getEffectiveRate(), rate.getEffectiveDate(), rate.getExpiryDate(), rate.getAuditEffectiveDate(), rate.getAuditCrud(), rate.getAuditUserId(), rate.getAuditWorkstationId(), rate.getAuditProgramId(), rate.getAuditHostTimestamp(), rate.getAuditLocalTimestamp(), rate.getAuditAcceptanceTimestamp(), rate.getAuditAcceptanceOffsetTimestamp(), rate.getAuditUuid());
    }

    private static String normalize(String value) { return value == null ? null : value.trim().toUpperCase(Locale.ROOT); }
}
