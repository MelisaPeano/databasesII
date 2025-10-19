package university.jala.finalProject.UI.model;

import javafx.beans.property.*;
import university.jala.finalProject.springJPA.dto.TaskResponse;

public class Task {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty createdIn = new SimpleStringProperty();
    private final StringProperty completedIn = new SimpleStringProperty();
    private final StringProperty expiresIn = new SimpleStringProperty();
    private final StringProperty categoryName = new SimpleStringProperty();

    /* -------------------- Getters & Setters -------------------- */
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    public String getPriority() { return priority.get(); }
    public void setPriority(String priority) { this.priority.set(priority); }
    public StringProperty priorityProperty() { return priority; }

    public String getCreatedIn() { return createdIn.get(); }
    public void setCreatedIn(String createdIn) { this.createdIn.set(createdIn); }
    public StringProperty createdInProperty() { return createdIn; }

    public String getCompletedIn() { return completedIn.get(); }
    public void setCompletedIn(String completedIn) { this.completedIn.set(completedIn); }
    public StringProperty completedInProperty() { return completedIn; }

    public String getExpiresIn() { return expiresIn.get(); }
    public void setExpiresIn(String expiresIn) { this.expiresIn.set(expiresIn); }
    public StringProperty expiresInProperty() { return expiresIn; }

    public String getCategoryName() { return categoryName.get(); }
    public void setCategoryName(String categoryName) { this.categoryName.set(categoryName); }
    public StringProperty categoryNameProperty() { return categoryName; }

    /* -------------------- Conversión desde DTO -------------------- */
    public static Task fromResponse(TaskResponse dto) {
        Task t = new Task();
        t.setId(dto.id != null ? dto.id : 0);
        t.setName(dto.title != null ? dto.title : "");
        t.setDescription(dto.description != null ? dto.description : "");
        t.setStatus(dto.status != null ? dto.status : "NEW");
        t.setPriority(dto.priority != null ? dto.priority : "-");
        t.setCreatedIn(dto.createdIn != null ? dto.createdIn.toString() : "");
        t.setCompletedIn(dto.completedIn != null ? dto.completedIn.toString() : "");
        t.setExpiresIn(dto.expiresIn != null ? dto.expiresIn.toString() : "");
        t.setCategoryName(dto.listId != null ? "Lista " + dto.listId : "");
        return t;
    }
}


