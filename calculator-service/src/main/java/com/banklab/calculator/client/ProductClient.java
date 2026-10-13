package com.banklab.calculator.client;

import com.banklab.calculator.exception.CalculationException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;

/**
 * Talks to your teammates' product-service (Lab L6: "integrate with Product and
 * Pricing module for fetching base rates"). Paths come from application.properties
 * so they can be matched to Ananya's/Lavanya's real code without changing Java.
 *
 * Team rule (from your plan): 3-second timeout, and if product-service is down
 * we answer 503 - we NEVER silently fall back to a made-up rate.
 */
@Component
public class ProductClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String productsPath;
    private final String ratesPath;
    private final String categoriesPath;

    public ProductClient(@Value("${calculator.product.base-url:http://localhost:8081}") String baseUrl,
                         @Value("${calculator.product.products-path:/products}") String productsPath,
                         @Value("${calculator.product.rates-path:/api/product-rate}") String ratesPath,
                         @Value("${calculator.product.categories-path:/api/customer-category}") String categoriesPath,
                         @Value("${calculator.product.timeout-ms:3000}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.baseUrl = baseUrl;
        this.productsPath = productsPath;
        this.ratesPath = ratesPath;
        this.categoriesPath = categoriesPath;
    }

    public JsonNode getProduct(String productCode) {
        return get(productsPath + "/" + productCode, "Product " + productCode);
    }

    public List<JsonNode> listProducts() {
        return JsonFields.items(get(productsPath + "?limit=500", "Product list"));
    }

    public List<JsonNode> listRates() {
        return JsonFields.items(get(ratesPath + "?limit=500", "Rate matrix"));
    }

    public List<JsonNode> listCategories() {
        return JsonFields.items(get(categoriesPath + "?limit=500", "Customer categories"));
    }

    private JsonNode get(String path, String what) {
        try {
            RestClient.RequestHeadersSpec<?> call = restClient.get().uri(path);
            String auth = currentAuthorizationHeader();
            if (auth != null) {
                call = call.header(HttpHeaders.AUTHORIZATION, auth);
            }
            return call.retrieve().body(JsonNode.class);
        } catch (ResourceAccessException e) {
            throw CalculationException.serviceUnavailable("PRODUCT_SERVICE_UNAVAILABLE",
                    "Product service is not reachable at " + baseUrl + " (waited 3 seconds). Is it running?");
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 404) {
                throw CalculationException.notFound("PRODUCT_NOT_FOUND", what + " was not found in product-service.");
            }
            throw CalculationException.badGateway("PRODUCT_SERVICE_ERROR",
                    "Product service answered HTTP " + status + " for " + what + ".");
        }
    }

    /** Passes the logged-in user's token on to product-service (its GET endpoints need a token). */
    private static String currentAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}
