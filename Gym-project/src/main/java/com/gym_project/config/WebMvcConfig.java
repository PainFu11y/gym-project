package com.gym_project.config;

import com.gym_project.logging.RestLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RestLoggingInterceptor restLoggingInterceptor;

    public WebMvcConfig(RestLoggingInterceptor restLoggingInterceptor) {
        this.restLoggingInterceptor = restLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restLoggingInterceptor)
                .addPathPatterns("/api/**");
    }
}