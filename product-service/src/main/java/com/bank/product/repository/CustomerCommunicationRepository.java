package com.bank.product.repository;

import com.bank.product.model.CustomerCommunication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCommunicationRepository extends JpaRepository<CustomerCommunication, String> {
    boolean existsByCategory_CategoryCode(String categoryCode);
}
