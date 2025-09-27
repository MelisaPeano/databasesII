package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "refresh_token",schema = "databaseII")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private AppUser user;

    public RefreshToken() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

}