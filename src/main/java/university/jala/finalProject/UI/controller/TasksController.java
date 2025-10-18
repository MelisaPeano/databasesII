package university.jala.finalProject.UI.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.model.Task;
import university.jala.finalProject.springJPA.dto.TaskCreateRequest;
import university.jala.finalProject.springJPA.service.TaskService;

import java.sql.*;
import java.util.HashMap;
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


    @Autowired
    private TaskService taskService;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    private static final String URL ="jdbc:mysql://localhost:3306/databaseii";
    private static final String USER ="melisa";
    private static final String PASS ="TuPassAquiMiPana";
    private Integer currentListId;
    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> listMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadCategories();

        cbCategory.setOnAction(e -> {
            String selectedCategory = cbCategory.getValue();
            if (selectedCategory != null) {
                Integer categoryId = categoryMap.get(selectedCategory);
                loadListsByCategory(categoryId);
            }
        });

        colCategory.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoryName()));
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colDescription.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());

        // Hacer editable
        taskTable.setEditable(true);
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        // Guardar edición en base de datos
        colName.setOnEditCommit(e -> updateField(e.getRowValue(), "task_title", e.getNewValue()));
        colDescription.setOnEditCommit(e -> updateField(e.getRowValue(), "task_description", e.getNewValue()));

        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());

        colStatus.setCellFactory(col -> {
            TableCell<Task, String> cell = new TableCell<>() {
                private final ComboBox<String> combo = new ComboBox<>();

                {
                    combo.getItems().addAll("NEW", "IN_PROGRESS", "DONE", "CANCELLED");
                    combo.setOnAction(e -> {
                        Task task = getTableView().getItems().get(getIndex());
                        if (task != null) {
                            String oldStatus = task.getStatus();      // guardamos el estado actual
                            String newStatus = combo.getValue();
                            if (!newStatus.equals(oldStatus)) {       // solo si cambió
                                task.setStatus(newStatus);
                                updateField(task, "task_status", newStatus);
                                showMessage("Estado de la tarea actualizado a: " + newStatus);
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        combo.setValue(status != null ? status : "NEW");
                        setGraphic(combo);
                    }
                }
            };
            return cell;
        });


        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Eliminar");

            {
                btnDelete.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    deleteTask(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });

        taskTable.setItems(taskList);
        loadTasks();
    }
    public void setCurrentListId(Integer listId) {
        this.currentListId = listId;
    }


    private void updateField(Task task, String field, String newValue) {
        String query = "UPDATE Task SET " + field + " = ? WHERE task_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newValue);
            ps.setInt(2, task.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                showMessage("Se actualizó correctamente " + field);
            }
        } catch (SQLException e) {
            showError("Error al actualizar " + field);
        }
    }

    private void loadTasks() {
        pi.setVisible(true);
        pi.setManaged(true);
        taskList.clear();

        new Thread(() -> {
            final String SQL_ALL =
                    "SELECT t.task_id, t.task_title, t.task_description, t.task_status, " +
                            "       c.category_name AS category_name " +
                            "FROM Task t " +
                            "JOIN List l ON l.list_id = t.list_id " +
                            "JOIN Category c ON c.category_id = l.category_id " +
                            "ORDER BY t.task_id";

            final String SQL_BY_LIST =
                    "SELECT t.task_id, t.task_title, t.task_description, t.task_status, " +
                            "       c.category_name AS category_name " +
                            "FROM Task t " +
                            "JOIN List l ON l.list_id = t.list_id " +
                            "JOIN Category c ON c.category_id = l.category_id " +
                            "WHERE t.list_id = ? " +
                            "ORDER BY t.task_id";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = (currentListId != null)
                         ? conn.prepareStatement(SQL_BY_LIST)
                         : conn.prepareStatement(SQL_ALL)) {

                if (currentListId != null) {
                    ps.setInt(1, currentListId);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Task t = new Task();
                        t.setId(rs.getInt("task_id"));
                        t.setName(rs.getString("task_title"));
                        t.setDescription(rs.getString("task_description"));
                        t.setStatus(rs.getString("task_status"));
                        t.setCategoryName(rs.getString("category_name")); // <-- ahora sí existe
                        taskList.add(t);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error al cargar tareas: " + e.getMessage());
            } finally {
                javafx.application.Platform.runLater(() -> {
                    taskTable.setItems(taskList);
                    pi.setVisible(false);
                    pi.setManaged(false);
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

        // Obtener la lista seleccionada del ComboBox
        String selectedListName = cbList.getValue();
        if (selectedListName == null) {
            showError("Seleccione una lista para la tarea");
            return;
        }
        Integer listId = listMap.get(selectedListName);

        TaskCreateRequest req = new TaskCreateRequest();
        req.title = name;
        req.description = desc;

        try {
            var created = taskService.create(listId, req);

            Task t = new Task();
            t.setId(created.id);
            t.setName(created.title);
            t.setDescription(created.description);
            t.setStatus(created.status);
            t.setCategoryName(cbCategory.getValue() != null ? cbCategory.getValue() : "");

            taskList.add(t);
            txtName.clear();
            txtDescription.clear();
            showMessage("Tarea agregada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al agregar tarea: " + e.getMessage());
        }
    }

    private void deleteTask(Task task) {
        String deleteStatusSql = "DELETE FROM Task_status WHERE task_id = ?";
        String deleteTaskSql   = "DELETE FROM Task WHERE task_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            try (PreparedStatement ps1 = conn.prepareStatement(deleteStatusSql)) {
                ps1.setInt(1, task.getId());
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(deleteTaskSql)) {
                ps2.setInt(1, task.getId());
                ps2.executeUpdate();
            }
            taskList.remove(task);
            showMessage("Tarea eliminada correctamente");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error al eliminar tarea: " + e.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        loadTasks();
    }

    private void showError(String msg) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.showAndWait();
        });
    }

    private void showMessage(String msg) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
            alert.showAndWait();
        });
    }
    private void loadCategories() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category_id, category_name FROM Category")) {

            while (rs.next()) {
                int id = rs.getInt("category_id");
                String name = rs.getString("category_name");
                categoryMap.put(name, id);
                cbCategory.getItems().add(name);
            }

        } catch (SQLException e) {
            showError("Error al cargar categorías");
            e.printStackTrace();
        }
    }

    private void loadListsByCategory(Integer categoryId) {
        cbList.getItems().clear();
        listMap.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement("SELECT list_id, list_name FROM List WHERE category_id = ?")) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("list_id");
                String name = rs.getString("list_name");
                listMap.put(name, id);
                cbList.getItems().add(name);
            }
        } catch (SQLException e) {
            showError("Error al cargar listas");
            e.printStackTrace();
        }
    }


}
