package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Task_status", schema = "databaseII")
public class TaskStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_status_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskState status;

    @Column(name = "changed_in")
    private Instant changedIn;

    @Column(name = "comment")
    private String comment;

    public Integer getId() { return id; }
    public Task getTask() { return task; }
    public TaskState getStatus() { return status; }
    public Instant getChangedIn() { return changedIn; }
    public String getComment() { return comment; }

    public void setId(Integer id) { this.id = id; }
    public void setTask(Task task) { this.task = task; }
    public void setStatus(TaskState status) { this.status = status; }
    public void setChangedIn(Instant changedIn) { this.changedIn = changedIn; }
    public void setComment(String comment) { this.comment = comment; }
}
