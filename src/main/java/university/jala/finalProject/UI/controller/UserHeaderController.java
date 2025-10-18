package university.jala.finalProject.UI.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.service.UserService;

@Component
public class UserHeaderController {

    private final UserService userService;

    @FXML private Label userName;

    public UserHeaderController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        String display = userService.getCurrentUser()
                .map(AppUser::getUserName)
                .orElse("Usuario");
        userName.setText(display);
    }

    @FXML
    public void logout() {
        userService.logout();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gracias");
        alert.setHeaderText("Gracias por usar nuestra app de listas");
        alert.setContentText("Creadores:\n• Ira Frias\n• Antonio Blinda\n• Melisa Peano");

        alert.setOnShown(event -> {
            Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
            s.setAlwaysOnTop(true);
        });

        alert.showAndWait();

        Platform.exit();
        System.exit(0);
    }

}
