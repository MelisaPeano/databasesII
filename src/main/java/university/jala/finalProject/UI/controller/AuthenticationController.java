package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import university.jala.finalProject.Main;
import university.jala.finalProject.UI.App;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;

import java.io.IOException;

@Component
public class AuthenticationController {

    @FXML private StackPane contentPane;

    @Autowired private ConfigurableApplicationContext context;

    @FXML
    public void initialize() {
        if (contentPane == null) {
            throw new IllegalStateException("fx:id=contentPane no inyectado (revisa authentication.fxml)");
        }
        loadLoginForm();
    }

    public void loadLoginForm() {
        try {
            var url = App.class.getResource("/view/login.fxml");
            if (url == null) throw new IllegalStateException("Falta /view/login.fxml en el classpath");

            var loader = new FXMLLoader(url);
            loader.setControllerFactory(context::getBean);   // integración Spring + FXML
            Parent loginForm = loader.load();

            var loginController = loader.getController();     // requiere fx:controller en login.fxml
            if (loginController == null) {
                throw new IllegalStateException("login.fxml no define fx:controller");
            }
            ((LoginController) loginController).setAuthController(this);

            contentPane.getChildren().setAll(loginForm);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar login.fxml", e);
        }
    }

    public void loadRegisterForm() {
        try {
            ConfigurableApplicationContext context = Main.getApplicationContext();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent registerForm = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setAuthController(this);

            contentPane.getChildren().setAll(registerForm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLoginSuccess(AppUser user) {
        System.out.println("Login exitoso: " + user.getUserName());
        //---------- creo que aquí iria la navegación al dashboard ----------
    }

    public void showSuccess(String message) {
        System.out.println("Éxito: " + message);
    }
}