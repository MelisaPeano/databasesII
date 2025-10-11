package university.jala.finalProject;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import university.jala.finalProject.UI.App;

@SpringBootApplication
public class Main {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        // Lanzar Spring Boot primero
        applicationContext = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);

        // Lanzar JavaFX
        javafx.application.Application.launch(App.class, args);
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}