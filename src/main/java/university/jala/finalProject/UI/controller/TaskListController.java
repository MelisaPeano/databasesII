package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import university.jala.finalProject.UI.model.Task;

public class TaskListController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private ComboBox<String> filterCombo;

    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());

        filterCombo.setItems(FXCollections.observableArrayList("Todos", "Nueva", "En Progreso", "Completado", "Pendiente"));
        filterCombo.getSelectionModel().select("Todos");

    }

    @FXML
    public void onFilterChanged() {
        String selected = filterCombo.getValue();
        // aquí filtrarías las tareas (puede hacerse con stream o consulta al backend)
    }
}
