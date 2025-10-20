package university.jala.finalProject.springJPA.dto;

import java.time.Instant;

public class TaskUpdateRequest {
    public String title;
    public String description;
    public String priority;
    public Instant expiresIn;
}
