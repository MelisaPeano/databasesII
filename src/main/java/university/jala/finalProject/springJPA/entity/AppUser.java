package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AppUser")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 100)
    private String userPassword;

    @Column(name = "created_in")
    private LocalDateTime createdIn;

    public AppUser() {}

    public AppUser(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.createdIn = LocalDateTime.now();
    }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPassword() { return userPassword; }

    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public LocalDateTime getCreatedIn() { return createdIn; }

    public void setCreatedIn(LocalDateTime createdIn) { this.createdIn = createdIn; }

    @PrePersist
    protected void onCreate() {
        createdIn = LocalDateTime.now();
    }
}