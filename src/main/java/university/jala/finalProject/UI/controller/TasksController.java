package university.jala.finalProject.UI.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.model.Task;
import university.jala.finalProject.UI.service.CategoryServiceUI;
import university.jala.finalProject.UI.service.CurrentUserService;
import university.jala.finalProject.UI.service.TaskUIService;
import university.jala.finalProject.springJPA.service.CategoryService;
import university.jala.finalProject.springJPA.service.CategoryServiceImpl;
import university.jala.finalProject.springJPA.service.ListService;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.entity.ListTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TasksController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colCategory;
    @FXML private TableColumn<Task, String> colName;
    @FXML private TableColumn<Task, String> colDescription;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private TableColumn<Task, Void> colActions;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private ProgressIndicator pi;
    @FXML private ComboBox<String> cbCategory;
    @FXML private ComboBox<String> cbList;

    @Autowired private TaskUIService taskUIService;
    @Autowired private ListService listService;
    @Autowired private CurrentUserService currentUserService;



    private final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final Map<String, Integer> listMap = new HashMap<>();
    private Integer currentListId;
    @Autowired
    private CategoryServiceUI categoryServiceUI;

    @FXML
    public void initialize() {
        setupColumns();
        loadCategories();

        cbCategory.setOnAction(e -> {
            String selectedCategory = cbCategory.getValue();
            if (selectedCategory != null) {
                Integer categoryId = categoryMap.get(selectedCategory);
                loadListsByCategory(categoryId);
            }
        });

        taskTable.setItems(taskList);
        loadTasks();
    }

    public void setCurrentListId(Integer listId) {
        this.currentListId = listId;
    }

    /* ------------------- Configuración columnas ------------------- */
    private void setupColumns() {
        colCategory.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoryName()));
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colDescription.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());

        taskTable.setEditable(true);
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        colName.setOnEditCommit(e -> {
            Task task = e.getRowValue();
            task.setName(e.getNewValue());
            taskUIService.updateTask(task.getId(), e.getNewValue(), task.getDescription());
        });

        colDescription.setOnEditCommit(e -> {
            Task task = e.getRowValue();
            task.setDescription(e.getNewValue());
            taskUIService.updateTask(task.getId(), task.getName(), e.getNewValue());
        });

        // ComboBox para actualizar estado
        colStatus.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>();

            {
                combo.getItems().addAll("NEW", "IN_PROGRESS", "DONE", "CANCELLED");
                combo.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    if (task != null) {
                        String newStatus = combo.getValue();
                        task.setStatus(newStatus);
                        taskUIService.changeStatus(task.getId(), newStatus);
                    }
                });
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) setGraphic(null);
                else {
                    combo.setValue(status != null ? status : "NEW");
                    setGraphic(combo);
                }
            }
        });

        // Columna acciones (eliminar)
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Eliminar");

            {
                btnDelete.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    taskUIService.deleteTask(task.getId());
                    taskList.remove(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    /* ------------------- CRUD OPERATIONS ------------------- */
    private void loadTasks() {
        pi.setVisible(true);
        taskList.clear();

        new Thread(() -> {
            try {
                Integer userId = currentUserService.getCurrentUserId();
                if (userId == null) {
                    Platform.runLater(() -> {
                        pi.setVisible(false);
                        showError("No hay un usuario activo. Inicie sesión primero.");
                    });
                    return;
                }

                System.out.println("🔵 Cargando todas las tareas del usuario: " + userId);
                List<Task> tasks = taskUIService.getAllTasksByUser(userId);
                System.out.println("🟢 Total de tareas encontradas: " + tasks.size());

                Platform.runLater(() -> {
                    taskList.setAll(tasks);
                    pi.setVisible(false);

                    if (tasks.isEmpty()) {
                        showError("No hay tareas registradas para este usuario.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    pi.setVisible(false);
                    showError("Error al cargar tareas: " + e.getMessage());
                });
            }
        }).start();
    }


    @FXML
    private void onAddTask() {
        String name = txtName.getText().trim();
        String desc = txtDescription.getText().trim();

        if (name.isBlank()) {
            showError("El nombre no puede estar vacío");
            return;
        }

        String selectedList = cbList.getValue();
        if (selectedList == null) {
            showError("Seleccione una lista para la tarea");
            return;
        }

        Integer listId = listMap.get(selectedList);
        Task created = taskUIService.createTask(listId, name, desc);

        if (created != null) {
            taskList.add(created);
            txtName.clear();
            txtDescription.clear();
        }
    }

    @FXML
    private void onRefresh() {
        loadTasks();
    }

    /* ------------------- Cargar categorías y listas ------------------- */
    private void loadCategories() {
        try {
            Integer userId = currentUserService.getCurrentUserId();

            if (userId == null) {
                showError("No hay un usuario activo. Inicie sesión primero.");
                return;
            }
            var categories = categoryServiceUI.getCategoriesByUser(userId);
            for (Category c : categories) {
                categoryMap.put(c.getName(), c.getId());
                cbCategory.getItems().add(c.getName());
            }
        } catch (Exception e) {
            showError("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void loadListsByCategory(Integer categoryId) {
        cbList.getItems().clear();
        listMap.clear();
        try {
            Integer userId = currentUserService.getCurrentUserId();
            if (userId == null) {
                showError("No hay un usuario activo. Inicie sesión primero.");
                return;
            }

            System.out.println("🟡 Cargando listas para categoryId=" + categoryId + ", userId=" + userId);
            var lists = listService.getListsByCategoryAndUser(categoryId, userId);

            if (lists.isEmpty()) {
                System.out.println("⚠️ No se encontraron listas para esta categoría.");
            }

            for (ListTable l : lists) {
                listMap.put(l.getName(), l.getId());
                cbList.getItems().add(l.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al cargar listas: " + e.getMessage());
        }
    }


    /* ------------------- Utilidades UI ------------------- */
    private void showError(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }
}
