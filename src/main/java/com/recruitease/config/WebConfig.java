package com.recruitease.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads/resumes}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded resumes
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/resumes/**")
                .addResourceLocations(uploadPath);

        // Serve uploaded logos
        String logoPath = Paths.get("uploads/logos").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/logos/**")
                .addResourceLocations(logoPath);
    }
}
