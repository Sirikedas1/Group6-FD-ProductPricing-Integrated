package com.bank.product.repository;

import com.bank.product.model.ProductFeeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFeeRepository extends JpaRepository<ProductFeeRate, String> {
    boolean existsByCategory_CategoryCode(String categoryCode);
    java.util.List<ProductFeeRate> findByProduct_ProductCode(String productCode);
}
