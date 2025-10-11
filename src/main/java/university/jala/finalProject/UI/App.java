package university.jala.finalProject.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import university.jala.finalProject.Main;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //obtiene el contexto de Spring
        var context = Main.getApplicationContext();

        //carga el FXML usando Spring
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authentication.fxml"));
        loader.setControllerFactory(context::getBean); //usa Spring para crear controladores

        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setTitle("Sistema de Gestión de Tareas");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("JavaFX Application started successfully!");
    }

    @Override
    public void stop() throws Exception {
        //cierra el contexto de Spring al salir
        Main.getApplicationContext().close();
        super.stop();
    }
}