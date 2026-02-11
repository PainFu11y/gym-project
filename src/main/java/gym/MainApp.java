package gym;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("gym")
@PropertySource("classpath:application.properties")
public class MainApp {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(MainApp.class);

        ConsoleApp consoleApp = context.getBean(ConsoleApp.class);
        consoleApp.start();

        context.close();
    }
}