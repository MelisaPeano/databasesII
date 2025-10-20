package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class SidebarController {
    private MainLayoutController navigator;

    void setNavigator(MainLayoutController nav) {
        this.navigator = nav;
    }

    @FXML public void goHome() {
        navigator.navigateTo("HomeView.fxml");
    }

    @FXML public void goTasks() {
        navigator.navigateToAsync("TasksView.fxml");
    }

    @FXML public void goCategories() {
        navigator.navigateTo("CategoriesView.fxml");
    }

    @FXML public void goLists() {
        navigator.navigateTo("HomeView.fxml");
    }


}