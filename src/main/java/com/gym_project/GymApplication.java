package com.gym_project;

import com.gym_project.config.ApplicationConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);

        AppRunner appRunner = context.getBean(AppRunner.class);

        appRunner.run();

        context.close();
    }
}