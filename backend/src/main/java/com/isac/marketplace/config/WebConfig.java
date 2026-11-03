package com.isac.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.local-dir:./uploads/anuncios}")
    private String localDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(localDir).toAbsolutePath().normalize();
        String uriPath = uploadPath.toUri().toString();

        registry.addResourceHandler("/uploads/anuncios/**")
                .addResourceLocations(uriPath.endsWith("/") ? uriPath : uriPath + "/");
    }
}
