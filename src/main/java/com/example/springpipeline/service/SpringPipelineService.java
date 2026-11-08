package com.example.springpipeline.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpringPipelineService {

    private static final Logger log = LoggerFactory.getLogger(SpringPipelineService.class);

    public Map<String, Object> get(Map<String, Object> body) {
       log.info("Processing GET request payload={}", body);
       return Map.of("body", body);
    }

    public Map<String, Object> post(Map<String, Object> body) {
       log.info("Processing POST request payload={}", body);
       return body;
    }

    public Map<String, Object> put(Map<String, Object> body) {
       log.info("Processing PUT request payload={}", body);
       return body;
    }

    public Map<String, Object> delete(String id) {
       log.info("Processing DELETE request with id={}", id);
       return Map.of("id", id);
    }

    public Map<String, Object> patch(Map<String, Object> body) {
       log.info("Processing PATCH request payload={}", body);
       return body;
    }

    public Map<String, Object> delete(Map<String, Object> body) {
       log.info("Processing DELETE request payload={}", body);
       return body;
    }
}
