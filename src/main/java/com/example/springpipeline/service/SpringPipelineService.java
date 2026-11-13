package com.example.springpipeline.service;

import java.util.Map;

import com.example.springpipeline.entities.Customer;
import com.example.springpipeline.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpringPipelineService {

    private static final Logger log = LoggerFactory.getLogger(SpringPipelineService.class);

    private final CustomerRepository customerRepository;

    public SpringPipelineService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Map<String, Object> getAll() {
       log.info("Processing GET All");
       Iterable<Customer> customers= customerRepository.findAll();
       return Map.of("customers", customers);
      // return body;
    }

    // create getById
    public Map<String, Object> getById(Long id) {
        log.info("Processing GET by ID: {}", id);
        Customer customer = customerRepository.findById(id).orElse(null);
        return Map.of("customer", customer);
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
