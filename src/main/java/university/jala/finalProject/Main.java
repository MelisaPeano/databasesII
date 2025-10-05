package university.jala.finalProject;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import university.jala.finalProject.UI.App;

@SpringBootApplication
public class Main {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		// Inicializa Spring Boot
		context = new SpringApplicationBuilder(Main.class)
				.headless(false) // <- necesario para usar JavaFX
				.run(args);

		// Lanza la aplicación JavaFX
		Application.launch(App.class, args);
	}

	public static ConfigurableApplicationContext getContext() {
		return context;
	}
}