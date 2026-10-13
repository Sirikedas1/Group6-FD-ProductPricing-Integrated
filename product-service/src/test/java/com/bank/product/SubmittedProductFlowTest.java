package com.bank.product;

import com.bank.product.dto.FirstSubmissionProductDtos.ProductRequest;
import com.bank.product.dto.ProductFeeDtos.ProductFeeRequest;
import com.bank.product.dto.ProductRateDtos.ProductRateRequest;
import com.bank.product.model.ProductStatus;
import com.bank.product.exception.NotFoundException;
import com.bank.product.service.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SubmittedProductFlowTest {
    @Autowired FirstSubmissionProductService products;
    @Autowired ProductRateService rates;
    @Autowired ProductFeeService fees;
    @Autowired ProductLifecycleService lifecycle;
    @Autowired PricingQuoteService pricing;
    @Autowired EntityManager entityManager;

    @Test
    void createsDraftThenActivatesAndQuotesSubmittedRateAndFeeTables() {
        products.create(new ProductRequest("FDNEW", "NEW100", "New Fixed Deposit", "FD", "INR",
                new BigDecimal("1000"), new BigDecimal("100000"), 18,
                new BigDecimal("5.00"), new BigDecimal("7.50"), "MONTHLY", "MATURITY",
                "MONTHLY,MATURITY", "RENEW_PRINCIPAL", new BigDecimal("10"),
                new BigDecimal("40000"), LocalDate.now(), "DRAFT"), "admin");
        assertEquals("DRAFT", products.get("FDNEW").status());

        rates.create(new ProductRateRequest("RATE_NEW", "FDNEW", "SENIOR_CITIZEN", 12, 24,
                new BigDecimal("1000"), new BigDecimal("100000"), new BigDecimal("6.50"),
                new BigDecimal("0.50"), null, LocalDate.now(), null), "admin");
        fees.create(new ProductFeeRequest("FEE_NEW", "FDNEW", "SENIOR_CITIZEN", "PENALTY",
                "Premature withdrawal", null, new BigDecimal("1.00"), "PREMATURE_WITHDRAWAL",
                LocalDate.now(), null), "admin");

        lifecycle.transition("FDNEW", ProductStatus.ACTIVE, "admin", "Rate approved");
        var quote = pricing.quote("FDNEW", "SENIOR_CITIZEN", new BigDecimal("5000"), 12, LocalDate.now());

        assertEquals("RATE_NEW", quote.rateId());
        assertEquals(0, quote.effectiveRate().compareTo(new BigDecimal("7.00")));
        assertEquals(1, quote.fees().size());
        assertEquals(2, lifecycle.history("FDNEW").size());
        assertEquals("Rate approved", lifecycle.history("FDNEW").get(1).getRemarks());
    }

    @Test
    void deletesDraftProductWithItsRateAndStatusHistory() {
        products.create(new ProductRequest("FDDELETE", "DELETE100", "Temporary FD", "FD", "INR",
                new BigDecimal("1000"), new BigDecimal("100000"), 18,
                null, null, "MONTHLY", "MATURITY", "MONTHLY,MATURITY",
                "RENEW_PRINCIPAL", null, null, LocalDate.now(), "DRAFT"), "admin");
        rates.create(new ProductRateRequest("RATE_DELETE", "FDDELETE", null, 12, 24,
                null, null, new BigDecimal("6.50"), null, null, LocalDate.now(), null), "admin");

        products.delete("FDDELETE");
        entityManager.flush();
        assertThrows(NotFoundException.class, () -> products.get("FDDELETE"));
    }
}
