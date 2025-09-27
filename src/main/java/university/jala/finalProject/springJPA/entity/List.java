package university.jala.finalProject.springJPA.entity;

import university.jala.finalProject.springJPA.Category;

import javax.persistence.*;

@Entity
@Table(name = "List", schema = "databaseII")
public class List {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", nullable = false)
    private Integer listId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "list_name", nullable = false, length = 60)
    private String listName;

    @Column(name = "list_description")
    private String listDescription;

    @Column(name = "created_in")
    private String createdIn;

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer id) {
        this.listId = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListDescription() {
        return listDescription;
    }

    public void setListDescription(String listDescription) {
        this.listDescription = listDescription;
    }

    public String getCreatedIn() {
        return createdIn;
    }

    public void setCreatedIn(String createdIn) {
        this.createdIn = createdIn;
    }

}