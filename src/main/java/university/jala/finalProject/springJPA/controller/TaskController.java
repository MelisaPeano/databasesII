package university.jala.finalProject.springJPA.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import university.jala.finalProject.springJPA.dto.*;
import university.jala.finalProject.springJPA.service.TaskService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    // POST /api/lists/:listId/tasks
    @PostMapping("/lists/{listId}/tasks")
    public ResponseEntity<TaskResponse> create(
            @PathVariable Integer listId,
            @RequestBody TaskCreateRequest req) {
        TaskResponse r = service.create(listId, req);
        return ResponseEntity.created(URI.create("/api/tasks/" + r.id)).body(r);
    }

    // GET /api/lists/:listId/tasks?status=tatata
    @GetMapping("/lists/{listId}/tasks")
    public List<TaskResponse> list(
            @PathVariable Integer listId,
            @RequestParam(value = "status", required = false) String status) {
        return service.list(listId, status);
    }

    // PUT /api/tasks/:id
    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Integer id, @RequestBody TaskUpdateRequest req) {
        return service.update(id, req);
    }

    // PATCH estado: /api/tasks/:id/status
    @PatchMapping("/tasks/{id}/status")
    public TaskResponse changeStatus(@PathVariable Integer id, @RequestBody TaskStatusChangeRequest req) {
        return service.changeStatus(id, req);
    }

    // DELETE /api/tasks/:id
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
