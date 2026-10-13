package com.banklab.calculator.controller;

import com.banklab.calculator.dto.CategoryListResponse;
import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.rates.RateProvider;
import com.banklab.calculator.service.CategorySelectionService;
import com.banklab.calculator.service.FdCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * The API "doors" of the FD Calculator.
 *
 *   GET  /api/fd/categories     -> categories + add-on % for the dropdown (Lab L6 section 4)
 *   GET  /api/fd/products       -> FD products (only filled in Phase C)
 *   GET  /api/fd/my-categories  -> logged-in customer's categories (only filled in Phase C)
 *   POST /api/fd/calculate      -> the calculation (Lab L6 section 3.1)
 *   POST /api/calculator/fd     -> same calculation, the name used in your team's WBS
 */
@RestController
@Tag(name = "FD Calculator", description = "Fixed Deposit maturity calculator (Labs L6, L10, L11)")
public class FdCalculatorController {

    private final FdCalculatorService calculatorService;
    private final RateProvider rateProvider;
    private final CategorySelectionService categorySelection;

    public FdCalculatorController(FdCalculatorService calculatorService,
                                  RateProvider rateProvider,
                                  CategorySelectionService categorySelection) {
        this.calculatorService = calculatorService;
        this.rateProvider = rateProvider;
        this.categorySelection = categorySelection;
    }

    @GetMapping("/api/fd/categories")
    @Operation(summary = "List customer categories with their additional interest %")
    public CategoryListResponse categories(@RequestParam(required = false) String productCode) {
        return new CategoryListResponse(
                rateProvider.sourceName(),
                categorySelection.categorySource(),
                rateProvider.addOnCap(),
                rateProvider.listCategories(productCode));
    }

    @GetMapping("/api/fd/products")
    @Operation(summary = "List FD products (empty when rates are LOCAL)")
    public List<ProductInfo> products() {
        return rateProvider.listProducts();
    }

    @GetMapping("/api/fd/my-categories")
    @Operation(summary = "Categories of the logged-in customer (Lab L10)")
    public List<String> myCategories(Principal principal, HttpServletRequest httpRequest) {
        return categorySelection.myCategories(principal, httpRequest);
    }

    @PostMapping({"/api/fd/calculate", "/api/calculator/fd"})
    @Operation(summary = "Calculate FD maturity amount, interest earned and effective rate")
    public FdCalculationResponse calculate(@Valid @RequestBody FdCalculationRequest request,
                                           Principal principal,
                                           HttpServletRequest httpRequest) {
        request.setCategories(categorySelection.resolve(request.getCategories(), principal, httpRequest));
        return calculatorService.calculate(request);
    }
}
