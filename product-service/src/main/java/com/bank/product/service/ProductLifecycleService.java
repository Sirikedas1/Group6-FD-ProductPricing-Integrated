package com.bank.product.service;

import com.bank.product.exception.BadRequestException;
import com.bank.product.exception.NotFoundException;
import com.bank.product.model.Product;
import com.bank.product.model.ProductStatus;
import com.bank.product.model.ProductStatusHistory;
import com.bank.product.repository.ProductRepository;
import com.bank.product.repository.ProductStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductLifecycleService {
    private final ProductRepository products;
    private final ProductStatusHistoryRepository history;
    private final com.bank.product.repository.ProductRateRepository rates;

    private static final Map<ProductStatus, Set<ProductStatus>> ALLOWED = Map.of(
            ProductStatus.DRAFT, Set.of(ProductStatus.ACTIVE),
            ProductStatus.ACTIVE, Set.of(ProductStatus.WITHDRAWN),
            ProductStatus.UNDER_ESTABLISHMENT, Set.of(ProductStatus.APPROVED),
            ProductStatus.APPROVED, Set.of(ProductStatus.UNROLLED),
            ProductStatus.UNROLLED, Set.of(ProductStatus.SETTLED),
            ProductStatus.SETTLED, Set.of(ProductStatus.UNCLAIMED, ProductStatus.MARKED_FOR_CLOSURE),
            ProductStatus.UNCLAIMED, Set.of(ProductStatus.MARKED_FOR_CLOSURE),
            ProductStatus.MARKED_FOR_CLOSURE, Set.of(ProductStatus.CLOSED)
    );

    @Transactional
    public Product transition(String code, ProductStatus target, String user, String remarks) {
        Product product = find(code);
        ProductStatus current = product.getStatus();
        if (current == target) throw new BadRequestException("Product is already in status " + target);
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(target)) {
            throw new BadRequestException("Invalid product status transition: " + current + " -> " + target);
        }
        if (target == ProductStatus.ACTIVE && !rates.existsByProduct_ProductCode(product.getProductCode()))
            throw new BadRequestException("Add a product rate before activating the product");
        ProductStatusHistory row = new ProductStatusHistory();
        row.setHistoryId(UUID.randomUUID().toString());
        row.setProductCode(product.getProductCode());
        row.setPreviousStatus(current == null ? null : current.name());
        row.setNewStatus(target.name());
        row.setChangedBy(user);
        row.setChangedAt(LocalDateTime.now());
        row.setRemarks(remarks);
        history.save(row);
        product.setStatus(target);
        product.setAuditUserId(user);
        return products.saveAndFlush(product);
    }

    @Transactional(readOnly = true)
    public List<ProductStatusHistory> history(String code) {
        find(code);
        return history.findByProductCodeOrderByChangedAtAsc(normalize(code));
    }

    private Product find(String code) {
        return products.findAggregate(normalize(code))
                .orElseThrow(() -> new NotFoundException("Product not found: " + code));
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
