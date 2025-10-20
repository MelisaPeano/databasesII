package university.jala.finalProject.springJPA.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import university.jala.finalProject.springJPA.dto.*;
import university.jala.finalProject.springJPA.entity.*;
import university.jala.finalProject.springJPA.repository.ListRepository;
import university.jala.finalProject.springJPA.repository.TaskRepository;
import university.jala.finalProject.springJPA.repository.TaskStatusHistoryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepo;
    private final ListRepository listRepo;
    private final TaskStatusHistoryRepository historyRepo;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;

    public TaskService(TaskRepository taskRepo, ListRepository listRepo,
                       TaskStatusHistoryRepository historyRepo, TaskStatusHistoryRepository taskStatusHistoryRepository) {
        this.taskRepo = taskRepo;
        this.listRepo = listRepo;
        this.historyRepo = historyRepo;
        this.taskStatusHistoryRepository = taskStatusHistoryRepository;
    }

    private TaskPriority parsePriority(String p) {
        if (p == null) return null;
        return TaskPriority.valueOf(p.trim().toUpperCase(Locale.ROOT));
    }

    private TaskState parseStatus(String s) {
        if (s == null) return null;
        String v = s.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace('Á','A').replace('É','E').replace('Í','I').replace('Ó','O').replace('Ú','U');
        switch (v) {
            case "NUEVA": case "NUEVO": case "PENDIENTE": return TaskState.NEW;
            case "EN_PROGRESO": return TaskState.IN_PROGRESS;
            case "COMPLETADO": case "COMPLETADA": return TaskState.DONE;
            default: return TaskState.valueOf(v);
        }
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.id = t.getId();
        r.listId = t.getList().getId();
        r.title = t.getTitle();
        r.description = t.getDescription();
        r.status = t.getStatus().name();
        r.priority = t.getPriority() == null ? null : t.getPriority().name();
        r.createdIn = t.getCreatedIn();
        r.completedIn = t.getCompletedIn();
        r.expiresIn = t.getExpiresIn();
        return r;
    }

    @Transactional
    public TaskResponse create(Integer listId, TaskCreateRequest req) {
        ListTable taskListTable =
                listRepo.findById(listId)
                        .orElseThrow(() -> new IllegalArgumentException("Lista no encontrada"));

        if (taskRepo.existsByListTable_IdAndTitleIgnoreCase(listId, req.title)) {
            throw new IllegalStateException("Ya existe una tarea con ese nombre en la lista");
        }

        Task t = new Task();
        t.setList(taskListTable);
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setPriority(parsePriority(req.priority));
        t.setExpiresIn(req.expiresIn);
        t.setStatus(TaskState.NEW);
        t.setCreatedIn(Instant.now());

        Task saved = taskRepo.save(t);

        // historial
        TaskStatusHistory h = new TaskStatusHistory();
        h.setTask(saved);
        h.setStatus(TaskState.NEW);
        h.setChangedIn(Instant.now());
        historyRepo.save(h);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public java.util.List<TaskResponse> list(Integer userId, Integer listId, String statusOpt) {
        TaskState st = parseStatus(statusOpt);
        java.util.List<Task> items;

        if (userId != null && listId == null && st == null) {
            // ✅ Todas las tareas del usuario actual
            items = taskRepo.findAllByUserId(userId);
        } else if (userId != null && listId == null) {
            // ✅ Todas las tareas del usuario por estado
            items = taskRepo.findAllByUserIdAndStatus(userId, st);
        } else if (st == null) {
            // ✅ Todas las tareas de una lista específica
            items = taskRepo.findByListTable_Id(listId);
        } else {
            // ✅ Tareas de una lista específica por estado
            items = taskRepo.findByListTable_IdAndStatus(listId, st);
        }

        System.out.println("🟢 [TaskService] Usuario: " + userId +
                " | Lista: " + listId +
                " | Estado: " + st +
                " | Total: " + items.size());

        return items.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }



    @Transactional
    public TaskResponse update(Integer taskId, TaskUpdateRequest req) {
        Task t = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        if (req.title != null && !req.title.equalsIgnoreCase(t.getTitle())) {
            if (taskRepo.existsByListTable_IdAndTitleIgnoreCase(t.getList().getId(), req.title)) {
                throw new IllegalStateException("Ya existe una tarea con ese nombre en la lista");
            }
            t.setTitle(req.title);
        }
        if (req.description != null) t.setDescription(req.description);
        if (req.priority != null) t.setPriority(parsePriority(req.priority));
        if (req.expiresIn != null) t.setExpiresIn(req.expiresIn);

        return toResponse(taskRepo.save(t));
    }

    @Transactional
    public TaskResponse changeStatus(Integer taskId, TaskStatusChangeRequest req) {
        Task t = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        TaskState newState = parseStatus(req.status);
        if (newState == null) throw new IllegalArgumentException("Estado inválido");

        t.setStatus(newState);
        if (newState == TaskState.DONE) {
            t.setCompletedIn(Instant.now());
        } else {
            t.setCompletedIn(null);
        }
        Task saved = taskRepo.save(t);

        TaskStatusHistory h = new TaskStatusHistory();
        h.setTask(saved);
        h.setStatus(newState);
        h.setComment(req.comment);
        h.setChangedIn(Instant.now());
        historyRepo.save(h);

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer taskId) {
        if (!taskRepo.existsById(taskId)) throw new IllegalArgumentException("Tarea no encontrada");
        taskRepo.deleteById(taskId);
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByUser(Integer userId) {
        var tasks = taskRepo.findByListTable_Category_UserId(userId);
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
