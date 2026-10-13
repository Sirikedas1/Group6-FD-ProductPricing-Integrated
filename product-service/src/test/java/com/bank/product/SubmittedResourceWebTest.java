package com.bank.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubmittedResourceWebTest {
    @Autowired MockMvc mvc;

    @Test
    void submittedCurrencyRouteUsesUppercaseFieldsAndRoles() throws Exception {
        mvc.perform(get("/currencies/INR"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/currencies/INR").with(user("reader").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CURRENCY_CODE").value("INR"))
                .andExpect(jsonPath("$.AUD_UUID").isNotEmpty());

        String body = """
                {"CURRENCY_CODE":"CHF","CURRENCY_NAME":"Swiss Franc",
                 "CURRENCY_SYMBOL":"Fr","DECIMAL_PLACES":2,"IS_ACTIVE":true}
                """;
        mvc.perform(post("/currencies").with(user("reader").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());

        mvc.perform(post("/currencies").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.CURRENCY_CODE").value("CHF"))
                .andExpect(jsonPath("$.AUD_USER_ID").value("admin"));
    }
}
