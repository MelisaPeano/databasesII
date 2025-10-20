package university.jala.finalProject.UI.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceUI {
    @Autowired
    private CategoryService categoryService;

    /* -------------------- READ -------------------- */
    public List<Category> getCategoriesByUser(Integer userId) {
        try {
            return categoryService.getCategoriesByUser(userId);
        } catch (Exception e) {
            showError("Error al cargar categorías: " + e.getMessage());
            return List.of();
        }
    }

    /* -------------------- CREATE -------------------- */
    public Category createCategory(Category category) {
        try {
            return categoryService.createCategory(category);
        } catch (Exception e) {
            showError("Error al crear categoría: " + e.getMessage());
            return null;
        }
    }

    /* -------------------- DELETE -------------------- */
    public boolean deleteCategory(Integer categoryId) {
        try {
            return categoryService.deleteCategory(categoryId);
        } catch (Exception e) {
            showError("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    /* -------------------- UI Helpers -------------------- */
    private void showError(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }
}
