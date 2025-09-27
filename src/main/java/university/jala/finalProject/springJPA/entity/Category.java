package university.jala.finalProject.springJPA;

import university.jala.finalProject.springJPA.entity.AppUser;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Category", schema = "databaseII")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Integer user_id;

    @Column(name = "category_name", nullable = false, length = 60)
    private String categoryName;

    @Column(name = "category_color", length = 7)
    private String categoryColor;

    @Column(name = "created_in")
    private String createdIn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user) {
        this.user_id = user;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getCreatedIn() {
        return createdIn;
    }

    public void setCreatedIn(String createdIn) {
        this.createdIn = createdIn;
    }

}