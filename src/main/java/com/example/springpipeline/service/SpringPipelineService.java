package com.example.springpipeline.service;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpringPipelineService {

    // create methods to return the request method for each HTTP method (GET, POST, PUT, PATCH, DELETE)
    //in com.example.springpipeline.RequestController, we can call these methods to get the request method and return it in the response
    //The methods have to accept a Map<String, Object> parameter for the request body, and return a Map<String, Object> with the request method and body.

    public Map<String, Object> get( Map<String, Object> body) {
        return Map.of( "body", body);
    }
     public Map<String, Object> post(Map<String, Object> body) {
       //  return Map.of("body", body);
         return body;
     }

    public Map<String, Object> put(Map<String, Object> body) {
    //    return Map.of("body", body);
        return  body;
    }
    public Map<String, Object> delete(String id) {
       // return Map.of("body", Map.of("id", id));
        return  Map.of("id", id);
    }

    public Map<String, Object> patch(Map<String, Object> body) {
       // return Map.of("body", body);
        return body;
    }

    public Map<String, Object> delete(Map<String, Object> body) {
       // return Map.of("body", body);
        return body;
    }

}
