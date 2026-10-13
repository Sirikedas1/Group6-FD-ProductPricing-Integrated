package com.bank.product.repository;

import com.bank.product.model.ProductRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRoleRepository extends JpaRepository<ProductRole, String> {
    java.util.List<ProductRole> findByProduct_ProductCode(String productCode);
    boolean existsByProduct_ProductCodeAndRoleType(String productCode, String roleType);
    boolean existsByProduct_ProductCodeAndRoleTypeAndRoleIdNot(String productCode, String roleType, String roleId);
}
