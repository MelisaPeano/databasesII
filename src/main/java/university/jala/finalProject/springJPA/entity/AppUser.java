package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "AppUser", uniqueConstraints = @UniqueConstraint(columnNames = "user_email"), schema = "databaseII")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 100)
    private String userPassword;

    @Column(name = "created_in")
    private Instant createdIn;

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Instant getCreatedIn() {
        return createdIn;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setUserPassword(String encode) {
        this.userPassword = encode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setCreatedIn(Instant createdIn) {
        this.createdIn = createdIn;
    }

}