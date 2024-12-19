package ch.zhaw.freelancer4u.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.security.TestSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestMethodOrder(OrderAnnotation.class)

public class CompanyControllerTest {
    @Autowired
    private MockMvc mvc;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static String company_id = "";

    private static final String TEST_COMPANY = "TestCompany";
    private static final String TEST_EMAIL = "test@testcompany.com";

    @Test
    @Order(1)
    @WithMockUser
    void testCreateCompany() throws Exception{
        Company company = new Company(TEST_COMPANY, TEST_EMAIL);
        String jsonBody = objectMapper.writeValueAsString(company);

        var result = mvc.perform(post("/api/company")
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, "Bearer token")
              .content(jsonBody))
              .andDo(print())
              .andExpect(status().isCreated())
              .andReturn();  

        // get the id of the created company
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        company_id = jsonNode.get("id").asText();
    }

    @Test
    @Order(2)
    @WithMockUser
    public void testGetCompany() throws Exception {
        mvc.perform(get("/api/company/" + company_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_COMPANY))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andReturn();
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteCompany() throws Exception {
        mvc.perform(delete("/api/company/" + company_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
