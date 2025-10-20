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
    private final TaskService taskService;

    public TaskController(TaskService service, TaskService taskService) { this.service = service;
        this.taskService = taskService;
    }

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
    public ResponseEntity<List<TaskResponse>> list(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer listId,
            @RequestParam(required = false) String status
    ) {
        var tasks = taskService.list(userId, listId, status);
        return ResponseEntity.ok(tasks);
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
