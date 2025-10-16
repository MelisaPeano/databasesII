package university.jala.finalProject.UI.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import university.jala.finalProject.UI.service.CurrentUserService;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.service.CategoryServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class CategoriesController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    @Autowired
    private CurrentUserService currentUserService; // Inyectar el servicio de sesión

    private Integer currentUserId;

    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, String> colorColumn;
    @FXML private TableColumn<Category, String> dateColumn;
    @FXML private TableColumn<Category, Integer> listsCountColumn;
    @FXML private Button newCategoryButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Category> categoriesObservable;

    @FXML
    public void initialize() {
        currentUserId = currentUserService.getCurrentUserId();

        if (currentUserId == null) {
            showError("Usuario no autenticado. Por favor inicie sesión primero.");
            return;
        }

        System.out.println("Cargando categorías para el usuario ID: " + currentUserId);

        setupTable();
        loadCategories();
        setupEvents();
    }

    private void setupTable() {
        categoriesObservable = FXCollections.observableArrayList();

        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        colorColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getColor() != null ?
                                cellData.getValue().getColor() : "Sin color"));

        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCreatedIn() != null ?
                                cellData.getValue().getCreatedIn().toString() : "Sin fecha"));

        listsCountColumn.setCellValueFactory(cellData -> {
            int count = categoryService.countListsByCategoryId(cellData.getValue().getId());
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });

        categoriesTable.setItems(categoriesObservable);

        categoriesTable.setRowFactory(tv -> {
            TableRow<Category> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editCategory();
                }
            });
            return row;
        });
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getCategoriesByUser(currentUserId);
            categoriesObservable.setAll(categories);
            System.out.println("Categorías cargadas: " + categories.size());
        } catch (Exception e) {
            showError("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void setupEvents() {
        categoriesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean hasSelection = newSelection != null;
                    editButton.setDisable(!hasSelection);
                    deleteButton.setDisable(!hasSelection);
                });
    }

    @FXML
    private void createNewCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CategoryModal.fxml"));

            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            CategoryModalController controller = loader.getController();
            controller.setCreationMode(true);
            controller.setCurrentUserId(currentUserId);
            controller.setCategoriesController(this);

            Stage stage = new Stage();
            stage.setTitle("Nueva Categoría");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error al abrir modal: " + e.getMessage());
        }
    }

    @FXML
    private void editCategory() {
        Category selectedCategory = categoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CategoryModal.fxml"));

                loader.setControllerFactory(applicationContext::getBean);

                Parent root = loader.load();

                CategoryModalController controller = loader.getController();
                controller.setCreationMode(false);
                controller.setCategory(selectedCategory);
                controller.setCurrentUserId(currentUserId);
                controller.setCategoriesController(this);

                Stage stage = new Stage();
                stage.setTitle("Editar Categoría");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Error al abrir modal: " + e.getMessage());
            }
        }
    }

    @FXML
    private void deleteCategory() {
        Category selectedCategory = categoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {

            int listCount = categoryService.countListsByCategoryId(selectedCategory.getId());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de eliminar esta categoría?");
            alert.setContentText("Categoría a eliminar: " + selectedCategory.getName() +
                    "\nNúmero de listas asociadas: " + listCount);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = categoryService.deleteCategory(selectedCategory.getId());
                    if (deleted) {
                        loadCategories();
                        showSuccess("Categoría eliminada exitosamente");
                    } else {
                        showError("No se pudo eliminar la categoría");
                    }
                } catch (Exception e) {
                    showError("Error al eliminar la categoría: " + e.getMessage());
                }
            }
        }
    }

    public void refreshCategoriesList() {
        loadCategories();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}