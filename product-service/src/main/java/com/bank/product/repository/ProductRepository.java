package com.bank.product.repository;
import com.bank.product.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDate; import java.util.*;
public interface ProductRepository extends JpaRepository<Product,String> {
 boolean existsByCurrency(String currency);
 boolean existsByProductId(String productId);
 boolean existsByProductIdAndProductCodeNot(String productId, String productCode);
 @Query("select distinct p from Product p where p.productCode=:code") Optional<Product> findAggregate(@Param("code") String code);
 @Query("select p from Product p where (:type is null or p.productType=:type) and (:fromDate is null or p.effectiveDate>=:fromDate) and (:toDate is null or p.effectiveDate<=:toDate)")
 List<Product> search(@Param("type") String type,@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);
}
