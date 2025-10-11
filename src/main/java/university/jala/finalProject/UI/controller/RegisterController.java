package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;

@Component
public class RegisterController {

    @Autowired
    private UserService userService;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label usernameError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;
    @FXML private Label authError;

    private AuthenticationController authController;

    public void setAuthController(AuthenticationController authController) {
        this.authController = authController;
        System.out.println("AuthController asignado en RegisterController");
    }

    @FXML
    public void initialize() {
        System.out.println("RegisterController inicializado - UserService: " + (userService != null));
        setupRealTimeValidation();
    }

    private void setupRealTimeValidation() {
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateUsername());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateEmail());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPassword());
    }

    @FXML
    private void handleRegister() {
        hideAllErrors();

        if (validateForm()) {
            try {
                AppUser newUser = new AppUser(
                        usernameField.getText().trim(),
                        emailField.getText().trim(),
                        passwordField.getText()
                );

                AppUser savedUser = userService.createUser(newUser);
                authController.showSuccess("Usuario registrado exitosamente!");
                switchToLogin();

            } catch (RuntimeException e) {
                showAuthError(e.getMessage());
            }
        }
    }

    @FXML
    private void switchToLogin() {
        if (authController != null) {
            authController.loadLoginForm();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!validateUsername()) isValid = false;
        if (!validateEmail()) isValid = false;
        if (!validatePassword()) isValid = false;
        if (!validateConfirmPassword()) isValid = false;

        return isValid;
    }

    private boolean validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showError(usernameError, "El nombre de usuario es requerido");
            return false;
        }
        if (username.length() < 3) {
            showError(usernameError, "Minimo 3 caracteres");
            return false;
        }
        if (userService.usernameExists(username)) {
            showError(usernameError, "Este nombre de usuario ya existe");
            return false;
        }
        hideError(usernameError);
        return true;
    }

    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError(emailError, "El email es requerido");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(emailError, "Formato de email invalido");
            return false;
        }
        if (userService.emailExists(email)) {
            showError(emailError, "Este email ya está registrado");
            return false;
        }
        hideError(emailError);
        return true;
    }

    private boolean validatePassword() {
        String password = passwordField.getText();
        if (password.isEmpty()) {
            showError(passwordError, "La contraseña es requerida");
            return false;
        }
        if (password.length() < 6) {
            showError(passwordError, "Minimo 6 caracteres");
            return false;
        }
        hideError(passwordError);
        return true;
    }

    private boolean validateConfirmPassword() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (confirm.isEmpty()) {
            showError(confirmPasswordError, "Confirma tu contraseña");
            return false;
        }
        if (!password.equals(confirm)) {
            showError(confirmPasswordError, "Las contraseñas no coinciden");
            return false;
        }
        hideError(confirmPasswordError);
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
        hideError(usernameError);
        hideError(emailError);
        hideError(passwordError);
        hideError(confirmPasswordError);
        hideError(authError);
    }

    private void showAuthError(String message) {
        authError.setText(message);
        authError.setVisible(true);
    }
}