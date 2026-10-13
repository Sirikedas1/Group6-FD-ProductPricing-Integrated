package com.bank.product.service;

import com.bank.product.dto.ProductRateDtos.ProductRateRequest;
import com.bank.product.model.Product;
import com.bank.product.model.ProductRate;
import com.bank.product.exception.ConflictException;
import com.bank.product.repository.CustomerCategoryRepository;
import com.bank.product.repository.ProductRateRepository;
import com.bank.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRateServiceTest {
    @Mock ProductRateRepository rates;
    @Mock ProductRepository products;
    @Mock CustomerCategoryRepository categories;

    @Test
    void calculatesEffectiveRateWhenItIsOmitted() {
        Product product = new Product();
        product.setProductCode("FD001");
        when(rates.existsById("RATE1")).thenReturn(false);
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        when(rates.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ProductRateService service = new ProductRateService(rates, products, categories);

        var result = service.create(new ProductRateRequest(
                "RATE1", "FD001", null, 12, 24, null, null,
                new BigDecimal("6.50"), new BigDecimal("0.25"), null, null, null), "admin");

        assertEquals(new BigDecimal("6.75"), result.effectiveRate());
        assertEquals("FD001", result.productCode());
    }

    @Test
    void rejectsOverlappingBandsForTheSameCategory() {
        Product product = new Product();
        product.setProductCode("FD001");
        ProductRate existing = new ProductRate();
        existing.setRateId("RATE1");
        existing.setMinTenureMonths(12);
        existing.setMaxTenureMonths(24);
        when(rates.existsById("RATE2")).thenReturn(false);
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        when(rates.findByProduct_ProductCode("FD001")).thenReturn(List.of(existing));

        ProductRateService service = new ProductRateService(rates, products, categories);
        var request = new ProductRateRequest("RATE2", "FD001", null, 18, 36,
                null, null, new BigDecimal("6.50"), BigDecimal.ZERO, null, null, null);

        assertThrows(ConflictException.class, () -> service.create(request, "admin"));
    }
}
