package com.example.springpipeline;

import com.example.springpipeline.controller.RequestController;
import com.example.springpipeline.security.JwtAuthenticationFilter;
import com.example.springpipeline.service.SpringPipelineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //@MockBean
    @MockitoBean
    private SpringPipelineService springPipelineService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getHandlesRequest() throws Exception {
        Map<String, Object> customer = Map.of(
                "firstName", "Alice",
                "lastName", "Smith",
                "email", "alice@example.com"
        );

        when(springPipelineService.getAll()).thenReturn(Map.of("customers", List.of(customer)));

        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method", is("GET")))
                .andExpect(jsonPath("$.body.customers[0].firstName", is("Alice")));
    }

    @Test
    void postHandlesAndReturnsRequestBody() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "sample");
        when(springPipelineService.post(anyMap())).thenReturn(body);

        mockMvc.perform(post("/api/requests")
                       .contentType("application/json")
                       .content("{\"name\":\"sample\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.method", is("POST")))
               .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void putHandlesAndReturnsRequestBody() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "sample");
        when(springPipelineService.put(anyMap())).thenReturn(body);

        mockMvc.perform(put("/api/requests")
                       .contentType("application/json")
                       .content("{\"name\":\"sample\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.method", is("PUT")))
               .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void patchHandlesAndReturnsRequestBody() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "sample");
        when(springPipelineService.patch(anyMap())).thenReturn(body);

        mockMvc.perform(patch("/api/requests")
                       .contentType("application/json")
                       .content("{\"name\":\"sample\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.method", is("PATCH")))
               .andExpect(jsonPath("$.body.name", is("sample")));
    }

    @Test
    void deleteHandlesRequest() throws Exception {
        when(springPipelineService.delete(Map.of("id", "value"))).thenReturn(Map.of("id", "value"));

        mockMvc.perform(delete("/api/requests"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.method", is("DELETE")))
               .andExpect(jsonPath("$.body.id", is("value")));
    }
}
