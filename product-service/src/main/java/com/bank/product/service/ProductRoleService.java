package com.bank.product.service;

import com.bank.product.dto.ProductRoleDtos.ProductRoleRequest;
import com.bank.product.dto.ProductRoleDtos.ProductRoleResponse;
import com.bank.product.exception.BadRequestException;
import com.bank.product.exception.ConflictException;
import com.bank.product.exception.NotFoundException;
import com.bank.product.model.Product;
import com.bank.product.model.ProductRole;
import com.bank.product.repository.ProductRepository;
import com.bank.product.repository.ProductRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductRoleService {
    private final ProductRoleRepository roles;
    private final ProductRepository products;

    @Transactional(readOnly = true)
    public com.bank.product.dto.RecordPage<ProductRoleResponse> list(int limit, int offset) {
        return com.bank.product.dto.RecordPage.of(roles.findAll().stream()
                .sorted(java.util.Comparator.comparing(ProductRole::getRoleId))
                .map(this::response).toList(), limit, offset);
    }

    @Transactional
    public ProductRoleResponse create(ProductRoleRequest request, String user) {
        String roleId = normalize(request.roleId());
        String productCode = normalize(request.productCode());
        String roleType = normalize(request.roleType());
        if (roles.existsById(roleId)) throw new ConflictException("Role ID already exists: " + roleId);
        Product product = product(productCode);
        if (roles.existsByProduct_ProductCodeAndRoleType(productCode, roleType)) {
            throw new ConflictException("Role type already exists for product: " + roleType);
        }
        ProductRole role = new ProductRole();
        role.setRoleId(roleId);
        role.setProduct(product);
        role.setRoleType(roleType);
        role.setMandatory(Boolean.TRUE.equals(request.mandatory()));
        role.setAuditUserId(user);
        return response(roles.save(role));
    }

    @Transactional(readOnly = true)
    public ProductRoleResponse get(String roleId) {
        return response(find(roleId));
    }

    @Transactional
    public ProductRoleResponse update(String roleId, ProductRoleRequest request, String user) {
        String id = normalize(roleId);
        if (!id.equals(normalize(request.roleId()))) {
            throw new BadRequestException("ROLE_ID in path and body must match");
        }
        ProductRole role = find(id);
        String productCode = normalize(request.productCode());
        String roleType = normalize(request.roleType());
        if (!role.getProduct().getProductCode().equals(productCode)) {
            throw new BadRequestException("PRODUCT_CODE cannot be changed for an existing role");
        }
        if (roles.existsByProduct_ProductCodeAndRoleTypeAndRoleIdNot(productCode, roleType, id)) {
            throw new ConflictException("Role type already exists for product: " + roleType);
        }
        role.setRoleType(roleType);
        role.setMandatory(Boolean.TRUE.equals(request.mandatory()));
        role.setAuditUserId(user);
        return response(roles.saveAndFlush(role));
    }

    @Transactional
    public void delete(String roleId) {
        ProductRole role = find(roleId);
        roles.delete(role);
    }

    private ProductRole find(String roleId) {
        return roles.findById(normalize(roleId))
                .orElseThrow(() -> new NotFoundException("Product role not found: " + roleId));
    }

    private Product product(String productCode) {
        return products.findAggregate(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productCode));
    }

    private ProductRoleResponse response(ProductRole role) {
        return new ProductRoleResponse(
                role.getRoleId(), role.getProduct().getProductCode(), role.getRoleType(), role.isMandatory(),
                role.getAuditEffectiveDate(), role.getAuditCrud(), role.getAuditUserId(), role.getAuditWorkstationId(),
                role.getAuditProgramId(), role.getAuditHostTimestamp(), role.getAuditLocalTimestamp(),
                role.getAuditAcceptanceTimestamp(), role.getAuditAcceptanceOffsetTimestamp(), role.getAuditUuid());
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
