package university.jala.finalProject.UI.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.stereotype.Component;
import university.jala.finalProject.UI.model.Task;

import java.sql.*;
@Component
public class TasksController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, Number> colId;
    @FXML private TableColumn<Task, String> colName;
    @FXML private TableColumn<Task, String> colDescription;
    @FXML private TableColumn<Task, String> colStatus;
    @FXML private TableColumn<Task, Void> colActions;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private ProgressIndicator pi;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    private static final String URL ="jdbc:mysql://localhost:3306/databaseii";
    private static final String USER ="melisa";
    private static final String PASS ="TuPasswordSegura";

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
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

        // Columna de estado con ComboBox
        colStatus.setCellFactory(col -> {
            TableCell<Task, String> cell = new TableCell<>() {
                private final ComboBox<String> combo = new ComboBox<>();

                {
                    combo.getItems().addAll("NEW", "IN_PROGRESS", "DONE", "CANCELLED");
                    combo.setOnAction(e -> {
                        Task task = getTableView().getItems().get(getIndex());
                        if (task != null) {
                            String newStatus = combo.getValue();
                            task.setStatus(newStatus);
                            updateField(task, "task_status", newStatus);
                            showMessage("Estado de la tarea actualizado a: " + newStatus);
                        }
                    });
                }

                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        combo.setValue(status);
                        setGraphic(combo);
                    }
                }
            };
            return cell;
        });

        // Acciones (Eliminar)
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
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT task_id, task_title, task_description, task_status FROM Task")) {

                while (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getInt("task_id"));
                    t.setName(rs.getString("task_title"));
                    t.setDescription(rs.getString("task_description"));
                    t.setStatus(rs.getString("task_status"));
                    taskList.add(t);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error al cargar tareas");
            } finally {
                javafx.application.Platform.runLater(() -> {
                    pi.setVisible(false);
                    pi.setManaged(false);
                });
            }
        }).start();
    }

    @FXML
    private void onAddTask() {
        String name = txtName.getText();
        String desc = txtDescription.getText();
        if (name.isBlank()) {
            showError("El nombre no puede estar vacío");
            return;
        }

        String sql = "INSERT INTO Task (task_title, task_description, task_status) VALUES (?, ?, 'NEW')";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, desc);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                Task t = new Task();
                t.setId(keys.getInt(1));
                t.setName(name);
                t.setDescription(desc);
                t.setStatus("NEW");
                taskList.add(t);
            }

            txtName.clear();
            txtDescription.clear();
            showMessage("Tarea agregada correctamente");
        } catch (SQLException e) {
            e.printStackTrace(); // imprime en consola el error real
            showError("Error al agregar tarea: " + e.getMessage());
        }
    }

    private void deleteTask(Task task) {
        String deleteStatusSql = "DELETE FROM task_status WHERE task_id = ?";
        String deleteTaskSql = "DELETE FROM task WHERE task_id = ?";
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

}
