package com.garyboiskin.springpipeline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public Greeting hello() {
        return new Greeting("Hello, World!");
    }

    public record Greeting(String message) {
    }
}
