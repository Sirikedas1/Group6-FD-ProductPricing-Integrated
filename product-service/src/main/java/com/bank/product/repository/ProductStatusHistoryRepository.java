package com.bank.product.repository;

import com.bank.product.model.ProductStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, String> {
    List<ProductStatusHistory> findByProductCodeOrderByChangedAtAsc(String productCode);
}
