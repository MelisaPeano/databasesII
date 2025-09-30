package university.jala.finalProject.springJPA.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "List", schema = "databaseii")
public class List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "list_name", nullable = false, length = 60)
    private String name;

    @Column(name = "list_description")
    private String description;

    @Column(name = "created_in")
    private Instant createdIn;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedIn() { return createdIn; }
    public void setCreatedIn(Instant createdIn) { this.createdIn = createdIn; }

    public Integer getListId() { return id; }
    public void setListId(Integer id) { this.id = id; }

    public Integer getCategoryId() { return category != null ? category.getId() : null; }
    public void setCategoryId(Integer categoryId) {
        if (categoryId == null) { this.category = null; }
        else { Category c = new Category(); c.setId(categoryId); this.category = c; }
    }

    public String getListName() { return name; }
    public void setListName(String listName) { this.name = listName; }

    public String getListDescription() { return description; }
    public void setListDescription(String listDescription) { this.description = listDescription; }

    public String getCreatedInString() { return createdIn != null ? createdIn.toString() : null; }
    public void setCreatedIn(String iso) { this.createdIn = (iso == null ? null : Instant.parse(iso)); }
}
