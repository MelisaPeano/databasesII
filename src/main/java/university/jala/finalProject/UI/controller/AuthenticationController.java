package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.App;
import university.jala.finalProject.UI.service.CurrentUserService;
import university.jala.finalProject.springJPA.entity.AppUser;

@Component
public class AuthenticationController {

    @FXML private StackPane contentPane;

    @Autowired private ConfigurableApplicationContext context; // usa SIEMPRE este

    @Autowired
    private CurrentUserService currentUserService;

    @FXML
    public void initialize() {
        if (contentPane == null) {
            throw new IllegalStateException("fx:id=contentPane no inyectado (revisa authentication.fxml)");
        }
        loadLoginForm();
    }

    public void loadLoginForm() {
        contentPane.getChildren().setAll(load("/view/login.fxml"));
        // login.fxml debe tener fx:controller=... y el controller @Component
        // y su LoginController debe llamarnos: authController.onLoginSuccess(user)
    }

    public void loadRegisterForm() {
        contentPane.getChildren().setAll(load("/view/register.fxml"));
        // register.fxml: fx:controller correcto + Button onAction="#onRegister"
    }

    public void onLoginSuccess(AppUser user) {
        System.out.println("Login exitoso: " + user.getUserName());

        currentUserService.setCurrentUser(user);

        // Ir al Dashboard
        contentPane.getChildren().setAll(load("/view/MainLayout.fxml"));
    }

    private Parent load(String fxmlPath) {
        try {
            var url = App.class.getResource(fxmlPath);
            if (url == null) throw new IllegalStateException("Falta " + fxmlPath + " en resources");
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(context::getBean); // integración Spring + FXML (única fuente)
            Parent root = loader.load();
            // Si el controller necesita referencia a este auth, setéala si expone setAuthController(...)
            Object ctrl = loader.getController();
            try {
                ctrl.getClass().getMethod("setAuthController", AuthenticationController.class)
                        .invoke(ctrl, this);
            } catch (NoSuchMethodException ignored) { /* opcional */ }
            return root;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar " + fxmlPath, e);
        }
    }

    public void showSuccess(String message) {
        System.out.println("Éxito: " + message);
    }
}
