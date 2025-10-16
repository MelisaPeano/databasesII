package university.jala.finalProject.UI.controller;

import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import university.jala.finalProject.springJPA.entity.List;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.repository.ListRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collection;


@Controller
public class ListsController {
    @FXML
    private TableColumn<List, Void> listActionsColumn;

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
                new SimpleStringProperty(cellData.getValue().getListDescription() != null ?
                        cellData.getValue().getListDescription() : "Sin descripción"));
        listDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedIn() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getCreatedIn()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime()
                                .format(dateFormatter)
                );
            } else return new SimpleStringProperty("Sin fecha");
        });

        listsTable.setEditable(true);
        listNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        listDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        listNameColumn.setOnEditCommit(e -> {
            List listEntity = e.getRowValue(); // <-- tu entidad
            listEntity.setListName(e.getNewValue());
            listRepository.save(listEntity);
        });

        listDescriptionColumn.setOnEditCommit(e -> {
            List listEntity = e.getRowValue(); // <-- tu entidad
            listEntity.setListDescription(e.getNewValue());
            listRepository.save(listEntity);
        });

// Botones de acciones
        listActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Eliminar");

            {
                deleteBtn.setOnAction(e -> {
                    List listEntity = getTableView().getItems().get(getIndex());

                    if (listEntity.getTasks() != null && !listEntity.getTasks().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmar eliminación");
                        alert.setHeaderText("La lista tiene tareas asociadas");
                        alert.setContentText("¿Desea eliminar la lista y todas sus tareas?");

                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                listRepository.delete(listEntity);
                                getTableView().getItems().remove(listEntity);
                            }
                        });
                    } else {
                        // Sin tareas, eliminar directamente
                        listRepository.delete(listEntity);
                        getTableView().getItems().remove(listEntity);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }
    private void loadLists() {
        if (category != null) {
            Collection<List> lists = listRepository.findByCategory_IdWithTasks(category.getId());
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Lista");
        dialog.setHeaderText("Ingrese el nombre de la nueva lista:");
        dialog.setContentText("Nombre:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                List newList = new List();
                newList.setListName(name);
                newList.setCategory(category);
                newList.setCreatedIn(java.time.Instant.now());
                listRepository.save(newList);
                listsTable.getItems().add(newList);
            }
        });
    }


}
