package com.bank.product.service;

import com.bank.product.exception.BadRequestException;
import com.bank.product.model.*;
import com.bank.product.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingQuoteServiceTest {
    @Mock ProductRepository products;
    @Mock ProductRateRepository rates;
    @Mock ProductFeeRepository fees;
    @Mock CustomerCategoryRepository categories;

    @Test
    void selectsCategoryRateWithinAmountTenureAndDate() {
        Product product = activeProduct();
        CustomerCategory senior = new CustomerCategory();
        senior.setCategoryCode("SENIOR_CITIZEN");
        ProductRate generic = rate("GENERAL", null, "6.50", "0.00");
        ProductRate specific = rate("SENIOR", senior, "6.50", "0.50");
        specific.setMinTenureMonths(12);
        specific.setMaxTenureMonths(24);
        specific.setMinAmount(new BigDecimal("1000"));
        specific.setMaxAmount(new BigDecimal("100000"));
        specific.setEffectiveDate(LocalDate.now().minusDays(1));
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        when(categories.existsById("SENIOR_CITIZEN")).thenReturn(true);
        when(rates.findByProduct_ProductCode("FD001")).thenReturn(List.of(generic, specific));

        var quote = new PricingQuoteService(products, rates, fees, categories)
                .quote("fd001", "senior_citizen", new BigDecimal("5000"), 12, LocalDate.now());

        assertEquals("SENIOR", quote.rateId());
        assertEquals(0, quote.effectiveRate().compareTo(new BigDecimal("7.00")));
    }

    @Test
    void blocksQuoteForDraftProduct() {
        Product product = activeProduct();
        product.setStatus(ProductStatus.DRAFT);
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> new PricingQuoteService(products, rates, fees, categories)
                .quote("FD001", null, new BigDecimal("5000"), 12, LocalDate.now()));
        verifyNoInteractions(rates);
    }

    private Product activeProduct() {
        Product p = new Product();
        p.setProductCode("FD001");
        p.setStatus(ProductStatus.ACTIVE);
        p.setEffectiveDate(LocalDate.now().minusDays(10));
        p.setMinAmount(new BigDecimal("1000"));
        p.setMaxAmount(new BigDecimal("100000"));
        p.setMaxRate(new BigDecimal("7.50"));
        return p;
    }

    private ProductRate rate(String id, CustomerCategory category, String base, String extra) {
        ProductRate r = new ProductRate();
        r.setRateId(id);
        r.setCategory(category);
        r.setBaseRate(new BigDecimal(base));
        r.setAdditionalRatePercent(new BigDecimal(extra));
        r.setEffectiveRate(r.getBaseRate().add(r.getAdditionalRatePercent()));
        return r;
    }
}
