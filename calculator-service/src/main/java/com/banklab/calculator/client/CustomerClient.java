package com.banklab.calculator.client;

import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab L10: "Calculator fetches logged-in user's category".
 * Calls Group 10's GET /api/customer/profile with the user's own token and reads
 * custCategoryCode (their spec: "drives rate/pricing selection in the FD module").
 * Used only when calculator.customer.source=REMOTE.
 */
@Component
public class CustomerClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String profilePath;

    public CustomerClient(@Value("${calculator.customer.base-url:http://localhost:8080}") String baseUrl,
                          @Value("${calculator.customer.profile-path:/api/customer/profile}") String profilePath,
                          @Value("${calculator.customer.timeout-ms:3000}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.baseUrl = baseUrl;
        this.profilePath = profilePath;
    }

    public List<String> fetchCategories(String authorizationHeader) {
        JsonNode profile;
        try {
            RestClient.RequestHeadersSpec<?> call = restClient.get().uri(profilePath);
            if (authorizationHeader != null) {
                call = call.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
            profile = call.retrieve().body(JsonNode.class);
        } catch (ResourceAccessException e) {
            throw CalculationException.serviceUnavailable("CUSTOMER_SERVICE_UNAVAILABLE",
                    "Customer service is not reachable at " + baseUrl + ". Is Group 10's service running?");
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw CalculationException.unprocessable("CUSTOMER_PROFILE_NOT_FOUND",
                        "No customer profile exists for your login. Ask a bank officer to create one.");
            }
            throw CalculationException.badGateway("CUSTOMER_SERVICE_ERROR",
                    "Customer service answered with HTTP " + e.getStatusCode().value() + ".");
        }

        List<String> result = new ArrayList<>();
        JsonNode list = JsonFields.field(profile, "categories", "custCategoryCodes");
        if (list != null && list.isArray()) {
            list.forEach(n -> result.add(n.asText()));
        }
        String single = JsonFields.text(profile, "custCategoryCode", "CUST_CATEGORY_CODE", "categoryCode",
                "classification", "customerClassification");
        if (single != null && !single.isBlank()) {
            result.add(single);
        }
        return CategoryCodes.normalizeAll(result);
    }
}
