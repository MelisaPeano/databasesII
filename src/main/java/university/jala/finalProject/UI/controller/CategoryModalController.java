package university.jala.finalProject.UI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.service.CategoryServiceImpl;

import java.time.Instant;

@Controller
public class CategoryModalController {

    @FXML private TextField nameField;
    @FXML private TextField colorField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label titleLabel;

    @Autowired
    private CategoryServiceImpl categoryService;

    private boolean creationMode;
    private Category category;
    private Integer currentUserId;
    private CategoriesController categoriesController;

    @FXML
    public void initialize() {
        setupValidations();
    }

    private void setupValidations() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
    }

    private void validateForm() {
        boolean isValid = !nameField.getText().trim().isEmpty();
        saveButton.setDisable(!isValid);
    }

    public void setCreationMode(boolean creationMode) {
        this.creationMode = creationMode;
        titleLabel.setText(creationMode ? "Nueva Categoria" : "Editar Categoria");
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            nameField.setText(category.getName());
            colorField.setText(category.getColor() != null ? category.getColor() : "");
        }
    }

    public void setCurrentUserId(Integer userId) {
        this.currentUserId = userId;
    }

    public void setCategoriesController(CategoriesController controller) {
        this.categoriesController = controller;
    }

    @FXML
    private void saveCategory() {
        try {
            if (creationMode) {
                category = new Category();
                category.setUserId(currentUserId);
                category.setCreatedIn(Instant.now());
            }

            category.setName(nameField.getText().trim());
            category.setColor(colorField.getText().trim().isEmpty() ? null : colorField.getText().trim());

            if (creationMode) {
                categoryService.createCategory(category);
            } else {
                categoryService.updateCategoryName(category.getId(), category.getName());
            }

            if (categoriesController != null) {
                categoriesController.refreshCategoriesList();
            }

            closeModal();

        } catch (Exception e) {
            showError("Error al guardar la categoria: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}