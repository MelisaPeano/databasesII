package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import university.jala.finalProject.Main;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;

import java.io.IOException;

@Component
public class AuthenticationController {

    @FXML
    private StackPane contentPane;

    @Autowired
    private UserService userService;

    @FXML
    public void initialize() {
        System.out.println("AuthenticationController inicializado - UserService: " + (userService != null));
        loadLoginForm();
    }

    public void loadLoginForm() {
        try {
            ConfigurableApplicationContext context = Main.getApplicationContext();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent loginForm = loader.load();
            LoginController loginController = loader.getController();
            loginController.setAuthController(this);

            contentPane.getChildren().setAll(loginForm);
        } catch (IOException e) {
            e.printStackTrace();
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