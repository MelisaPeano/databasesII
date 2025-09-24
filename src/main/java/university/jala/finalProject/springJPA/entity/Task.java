package university.jala.finalProject.springJPA;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Task", schema = "databaseII")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private List list;

    @Column(name = "task_title", nullable = false, length = 100)
    private String taskTitle;

    @Lob
    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "expires_in")
    private Instant expiresIn;

    @Lob
    @Column(name = "priority")
    private String priority;

    @Lob
    @Column(name = "task_status")
    private String taskStatus;

    @Column(name = "created_in")
    private Instant createdIn;

    @Column(name = "completed_in")
    private Instant completedIn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Instant getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Instant expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Instant getCreatedIn() {
        return createdIn;
    }

    public void setCreatedIn(Instant createdIn) {
        this.createdIn = createdIn;
    }

    public Instant getCompletedIn() {
        return completedIn;
    }

    public void setCompletedIn(Instant completedIn) {
        this.completedIn = completedIn;
    }

}