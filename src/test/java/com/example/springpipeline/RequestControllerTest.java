package com.example.springpipeline;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHandlesRequest() throws Exception {
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("GET")));
    }

    @Test
    void postHandlesAndReturnsRequestBody() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType("application/json")
                        .content("{\"name\":\"sample\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("POST")))
                .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void putHandlesAndReturnsRequestBody() throws Exception {
        mockMvc.perform(put("/api/requests")
                        .contentType("application/json")
                        .content("{\"name\":\"sample\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("PUT")))
                .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void patchHandlesAndReturnsRequestBody() throws Exception {
        mockMvc.perform(patch("/api/requests")
                        .contentType("application/json")
                        .content("{\"name\":\"sample\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("PATCH")))
                .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void deleteHandlesRequest() throws Exception {
        mockMvc.perform(delete("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("DELETE")));
    }
}
