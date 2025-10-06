package university.jala.finalProject.UI.service;

import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import university.jala.finalProject.UI.model.Task;

import java.util.Arrays;
import java.util.List;

@Service
public class TaskUIService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/api"; // tu backend

    public List<Task> getAllTasks() {
        try {
            Task[] tasks = restTemplate.getForObject(baseUrl + "/lists/1/tasks", Task[].class);
            return Arrays.asList(tasks != null ? tasks : new Task[0]);
        } catch (Exception e) {
            showError("Error al cargar tareas: " + e.getMessage());
            return List.of();
        }
    }

    public void deleteTask(Integer id) {
        try {
            restTemplate.delete(baseUrl + "/tasks/" + id);
        } catch (Exception e) {
            showError("No se pudo eliminar la tarea: " + e.getMessage());
        }
    }

    public void openCreateForm() {
        // TODO: abrir una ventana modal para crear tarea
        showInfo("Abrir formulario de creación");
    }

    public void openEditForm(Task task) {
        // TODO: abrir una ventana modal para editar tarea
        showInfo("Editar tarea: " + task.getName());
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
