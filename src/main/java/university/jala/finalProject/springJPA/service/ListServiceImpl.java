package university.jala.finalProject.springJPA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.entity.List;
import university.jala.finalProject.springJPA.repository.CategoryRepository;
import university.jala.finalProject.springJPA.repository.ListRepository;
import university.jala.finalProject.springJPA.service.ListService;

import java.time.Instant;
import java.util.Collection;

@Service
@Transactional
public class ListServiceImpl implements ListService {

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List createList(String listName, String listDescription, Integer categoryId, Integer userId) {
        // Verificar que la categoría exista y pertenezca al usuario
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category does not exist or does not belong to the user"));

        List list = new List();
        list.setName(listName);
        list.setDescription(listDescription);
        list.setCategory(category);
        list.setCreatedIn(Instant.now());

        List savedList = listRepository.save(list);
        System.out.println("List successfully created in the category!");
        return savedList;
    }

    @Override
    public Collection<List> getAllListsInCategory(Integer categoryId, Integer userId) {
        // Verificar permisos primero
        if (!categoryRepository.findByIdAndUserId(categoryId, userId).isPresent()) {
            System.out.println("Error the category does not exist or does not belong to the user!");
            return java.util.Collections.emptyList();
        }

        Collection<List> lists = listRepository.findByCategoryIdAndUserId(categoryId, userId);
        System.out.println(lists.size() + " lists were found in the category!");
        return lists;
    }

    @Override
    public boolean moveListToCategory(Integer listId, Integer newCategoryId, Integer userId) {
        // Verificar que la lista exista y pertenezca al usuario
        List list = listRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new RuntimeException("List does not exist or does not belong to the user"));

        // Verificar nueva categoría
        Category newCategory = categoryRepository.findByIdAndUserId(newCategoryId, userId)
                .orElseThrow(() -> new RuntimeException("New category does not exist or does not belong to the user"));

        // Verificar si ya está en la misma categoría
        if (list.getCategory().getId().equals(newCategoryId)) {
            System.out.println("The list is already in this category!");
            return true;
        }

        list.setCategory(newCategory);
        listRepository.save(list);
        System.out.println("List successfully moved to the new category!");
        return true;
    }

    @Override
    public List updateList(Integer listId, String listName, String listDescription, Integer userId) {
        List list = listRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new RuntimeException("List does not exist or does not belong to the user"));

        if (listName == null || listName.trim().isEmpty()) {
            throw new RuntimeException("List name cannot be empty");
        }

        list.setName(listName.trim());
        list.setDescription(listDescription != null ? listDescription.trim() : null);

        List updatedList = listRepository.save(list);
        System.out.println("List updated successfully!");
        return updatedList;
    }

    @Override
    public boolean deleteList(Integer listId, Integer userId) {
        List list = listRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new RuntimeException("List does not exist or does not belong to the user"));

        listRepository.delete(list);
        System.out.println("List successfully deleted!");
        return true;
    }

    @Override
    public boolean listBelongsToUser(Integer listId, Integer userId) {
        return listRepository.findByIdAndUserId(listId, userId).isPresent();
    }
}