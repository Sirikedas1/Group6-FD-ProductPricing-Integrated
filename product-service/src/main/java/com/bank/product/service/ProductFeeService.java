package com.bank.product.service;

import com.bank.product.dto.ProductFeeDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.exception.*;
import com.bank.product.model.*;
import com.bank.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;

@Service @RequiredArgsConstructor
public class ProductFeeService {
    private final ProductFeeRepository fees;
    private final ProductRepository products;
    private final CustomerCategoryRepository categories;

    @Transactional(readOnly = true)
    public RecordPage<ProductFeeResponse> list(int limit, int offset) {
        return RecordPage.of(fees.findAll().stream()
                .sorted(Comparator.comparing(ProductFeeRate::getFeeRateId)).map(this::response).toList(), limit, offset);
    }

    @Transactional(readOnly = true)
    public ProductFeeResponse get(String id) { return response(find(id)); }

    @Transactional
    public ProductFeeResponse create(ProductFeeRequest request, String user) {
        String id = SubmissionRules.code(request.feeRateId());
        if (fees.existsById(id)) throw new ConflictException("FEE_RATE_ID already exists: " + id);
        ProductFeeRate fee = new ProductFeeRate();
        fee.setFeeRateId(id);
        apply(fee, request, user);
        return response(fees.saveAndFlush(fee));
    }

    @Transactional
    public ProductFeeResponse update(String id, ProductFeeRequest request, String user) {
        SubmissionRules.sameId(id, request.feeRateId(), "FEE_RATE_ID");
        ProductFeeRate fee = find(id);
        apply(fee, request, user);
        return response(fees.saveAndFlush(fee));
    }

    @Transactional
    public void delete(String id) { fees.delete(find(id)); }

    private void apply(ProductFeeRate fee, ProductFeeRequest request, String user) {
        SubmissionRules.dates(request.effectiveDate(), request.expiryDate());
        if ((request.feeAmount() == null) == (request.feePercent() == null))
            throw new BadRequestException("Set exactly one of FEE_AMOUNT or FEE_PERCENT");
        String productCode = SubmissionRules.code(request.productCode());
        Product product = products.findAggregate(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productCode));
        String categoryCode = SubmissionRules.optionalCode(request.categoryCode());
        CustomerCategory category = categoryCode == null ? null : categories.findById(categoryCode)
                .orElseThrow(() -> new BadRequestException("Unknown CATEGORY_CODE: " + categoryCode));
        fee.setProduct(product);
        fee.setCategory(category);
        fee.setFeeType(SubmissionRules.code(request.feeType()));
        fee.setFeeName(request.feeName().trim());
        fee.setFeeAmount(request.feeAmount());
        fee.setFeePercent(request.feePercent());
        fee.setTriggerEvent(SubmissionRules.optionalCode(request.triggerEvent()));
        fee.setEffectiveDate(request.effectiveDate() == null ? LocalDate.now() : request.effectiveDate());
        fee.setExpiryDate(request.expiryDate());
        fee.setAuditUserId(user);
    }

    private ProductFeeRate find(String id) {
        return fees.findById(SubmissionRules.code(id))
                .orElseThrow(() -> new NotFoundException("Product fee not found: " + id));
    }

    private ProductFeeResponse response(ProductFeeRate fee) {
        return new ProductFeeResponse(fee.getFeeRateId(), fee.getProduct().getProductCode(),
                fee.getCategory() == null ? null : fee.getCategory().getCategoryCode(), fee.getFeeType(), fee.getFeeName(),
                fee.getFeeAmount(), fee.getFeePercent(), fee.getTriggerEvent(), fee.getEffectiveDate(), fee.getExpiryDate(),
                fee.getAuditEffectiveDate(), fee.getAuditCrud(), fee.getAuditUserId(), fee.getAuditWorkstationId(),
                fee.getAuditProgramId(), fee.getAuditHostTimestamp(), fee.getAuditLocalTimestamp(),
                fee.getAuditAcceptanceTimestamp(), fee.getAuditAcceptanceOffsetTimestamp(), fee.getAuditUuid());
    }
}
