package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userName.getScene().getWindow();
            stage.setScene(new Scene(root));  // reemplazar la escena
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
