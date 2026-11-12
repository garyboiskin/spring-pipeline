package com.example.springpipeline;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springpipeline.service.SpringPipelineService;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    private final SpringPipelineService springPipelineService;

    public RequestController(SpringPipelineService springPipelineService) {
        this.springPipelineService = springPipelineService;
    }

    @GetMapping
    public Map<String, Object> get() {
        log.info("Received GET request on /api/requests");
        return response("GET", springPipelineService.getAll());
    }

    @PostMapping
    public Map<String, Object> post(@RequestBody Map<String, Object> body) {
        log.info("Received POST request on /api/requests with body={}", body);
        return response("POST", springPipelineService.post(body));
    }

    @PutMapping
    public Map<String, Object> put(@RequestBody Map<String, Object> body) {
        log.info("Received PUT request on /api/requests with body={}", body);
        return response("PUT", springPipelineService.put(body));
    }

    @PatchMapping
    public Map<String, Object> patch(@RequestBody Map<String, Object> body) {
        log.info("Received PATCH request on /api/requests with body={}", body);
        return response("PATCH", springPipelineService.patch(body));
    }

    @DeleteMapping
    public Map<String, Object> delete() {
        log.info("Received DELETE request on /api/requests");
        return response("DELETE", springPipelineService.delete(Map.of("id", "value")));
    }

    private Map<String, Object> response(String method, Map<String, Object> body) {
        Map<String, Object> response = Map.of("method", method, "body", body == null ? Map.of() : body);
        log.info("Responding to {} request with body={}", method, response);
        return response;
    }
}
