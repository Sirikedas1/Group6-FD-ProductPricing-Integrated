package com.bank.product.controller;
import com.bank.product.dto.ApiResponse; import com.bank.product.dto.ProductDtos.CategoryRequest; import com.bank.product.model.CustomerCategory; import com.bank.product.service.CategoryService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/product/categories") @RequiredArgsConstructor public class CategoryController {private final CategoryService service;
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<ApiResponse<CustomerCategory>> create(@Valid @RequestBody CategoryRequest r,Authentication a){return ResponseEntity.status(201).body(ApiResponse.success(service.create(r,a.getName()),"Category created"));}
 @GetMapping public ApiResponse<List<CustomerCategory>> all(){return ApiResponse.success(service.all(),"Categories retrieved");}
}
