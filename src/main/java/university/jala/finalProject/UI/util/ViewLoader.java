package university.jala.finalProject.UI.util;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.App; // tu clase App que apuntas a resources


@Component
public class ViewLoader {
    @Autowired private ConfigurableApplicationContext context;


    public Parent load(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/view/" + fxmlPath));
            loader.setControllerFactory(context::getBean);
            return loader.load();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar FXML: " + fxmlPath, e);
        }
    }
}