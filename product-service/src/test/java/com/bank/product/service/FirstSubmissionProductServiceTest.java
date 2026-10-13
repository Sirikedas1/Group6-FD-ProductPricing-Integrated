package com.bank.product.service;

import com.bank.product.dto.FirstSubmissionProductDtos.ProductRequest;
import com.bank.product.exception.BadRequestException;
import com.bank.product.model.Product;
import com.bank.product.repository.ProductRepository;
import com.bank.product.repository.CurrencyRepository;
import com.bank.product.repository.ProductStatusHistoryRepository;
import com.bank.product.repository.ProductRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class FirstSubmissionProductServiceTest {
    @Mock ProductRepository products;
    @Mock CurrencyRepository currencies;
    @Mock ProductStatusHistoryRepository history;
    @Mock ProductRateRepository rates;

    @Test
    void rejectsInvertedFirstSubmissionAmountRange() {
        FirstSubmissionProductService service = new FirstSubmissionProductService(products, currencies, history, rates);
        ProductRequest request = request(new BigDecimal("5000"), new BigDecimal("1000"), new BigDecimal("5"), new BigDecimal("7"));
        assertThrows(BadRequestException.class, () -> service.create(request, "admin"));
    }

    @Test
    void rejectsInvertedFirstSubmissionRateRange() {
        FirstSubmissionProductService service = new FirstSubmissionProductService(products, currencies, history, rates);
        ProductRequest request = request(new BigDecimal("1000"), new BigDecimal("5000"), new BigDecimal("8"), new BigDecimal("7"));
        assertThrows(BadRequestException.class, () -> service.create(request, "admin"));
    }

    private ProductRequest request(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal minRate, BigDecimal maxRate) {
        return new ProductRequest("FDTEST", "TEST1", "Test FD", "FD", "INR", minAmount, maxAmount, 18,
                minRate, maxRate, "MONTHLY", "MATURITY", "MONTHLY,MATURITY", "RENEW_PRINCIPAL", new BigDecimal("10"),
                new BigDecimal("40000"), LocalDate.now(), "DRAFT");
    }
}
