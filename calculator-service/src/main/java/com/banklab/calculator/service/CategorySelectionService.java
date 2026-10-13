package com.banklab.calculator.service;

import com.banklab.calculator.auth.DevUserStore;
import com.banklab.calculator.client.CustomerClient;
import com.banklab.calculator.model.CategoryCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Decides WHICH customer categories are used for a calculation (Phase C version).
 *
 * calculator.categories.source=REQUEST : categories ticked on the page are used.
 * calculator.categories.source=PROFILE : Lab L10 - "Calculator fetches logged-in user's
 *     category". Whatever the page sends is ignored; the logged-in customer's own
 *     categories are used instead, taken from:
 *       calculator.customer.source=DEV    -> demo users of the dev login stand-in
 *       calculator.customer.source=REMOTE -> Group 10's GET /api/customer/profile
 */
@Service
public class CategorySelectionService {

    private final String categorySource;
    private final String customerSource;
    private final DevUserStore devUsers;
    private final CustomerClient customerClient;

    public CategorySelectionService(@Value("${calculator.categories.source:REQUEST}") String categorySource,
                                    @Value("${calculator.customer.source:DEV}") String customerSource,
                                    DevUserStore devUsers,
                                    CustomerClient customerClient) {
        this.categorySource = categorySource.trim().toUpperCase(Locale.ROOT);
        this.customerSource = customerSource.trim().toUpperCase(Locale.ROOT);
        this.devUsers = devUsers;
        this.customerClient = customerClient;
    }

    /** "REQUEST" or "PROFILE". */
    public String categorySource() {
        return categorySource;
    }

    /** Categories to use for this calculation. */
    public List<String> resolve(List<String> requestedCategories, Principal principal,
                                HttpServletRequest httpRequest) {
        if (!"PROFILE".equals(categorySource) || principal == null) {
            return requestedCategories == null ? List.of() : requestedCategories;
        }
        return myCategories(principal, httpRequest);
    }

    /** Categories of the logged-in customer; GENERAL when they have none. */
    public List<String> myCategories(Principal principal, HttpServletRequest httpRequest) {
        if (principal == null) {
            return List.of();
        }
        List<String> categories;
        if ("REMOTE".equals(customerSource)) {
            categories = customerClient.fetchCategories(httpRequest.getHeader(HttpHeaders.AUTHORIZATION));
        } else {
            categories = CategoryCodes.normalizeAll(devUsers.categoriesOf(principal.getName()));
        }
        return categories.isEmpty() ? List.of("GENERAL") : categories;
    }
}
