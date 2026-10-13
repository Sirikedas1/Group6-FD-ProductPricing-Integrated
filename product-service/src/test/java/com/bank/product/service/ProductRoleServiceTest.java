package com.bank.product.service;

import com.bank.product.dto.ProductRoleDtos.ProductRoleRequest;
import com.bank.product.model.Product;
import com.bank.product.repository.ProductRepository;
import com.bank.product.repository.ProductRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRoleServiceTest {
    @Mock ProductRoleRepository roles;
    @Mock ProductRepository products;

    @Test
    void createsRoleWithFalseMandatoryFlagWhenOmitted() {
        Product product = new Product();
        product.setProductCode("FD001");
        when(products.findAggregate("FD001")).thenReturn(Optional.of(product));
        when(roles.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(roles.existsById("ROLE1")).thenReturn(false);
        when(roles.existsByProduct_ProductCodeAndRoleType("FD001", "NOMINEE")).thenReturn(false);
        ProductRoleService service = new ProductRoleService(roles, products);

        var result = service.create(new ProductRoleRequest("ROLE1", "FD001", "NOMINEE", null), "admin");

        assertEquals("ROLE1", result.roleId());
        assertEquals("FD001", result.productCode());
        assertEquals(false, result.mandatory());
    }

    @Test
    void updatesRoleMandatoryFlag() {
        Product product = new Product();
        product.setProductCode("FD001");
        var role = new com.bank.product.model.ProductRole();
        role.setRoleId("ROLE1");
        role.setProduct(product);
        role.setRoleType("NOMINEE");
        when(roles.findById("ROLE1")).thenReturn(Optional.of(role));
        when(roles.existsByProduct_ProductCodeAndRoleTypeAndRoleIdNot("FD001", "NOMINEE", "ROLE1")).thenReturn(false);
        when(roles.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ProductRoleService service = new ProductRoleService(roles, products);

        var result = service.update("ROLE1", new ProductRoleRequest("ROLE1", "FD001", "NOMINEE", true), "admin");

        assertEquals(true, result.mandatory());
        verify(roles).saveAndFlush(role);
    }

    @Test
    void deletesExistingRole() {
        var role = new com.bank.product.model.ProductRole();
        role.setRoleId("ROLE1");
        when(roles.findById("ROLE1")).thenReturn(Optional.of(role));
        ProductRoleService service = new ProductRoleService(roles, products);

        service.delete("ROLE1");

        verify(roles).delete(role);
    }
}
