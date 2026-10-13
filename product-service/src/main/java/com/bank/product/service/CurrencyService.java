package com.bank.product.service;

import com.bank.product.dto.CurrencyDtos.*;
import com.bank.product.dto.RecordPage;
import com.bank.product.exception.*;
import com.bank.product.model.Currency;
import com.bank.product.repository.CurrencyRepository;
import com.bank.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service @RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencies;
    private final ProductRepository products;

    @Transactional(readOnly = true)
    public RecordPage<CurrencyResponse> list(int limit, int offset) {
        return RecordPage.of(currencies.findAll().stream()
                .sorted(Comparator.comparing(Currency::getCurrencyCode)).map(this::response).toList(), limit, offset);
    }

    @Transactional(readOnly = true)
    public CurrencyResponse get(String code) { return response(find(code)); }

    @Transactional
    public CurrencyResponse create(CurrencyRequest request, String user) {
        String code = SubmissionRules.code(request.currencyCode());
        if (currencies.existsById(code)) throw new ConflictException("CURRENCY_CODE already exists: " + code);
        Currency currency = new Currency();
        currency.setCurrencyCode(code);
        apply(currency, request, user);
        return response(currencies.saveAndFlush(currency));
    }

    @Transactional
    public CurrencyResponse update(String code, CurrencyRequest request, String user) {
        SubmissionRules.sameId(code, request.currencyCode(), "CURRENCY_CODE");
        Currency currency = find(code);
        apply(currency, request, user);
        return response(currencies.saveAndFlush(currency));
    }

    @Transactional
    public void delete(String code) {
        Currency currency = find(code);
        if (products.existsByCurrency(currency.getCurrencyCode()))
            throw new ConflictException("Currency is used by a product: " + currency.getCurrencyCode());
        currencies.delete(currency);
    }

    private void apply(Currency currency, CurrencyRequest request, String user) {
        currency.setCurrencyName(request.currencyName().trim());
        currency.setCurrencySymbol(request.currencySymbol());
        currency.setDecimalPlaces(request.decimalPlaces());
        currency.setActive(request.active() == null || request.active());
        currency.setAuditUserId(user);
    }

    private Currency find(String code) {
        return currencies.findById(SubmissionRules.code(code))
                .orElseThrow(() -> new NotFoundException("Currency not found: " + code));
    }

    private CurrencyResponse response(Currency c) {
        return new CurrencyResponse(c.getCurrencyCode(), c.getCurrencyName(), c.getCurrencySymbol(),
                c.getDecimalPlaces(), c.isActive(), c.getAuditEffectiveDate(), c.getAuditCrud(),
                c.getAuditUserId(), c.getAuditWorkstationId(), c.getAuditProgramId(),
                c.getAuditHostTimestamp(), c.getAuditLocalTimestamp(), c.getAuditAcceptanceTimestamp(),
                c.getAuditAcceptanceOffsetTimestamp(), c.getAuditUuid());
    }
}
