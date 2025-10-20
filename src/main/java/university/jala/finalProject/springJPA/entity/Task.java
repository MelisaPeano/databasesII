package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Task", schema = "databaseII")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private List list;

    @Column(name = "task_title", nullable = false, length = 100)
    private String title;

    @Column(name = "task_description")
    private String description;

    @Column(name = "expires_in")
    private Instant expiresIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskState status = TaskState.NEW;

    @Column(name = "created_in")
    private Instant createdIn;

    @Column(name = "completed_in")
    private Instant completedIn;

    public Integer getId() { return id; }
    public List getList() { return list; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instant getExpiresIn() { return expiresIn; }
    public TaskPriority getPriority() { return priority; }
    public TaskState getStatus() { return status; }
    public Instant getCreatedIn() { return createdIn; }
    public Instant getCompletedIn() { return completedIn; }

    public void setId(Integer id) { this.id = id; }
    public void setList(List list) { this.list = list; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setExpiresIn(Instant expiresIn) { this.expiresIn = expiresIn; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setStatus(TaskState status) { this.status = status; }
    public void setCreatedIn(Instant createdIn) { this.createdIn = createdIn; }
    public void setCompletedIn(Instant completedIn) { this.completedIn = completedIn; }
}
