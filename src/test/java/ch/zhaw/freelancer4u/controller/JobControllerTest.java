package ch.zhaw.freelancer4u.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.CompanyRepository;
import ch.zhaw.freelancer4u.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    CompanyRepository companyRepository;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEST_DESCRIPTION = "TEST-abc...xyz";
    private static String company_id = "";
    private static String job_id = "";

    @Disabled
    @Test
    @Order(1)
    @WithMockUser
    public void testCreateJob() throws Exception {
        // get valid company id
        company_id = getCompanyId();
        System.out.println("using company id " + company_id);

        // create a test job and convert to Json
        Job job = new Job();
        job.setDescription(TEST_DESCRIPTION);
        job.setJobType(JobType.TEST);
        job.setEarnings(3.1415);
        job.setCompanyId(company_id);
        String jsonBody = objectMapper.writeValueAsString(job);

        // POST Json to service with authorization header
        var result = mvc.perform(post("/api/job")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        job_id = jsonNode.get("id").asText();
        System.out.println("created job with id " + job_id);
    }

    @Disabled
    @Test
    @Order(2)
    @WithMockUser
    public void testGetJob() throws Exception {
        mvc.perform(get("/api/job/" + job_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.companyId").value(company_id))
                .andReturn();
    }

    @Disabled
    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteJobs() throws Exception {
        mvc.perform(delete("/api/job/" + job_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Disabled
    @Test
    @Order(4)
    @WithMockUser
    public void testGetDeletedJob() throws Exception {
        mvc.perform(get("/api/job/" + job_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private String getCompanyId() {
        // get valid company id
        return companyRepository.findAll().get(0).getId();
    }
}
