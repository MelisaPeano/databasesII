package university.jala.finalProject.UI.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.model.Task;
import university.jala.finalProject.UI.service.TaskUIService;
import university.jala.finalProject.springJPA.entity.AppUser;

import java.util.List;

@Component
public class DashboardController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, Void> actionsColumn;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;
    private AppUser currentUser;

    @Autowired
    private TaskUIService taskService;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    public void setCurrentUser(AppUser user) {
        this.currentUser = user;
        System.out.println("DashboardController - Usuario establecido: " +
                (user != null ? user.getUserName() : "null"));
    }

    @FXML
    public void initialize() {
        // Configurar columnas
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        //descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        // Agregar badges de color en la columna de estado
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                       case "NUEVA" -> setStyle("-fx-text-fill: #1E90FF; -fx-font-weight: bold;");
                        case "En Progreso" -> setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                        case "Completado" -> setStyle("-fx-text-fill: #32CD32; -fx-font-weight: bold;");
                        case "Pendiente" -> setStyle("-fx-text-fill: #FF4500; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Acciones de fila
        addActionsColumn();

        // Llenar combo de filtro
        statusFilter.setItems(FXCollections.observableArrayList("Todos", "Nueva", "En Progreso", "Completado", "Pendiente"));
        statusFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((obs, old, val) -> applyFilters());

        // Cargar tareas
        loadTasks();
    }

    private void loadTasks() {
        List<Task> tasks = taskService.getAllTasks(); // devuelve lista
        taskList.setAll(tasks);
        taskTable.setItems(taskList);
        updateStats();
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String selected = statusFilter.getValue();

        List<Task> filtered = taskService.getAllTasks().stream()
                .filter(t -> t.getName().toLowerCase().contains(search))
                .filter(t -> selected == null || selected.equals("Todos") || t.getStatus().equals(selected))
                .toList();

        taskList.setAll(filtered);
        updateStats();
    }

    private void updateStats() {
        long nuevas = taskList.stream().filter(t -> t.getStatus().equals("Nueva")).count();
        long progreso = taskList.stream().filter(t -> t.getStatus().equals("En Progreso")).count();
        long completadas = taskList.stream().filter(t -> t.getStatus().equals("Completado")).count();
        long pendientes = taskList.stream().filter(t -> t.getStatus().equals("Pendiente")).count();

        statsLabel.setText(String.format("🔵 Nueva: %d | 🟡 En progreso: %d | 🟢 Completado: %d | 🔴 Pendiente: %d",
                nuevas, progreso, completadas, pendientes));
    }

    private void addActionsColumn() {
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = col -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");

            {
                editButton.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    taskService.openEditForm(task);
                });

                deleteButton.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    taskService.deleteTask(task.getId());
                    loadTasks();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(5, editButton, deleteButton));
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void openTaskForm() {
        taskService.openCreateForm();
    }
}
