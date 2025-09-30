package university.jala.finalProject.springJPA.dto;

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
}
