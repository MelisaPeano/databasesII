package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        System.out.println("=== INICIANDO NAVEGACIÓN AL DASHBOARD ===");
        System.out.println("Login exitoso: " + user.getUserName());

        try {

            System.out.println("ContentPane: " + contentPane);
            System.out.println("ContentPane scene: " + contentPane.getScene());

            ConfigurableApplicationContext context = Main.getApplicationContext();
            System.out.println("Context de Spring: " + (context != null));

            java.net.URL fxmlUrl = getClass().getResource("/view/dashboard.fxml");
            System.out.println("URL del dashboard.fxml: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERROR: No se encuentra el archivo dashboard.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(context::getBean);

            Parent dashboardRoot = loader.load();
            System.out.println("Dashboard FXML cargado exitosamente");

            Scene currentScene = contentPane.getScene();
            if (currentScene == null) {
                System.err.println("ERROR: No hay escena actual");
                return;
            }

            System.out.println("Cambiando root de la escena...");
            currentScene.setRoot(dashboardRoot);
            System.out.println("Root cambiado exitosamente");

            DashboardController dashboardController = loader.getController();
            System.out.println("DashboardController: " + dashboardController);

            if (dashboardController != null) {
                dashboardController.setCurrentUser(user);
                System.out.println("Usuario pasado al dashboard controller");
            }

            System.out.println("=== NAVEGACIÓN COMPLETADA ===");

        } catch (IOException e) {
            System.err.println("ERROR CRÍTICO al cargar el dashboard:");
            e.printStackTrace();
        }
    }

    public void showSuccess(String message) {
        System.out.println("Exito: " + message);
    }
}