package university.jala.finalProject.springJPA.dto;

import java.time.Instant;

public class TaskCreateRequest {
    public String title;
    public String description;
    public String priority;
    public Instant expiresIn;
}
