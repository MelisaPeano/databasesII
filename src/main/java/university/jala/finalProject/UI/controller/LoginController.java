package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;

import java.util.Optional;

@Component
public class LoginController {

    @Autowired
    private UserService userService;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label authError;

    private AuthenticationController authController;

    public void setAuthController(AuthenticationController authController) {
        this.authController = authController;
        System.out.println("AuthController asignado en LoginController");
    }

    @FXML
    public void initialize() {
        System.out.println("LoginController inicializado - UserService: " + (userService != null));
        setupRealTimeValidation();
    }

    private void setupRealTimeValidation() {
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
    }

    @FXML
    private void handleLogin() {
        hideAllErrors();

        try {
            if (!validateForm()) {
                System.out.println("❌ Validación de formulario falló (email o password vacíos o inválidos).");
                return;
            }

            String email = emailField.getText();
            String password = passwordField.getText();

            System.out.println("🔍 Intentando autenticar con email: " + email);

            Optional<AppUser> user = userService.authenticate(email, password);

            if (user.isPresent()) {
                System.out.println("✅ Autenticación exitosa para usuario: " + user.get().getUserName());
                if (authController != null) {
                    authController.onLoginSuccess(user.get());
                } else {
                    System.out.println("⚠️ authController es NULL — no se puede redirigir tras el login.");
                }
            } else {
                System.out.println("❌ Autenticación fallida: usuario no encontrado o contraseña incorrecta.");
                showAuthError("Credenciales incorrectas. Intenta nuevamente.");
            }

        } catch (Exception e) {
            System.out.println("🔥 Error durante el proceso de login:");
            e.printStackTrace();
            showAuthError("Ocurrió un error interno. Revisa la consola para más detalles.");
        }
    }


    @FXML
    private void switchToRegister() {
        if (authController != null) {
            authController.loadRegisterForm();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        if (!validateEmail()) isValid = false;
        if (!validatePassword()) isValid = false;
        return isValid;
    }

    private boolean validateEmail() {
        String email = emailField.getText();
        if (email == null || email.trim().isEmpty()) {
            showError(emailError, "El email es requerido");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(emailError, "Formato de email inválido");
            return false;
        }
        hideError(emailError);
        return true;
    }

    private boolean validatePassword() {
        String password = passwordField.getText();
        if (password == null || password.trim().isEmpty()) {
            showError(passwordError, "La contraseña es requerida");
            return false;
        }
        hideError(passwordError);
        return true;
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
    }

    private void hideAllErrors() {
        hideError(emailError);
        hideError(passwordError);
        hideError(authError);
    }

    private void showAuthError(String message) {
        authError.setText(message);
        authError.setVisible(true);
    }
}