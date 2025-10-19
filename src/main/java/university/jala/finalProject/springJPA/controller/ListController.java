package university.jala.finalProject.springJPA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import university.jala.finalProject.springJPA.entity.ListTable;
import university.jala.finalProject.springJPA.service.ListService;

import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/api/lists")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Collection<ListTable>> getListsByCategory(@PathVariable Integer categoryId) {
        try {
            Collection<ListTable> listEntities = listService.getListsByCategory(categoryId);
            return ResponseEntity.ok(listEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{categoryId}/user/{userId}")
    public ResponseEntity<Collection<ListTable>> getListsByCategoryAndUser(
            @PathVariable Integer categoryId,
            @PathVariable Integer userId) {
        try {
            Collection<ListTable> listEntities = listService.getListsByCategoryAndUser(categoryId, userId);
            return ResponseEntity.ok(listEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{listId}/user/{userId}")
    public ResponseEntity<ListTable> getListByIdAndUser(
            @PathVariable Integer listId,
            @PathVariable Integer userId) {
        try {
            Optional<ListTable> list = listService.getListByIdAndUser(listId, userId);
            return list.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ListTable> createList(@RequestBody ListTable listTable) {
        try {
            ListTable savedListTable = listService.saveList(listTable);
            return ResponseEntity.ok(savedListTable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListTable> updateList(@PathVariable Integer id, @RequestBody ListTable listTable) {
        try {
            listTable.setId(id);
            ListTable updatedListTable = listService.saveList(listTable);
            return ResponseEntity.ok(updatedListTable);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Integer id) {
        try {
            boolean deleted = listService.deleteList(id);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<ListTable>> getAllLists() {
        try {
            Collection<ListTable> listEntities = listService.getAllLists();
            return ResponseEntity.ok(listEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListTable> getListById(@PathVariable Integer id) {
        try {
            Optional<ListTable> list = listService.getListById(id);
            return list.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}