package university.jala.finalProject.springJPA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import university.jala.finalProject.springJPA.entity.List;
import university.jala.finalProject.springJPA.service.ListService;

import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/api/lists")
public class ListController {

    @Autowired
    private ListService listService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Collection<List>> getListsByCategory(@PathVariable Integer categoryId) {
        try {
            Collection<List> lists = listService.getListsByCategory(categoryId);
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{categoryId}/user/{userId}")
    public ResponseEntity<Collection<List>> getListsByCategoryAndUser(
            @PathVariable Integer categoryId,
            @PathVariable Integer userId) {
        try {
            Collection<List> lists = listService.getListsByCategoryAndUser(categoryId, userId);
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{listId}/user/{userId}")
    public ResponseEntity<List> getListByIdAndUser(
            @PathVariable Integer listId,
            @PathVariable Integer userId) {
        try {
            Optional<List> list = listService.getListByIdAndUser(listId, userId);
            return list.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<List> createList(@RequestBody List list) {
        try {
            List savedList = listService.saveList(list);
            return ResponseEntity.ok(savedList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<List> updateList(@PathVariable Integer id, @RequestBody List list) {
        try {
            list.setId(id);
            List updatedList = listService.saveList(list);
            return ResponseEntity.ok(updatedList);
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
    public ResponseEntity<Collection<List>> getAllLists() {
        try {
            Collection<List> lists = listService.getAllLists();
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List> getListById(@PathVariable Integer id) {
        try {
            Optional<List> list = listService.getListById(id);
            return list.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}