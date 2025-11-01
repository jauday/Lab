package com.lab.patientservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class config implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // Apply to all paths
                    .allowedOrigins("http://localhost:3000", "http://anotherdomain.com") // Specific origins
                    .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                    .allowedHeaders("*") // All headers allowed
                    .allowCredentials(true) // Allow credentials (cookies, HTTP authentication)
                    .maxAge(3600); // Cache preflight requests for 1 hour
        }
}
