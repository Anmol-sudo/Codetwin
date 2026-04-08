package com.example.codetwin.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/test")
public class TestController {
    // Protected route
    @GetMapping
    public String test() {
        return "Protected route working!";
    }
}
