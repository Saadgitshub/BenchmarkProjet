package com.example.WebPerformance;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JerseyConfig extends ResourceConfig {

    @PostConstruct
    public void init() {
        register(com.example.WebPerformance.resource.CategoryResource.class);
        register(com.example.WebPerformance.resource.ItemResource.class);
        // JSON mapping
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}