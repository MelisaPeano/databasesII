package university.jala.finalProject.springJPA.dto;

import university.jala.finalProject.springJPA.entity.Task;

import java.time.Instant;

public class TaskResponse {
    public Integer id;
    public Integer listId;
    public String title;
    public String description;
    public String status;
    public String priority;
    public Instant createdIn;
    public Instant completedIn;
    public Instant expiresIn;

    public static TaskResponse fromEntity(Task entity) {
        TaskResponse dto = new TaskResponse();
        dto.id = entity.getId();
        dto.title = entity.getTitle();
        dto.description = entity.getDescription();
        dto.status = entity.getStatus() != null ? entity.getStatus().name() : null;
        dto.listId = entity.getList() != null ? entity.getList().getId() : null;
        return dto;
    }
}
