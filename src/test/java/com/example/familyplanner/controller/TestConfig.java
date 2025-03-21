package com.example.familyplanner.controller;



import com.example.familyplanner.Security.JWT.JwtCore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtCore jwtCore() {
        return new JwtCore();
    }
}