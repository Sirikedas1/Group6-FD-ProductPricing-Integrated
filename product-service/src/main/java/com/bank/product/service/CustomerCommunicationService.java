package com.bank.product.service;

import com.bank.product.dto.CustomerCommunicationDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.exception.*;
import com.bank.product.model.*;
import com.bank.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service @RequiredArgsConstructor
public class CustomerCommunicationService {
    private final CustomerCommunicationRepository communications;
    private final ProductRepository products;
    private final CustomerCategoryRepository categories;

    @Transactional(readOnly = true)
    public RecordPage<CustomerCommunicationResponse> list(int limit, int offset) {
        return RecordPage.of(communications.findAll().stream()
                .sorted(Comparator.comparing(CustomerCommunication::getCommunicationId))
                .map(this::response).toList(), limit, offset);
    }

    @Transactional(readOnly = true)
    public CustomerCommunicationResponse get(String id) { return response(find(id)); }

    @Transactional
    public CustomerCommunicationResponse create(CustomerCommunicationRequest request, String user) {
        String id = SubmissionRules.code(request.communicationId());
        if (communications.existsById(id)) throw new ConflictException("COMMUNICATION_ID already exists: " + id);
        CustomerCommunication communication = new CustomerCommunication();
        communication.setCommunicationId(id);
        apply(communication, request, user);
        return response(communications.saveAndFlush(communication));
    }

    @Transactional
    public CustomerCommunicationResponse update(String id, CustomerCommunicationRequest request, String user) {
        SubmissionRules.sameId(id, request.communicationId(), "COMMUNICATION_ID");
        CustomerCommunication communication = find(id);
        apply(communication, request, user);
        return response(communications.saveAndFlush(communication));
    }

    @Transactional
    public void delete(String id) { communications.delete(find(id)); }

    private void apply(CustomerCommunication communication, CustomerCommunicationRequest request, String user) {
        String productCode = SubmissionRules.code(request.productCode());
        String categoryCode = SubmissionRules.code(request.categoryCode());
        communication.setProduct(products.findAggregate(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productCode)));
        communication.setCategory(categories.findById(categoryCode)
                .orElseThrow(() -> new BadRequestException("Unknown CATEGORY_CODE: " + categoryCode)));
        communication.setCommunicationType(SubmissionRules.code(request.communicationType()));
        communication.setChannel(SubmissionRules.code(request.channel()));
        communication.setFrequency(SubmissionRules.code(request.frequency()));
        communication.setTemplateName(request.templateName().trim());
        communication.setActive(request.active() == null || request.active());
        communication.setAuditUserId(user);
    }

    private CustomerCommunication find(String id) {
        return communications.findById(SubmissionRules.code(id))
                .orElseThrow(() -> new NotFoundException("Customer communication not found: " + id));
    }

    private CustomerCommunicationResponse response(CustomerCommunication c) {
        return new CustomerCommunicationResponse(c.getCommunicationId(), c.getProduct().getProductCode(),
                c.getCategory().getCategoryCode(), c.getCommunicationType(), c.getChannel(), c.getFrequency(),
                c.getTemplateName(), c.isActive(), c.getAuditEffectiveDate(), c.getAuditCrud(),
                c.getAuditUserId(), c.getAuditWorkstationId(), c.getAuditProgramId(),
                c.getAuditHostTimestamp(), c.getAuditLocalTimestamp(), c.getAuditAcceptanceTimestamp(),
                c.getAuditAcceptanceOffsetTimestamp(), c.getAuditUuid());
    }
}
