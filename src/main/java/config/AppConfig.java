package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = {"dao", "service.impl", "utils", "storage"})
public class AppConfig {

}

