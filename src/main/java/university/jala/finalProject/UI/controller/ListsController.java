package university.jala.finalProject.UI.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.entity.List;
import university.jala.finalProject.springJPA.repository.ListRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Controller
public class ListsController {

    @FXML
    private Label titleLabel;

    @FXML
    private TableView<List> listsTable;

    @FXML
    private TableColumn<List, String> listNameColumn;

    @FXML
    private TableColumn<List, String> listDescriptionColumn;

    @FXML
    private TableColumn<List, String> listDateColumn;

    @Autowired
    private ListRepository listRepository;

    private Category category;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setCategory(Category category) {
        this.category = category;
        titleLabel.setText("Listas de: " + category.getName());
        setupTableColumns();
        loadLists();
    }

    private void setupTableColumns() {
        listNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getListName()));

        listDescriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getListDescription() != null ?
                                cellData.getValue().getListDescription() : "Sin descripción"));

        listDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedIn() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getCreatedIn().atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime()
                                .format(dateFormatter)
                );
            } else {
                return new SimpleStringProperty("Sin fecha");
            }
        });
    }

    private void loadLists() {
        if (category != null) {
            Collection<List> lists = listRepository.findByCategory_Id(category.getId());
            listsTable.getItems().setAll(lists);
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void createNewList() {
        // ⚠️ Aquí más adelante podés abrir un modal para crear una nueva lista
    }
}
