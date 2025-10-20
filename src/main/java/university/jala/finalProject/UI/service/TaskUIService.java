package university.jala.finalProject.UI.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import university.jala.finalProject.UI.model.Task;
import university.jala.finalProject.springJPA.dto.TaskCreateRequest;
import university.jala.finalProject.springJPA.dto.TaskStatusChangeRequest;
import university.jala.finalProject.springJPA.dto.TaskUpdateRequest;
import university.jala.finalProject.springJPA.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskUIService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CurrentUserService currentUserService;

    /* -------------------- READ -------------------- */
    public List<Task> getAllTasksByList(Integer listId) {
        try {
            Integer userId = currentUserService.getCurrentUserId();
            var responses = taskService.list(userId, listId, null);
            return responses.stream()
                    .map(Task::fromResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            showError("Error al cargar tareas: " + e.getMessage());
            return List.of();
        }
    }

    public List<Task> getAllTasks() {
        try {
            Integer userId = currentUserService.getCurrentUserId();
            var responses = taskService.list(userId, null, null);
            System.out.println("✅ [TaskUIService] Tareas del usuario " + userId + ": " + responses.size());
            return responses.stream()
                    .map(Task::fromResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            showError("Error al cargar todas las tareas");
            return List.of();
        }
    }

    /* -------------------- CREATE -------------------- */
    public Task createTask(Integer listId, String title, String description) {
        try {
            TaskCreateRequest req = new TaskCreateRequest();
            req.title = title;
            req.description = description;

            var created = taskService.create(listId, req);
            showInfo("Tarea creada correctamente");
            return Task.fromResponse(created);
        } catch (Exception e) {
            showError("Error al crear tarea: " + e.getMessage());
            return null;
        }
    }

    /* -------------------- UPDATE -------------------- */
    public void updateTask(Integer taskId, String title, String description) {
        try {
            TaskUpdateRequest req = new TaskUpdateRequest();
            req.title = title;
            req.description = description;
            taskService.update(taskId, req);
            showInfo("Tarea actualizada correctamente");
        } catch (Exception e) {
            showError("Error al actualizar tarea: " + e.getMessage());
        }
    }

    public void changeStatus(Integer taskId, String newStatus) {
        try {
            TaskStatusChangeRequest req = new TaskStatusChangeRequest();
            req.status = newStatus;
            req.comment = "Cambio desde la interfaz de usuario";
            taskService.changeStatus(taskId, req);
            showInfo("Estado actualizado a " + newStatus);
        } catch (Exception e) {
            showError("Error al cambiar estado: " + e.getMessage());
        }
    }

    /* -------------------- DELETE -------------------- */
    public void deleteTask(Integer id) {
        try {
            taskService.delete(id);
            showInfo("Tarea eliminada correctamente");
        } catch (Exception e) {
            showError("Error al eliminar tarea: " + e.getMessage());
        }
    }

    /* -------------------- FORM PLACEHOLDERS -------------------- */
    public void openCreateForm() {
        // TODO: abrir una ventana modal para crear tarea
        showInfo("Abrir formulario de creación");
    }

    public void openEditForm(Task task) {
        // TODO: abrir una ventana modal para editar tarea
        showInfo("Editar tarea: " + task.getName());
    }

    /* -------------------- UI Helpers -------------------- */
    private void showError(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }

    private void showInfo(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, msg).showAndWait());
    }

    public List<Task> getAllTasksByUser(Integer userId) {
        try {
            var responses = taskService.getTasksByUser(userId);
            return responses.stream()
                    .map(Task::fromResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al cargar tareas del usuario: " + e.getMessage());
            return List.of();
        }
    }
}
