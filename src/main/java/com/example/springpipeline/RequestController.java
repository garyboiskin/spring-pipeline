package com.example.springpipeline;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import  com.example.springpipeline.service.SpringPipelineService;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final SpringPipelineService springPipelineService;

    public RequestController(SpringPipelineService springPipelineService) {
        this.springPipelineService = springPipelineService;
    }

    @GetMapping
    public Map<String, Object> get() {
      //  return response("GET", null);
        return response("GET", springPipelineService.get(Map.of("GET", "value")));
    }

    @PostMapping
    public Map<String, Object> post(@RequestBody Map<String, Object> body) {
       // return response("POST", body);
        return response("POST", springPipelineService.post(body));
    }

    @PutMapping
    public Map<String, Object> put(@RequestBody Map<String, Object> body) {


//        return response("PUT", body);
        return response("PUT", springPipelineService.put(body));
    }

    @PatchMapping
    public Map<String, Object> patch(@RequestBody Map<String, Object> body) {

//        return response("PATCH", body);

        return response("PATCH", springPipelineService.patch(body));
    }

    @DeleteMapping
    public Map<String, Object> delete() {
        return response("DELETE", springPipelineService.delete(Map.of("id", "value")));
    }

    private Map<String, Object> response(String method, Map<String, Object> body) {
        return Map.of("method", method, "body", body == null ? Map.of() : body);
    }
}
