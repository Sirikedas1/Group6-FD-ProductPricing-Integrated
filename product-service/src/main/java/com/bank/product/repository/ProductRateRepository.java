package com.bank.product.repository;

import com.bank.product.model.ProductRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRateRepository extends JpaRepository<ProductRate, String> {
    boolean existsByCategory_CategoryCode(String categoryCode);
    boolean existsByProduct_ProductCode(String productCode);
    List<ProductRate> findByProduct_ProductCode(String productCode);
    List<ProductRate> findByProduct_ProductCodeOrderByMinTenureMonthsAsc(String productCode);
}
