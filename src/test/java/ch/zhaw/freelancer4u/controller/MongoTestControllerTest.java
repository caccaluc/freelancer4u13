package ch.zhaw.freelancer4u.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class MongoTestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testTestMongoDb() throws Exception {
              mvc.perform(get("/testmongodb")
              .contentType(MediaType.TEXT_PLAIN))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string("Connection ok"))
              .andReturn();  
    }
}
