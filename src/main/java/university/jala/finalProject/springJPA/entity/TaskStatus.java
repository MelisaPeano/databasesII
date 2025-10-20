package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Task_status", schema = "databaseII")
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_status_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "changed_in")
    private Instant changedIn;

    @Column(name = "comment", length = 100)
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getChangedIn() {
        return changedIn;
    }

    public void setChangedIn(Instant changedIn) {
        this.changedIn = changedIn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}