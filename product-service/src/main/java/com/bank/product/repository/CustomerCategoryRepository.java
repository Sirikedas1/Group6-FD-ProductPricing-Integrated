package com.bank.product.repository;
import com.bank.product.model.CustomerCategory; import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerCategoryRepository extends JpaRepository<CustomerCategory,String>{}
