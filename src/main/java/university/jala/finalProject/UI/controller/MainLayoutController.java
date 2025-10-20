package university.jala.finalProject.UI.controller;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.util.ViewLoader;


@Component
public class MainLayoutController {
    @FXML private StackPane centerPane; // Zona de contenido


    // Controllers incluidos
    @FXML private SidebarController sidebarIncludeController; // tomado por fx:id del <fx:include>
    @FXML private UserHeaderController headerIncludeController;


    @FXML private StackPane loadingInclude; // overlay desde LoadingSpinner.fxml


    @Autowired private ViewLoader viewLoader;


    @FXML
    public void initialize() {
        if (sidebarIncludeController != null) {
            sidebarIncludeController.setNavigator(this);
        }
        navigateTo("HomeView.fxml");
    }


    public void showLoading(boolean show) {
        loadingInclude.setVisible(show);
        loadingInclude.setManaged(show);
    }


    /** Carga sincrónica (rápida) */
    public void navigateTo(String fxml) {
        showLoading(true);

        try {
            Node view = viewLoader.load(fxml);
            centerPane.getChildren().setAll(view);
        } finally {
            showLoading(false);
        }
    }


    /** Carga asíncrona (para vistas pesadas o llamadas a BD) */
    public void navigateToAsync(String fxml) {
        showLoading(true);
        System.out.println("Cargando vista: " + fxml);
        javafx.concurrent.Task<Node> task = new javafx.concurrent.Task<>() {
            @Override protected Node call() { return viewLoader.load(fxml); }
        };
        task.setOnSucceeded(e -> {
            System.out.println("Vista cargada correctamente: " + fxml);
            centerPane.getChildren().setAll(task.getValue());
            showLoading(false);
        });
        task.setOnFailed(e -> {
            System.err.println("❌ Error al cargar la vista: " + fxml);
            task.getException().printStackTrace(); // 👈 muestra la causa real
            showLoading(false);
        });
        new Thread(task, "load-" + fxml).start();
    }
}