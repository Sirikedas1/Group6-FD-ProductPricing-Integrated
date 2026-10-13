package com.bank.product.service;

import com.bank.product.exception.BadRequestException;
import com.bank.product.model.Product;
import com.bank.product.model.ProductStatus;
import com.bank.product.repository.ProductRepository;
import com.bank.product.repository.ProductStatusHistoryRepository;
import com.bank.product.repository.ProductRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductLifecycleServiceTest {
    @Mock ProductRepository products;
    @Mock ProductStatusHistoryRepository history;
    @Mock ProductRateRepository rates;

    @Test
    void recordsValidDraftToActiveTransition() {
        Product product = new Product();
        product.setProductCode("FD001");
        product.setStatus(ProductStatus.DRAFT);
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        when(rates.existsByProduct_ProductCode("FD001")).thenReturn(true);
        when(products.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProductLifecycleService service = new ProductLifecycleService(products, history, rates);

        service.transition("FD001", ProductStatus.ACTIVE, "admin", "Approved for sale");

        verify(history).save(any());
        verify(products).saveAndFlush(product);
    }

    @Test
    void blocksActiveToDraftTransition() {
        Product product = new Product();
        product.setProductCode("FD001");
        product.setStatus(ProductStatus.ACTIVE);
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        ProductLifecycleService service = new ProductLifecycleService(products, history, rates);

        assertThrows(BadRequestException.class, () -> service.transition("FD001", ProductStatus.DRAFT, "admin", null));
        verifyNoInteractions(history);
    }
}
