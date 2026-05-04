package com.gym_report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GymReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymReportApplication.class, args);
    }
}
