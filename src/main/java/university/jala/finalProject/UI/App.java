package university.jala.finalProject.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import university.jala.finalProject.Main;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class App extends Application {
    private ConfigurableApplicationContext ctx;

    @Override public void init() {
        // Levantá Spring ANTES del start()
        ctx = new SpringApplicationBuilder(Main.class).run();
    }

    @Override public void start(Stage primaryStage) throws Exception {
        var url = App.class.getResource("/view/authentication.fxml");
        if (url == null) throw new IllegalStateException("Falta /view/authentication.fxml");

        var loader = new FXMLLoader(url);
        loader.setControllerFactory(ctx::getBean); // controla via Spring
        var scene = new Scene(loader.load(), 800, 600);

        primaryStage.setTitle("Sistema de Gestión de Tareas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override public void stop() {
        if (ctx != null) ctx.close();
    }
}