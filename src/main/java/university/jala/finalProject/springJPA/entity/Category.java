package university.jala.finalProject.springJPA.entity;

import university.jala.finalProject.springJPA.entity.AppUser;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Category", schema = "databaseii")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "category_name", nullable = false, length = 60)
    private String name;

    @Column(name = "category_color", length = 7)
    private String color;

    @Column(name = "created_in")
    private Instant createdIn;

    @Column(name = "is_default")
    private Boolean isDefault;

    public Boolean getDefault() {
        return isDefault;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Instant getCreatedIn() { return createdIn; }
    public void setCreatedIn(Instant createdIn) { this.createdIn = createdIn; }

    public Integer getUser_id() { return userId; }
    public void setUser_id(Integer user) { this.userId = user; }

    public String getCategoryName() { return name; }
    public void setCategoryName(String categoryName) { this.name = categoryName; }

    public String getCategoryColor() { return color; }
    public void setCategoryColor(String categoryColor) { this.color = categoryColor; }

    public String getCreatedInString() { return createdIn != null ? createdIn.toString() : null; }
    public void setCreatedIn(String iso) { this.createdIn = (iso == null ? null : Instant.parse(iso)); }
}
