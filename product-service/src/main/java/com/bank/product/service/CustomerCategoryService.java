package com.bank.product.service;

import com.bank.product.dto.CustomerCategoryDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.exception.*;
import com.bank.product.model.CustomerCategory;
import com.bank.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service @RequiredArgsConstructor
public class CustomerCategoryService {
    private final CustomerCategoryRepository categories;
    private final ProductRateRepository rates;
    private final ProductFeeRepository fees;
    private final CustomerCommunicationRepository communications;

    @Transactional(readOnly = true)
    public RecordPage<CustomerCategoryResponse> list(int limit, int offset) {
        return RecordPage.of(categories.findAll().stream()
                .sorted(Comparator.comparing(CustomerCategory::getCategoryCode)).map(this::response).toList(), limit, offset);
    }

    @Transactional(readOnly = true)
    public CustomerCategoryResponse get(String code) { return response(find(code)); }

    @Transactional
    public CustomerCategoryResponse create(CustomerCategoryRequest request, String user) {
        String code = SubmissionRules.code(request.categoryCode());
        if (categories.existsById(code)) throw new ConflictException("CATEGORY_CODE already exists: " + code);
        CustomerCategory category = new CustomerCategory();
        category.setCategoryCode(code);
        apply(category, request, user);
        return response(categories.saveAndFlush(category));
    }

    @Transactional
    public CustomerCategoryResponse update(String code, CustomerCategoryRequest request, String user) {
        SubmissionRules.sameId(code, request.categoryCode(), "CATEGORY_CODE");
        CustomerCategory category = find(code);
        apply(category, request, user);
        return response(categories.saveAndFlush(category));
    }

    @Transactional
    public void delete(String code) {
        CustomerCategory category = find(code);
        String key = category.getCategoryCode();
        if (rates.existsByCategory_CategoryCode(key) || fees.existsByCategory_CategoryCode(key)
                || communications.existsByCategory_CategoryCode(key))
            throw new ConflictException("Customer category is used by product pricing: " + key);
        categories.delete(category);
    }

    private void apply(CustomerCategory category, CustomerCategoryRequest request, String user) {
        category.setCategoryName(request.categoryName().trim());
        category.setDescription(request.description());
        category.setAuditUserId(user);
    }

    private CustomerCategory find(String code) {
        return categories.findById(SubmissionRules.code(code))
                .orElseThrow(() -> new NotFoundException("Customer category not found: " + code));
    }

    private CustomerCategoryResponse response(CustomerCategory c) {
        return new CustomerCategoryResponse(c.getCategoryCode(), c.getCategoryName(), c.getDescription(),
                c.getAuditEffectiveDate(), c.getAuditCrud(), c.getAuditUserId(), c.getAuditWorkstationId(),
                c.getAuditProgramId(), c.getAuditHostTimestamp(), c.getAuditLocalTimestamp(),
                c.getAuditAcceptanceTimestamp(), c.getAuditAcceptanceOffsetTimestamp(), c.getAuditUuid());
    }
}
