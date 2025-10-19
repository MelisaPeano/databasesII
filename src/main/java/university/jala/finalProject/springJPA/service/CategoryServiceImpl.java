package university.jala.finalProject.springJPA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import university.jala.finalProject.springJPA.entity.Category;
import university.jala.finalProject.springJPA.repository.CategoryRepository;
import university.jala.finalProject.springJPA.repository.ListRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ListRepository listRepository;

    @Override
    public Category createCategory(Category category) {
        // Verificar si ya existe una categoría con el mismo nombre para este usuario
        if (categoryRepository.existsByUserIdAndName(category.getUserId(), category.getName())) {
            throw new RuntimeException("There is already a Category with that name");
        }

        // Establecer fecha de creación si no existe
        if (category.getCreatedIn() == null) {
            category.setCreatedIn(Instant.now());
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategoryName(Integer categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Verificar si el nuevo nombre ya existe para este usuario
        if (categoryRepository.existsByUserIdAndName(category.getUserId(), newName)) {
            throw new RuntimeException("There is already a Category with that name");
        }

        category.setName(newName);
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Verificar si tiene listas asociadas
        int listCount = categoryRepository.countListsByCategoryId(categoryId);
        if (listCount > 0) {
            throw new RuntimeException("Cannot delete, category has " + listCount + " associated lists!");
        }
        categoryRepository.delete(category);
        return true;
    }

    @Override
    public List<Category> getCategoriesByUser(Integer userId) {
        return categoryRepository.findByUserIdOrderByName(userId);
    }

    @Override
    public boolean categoryBelongsToUser(Integer categoryId, Integer userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId).isPresent();
    }

    @Override
    public List<Category> findAll() {
        return List.of();
    }

    public int countListsByCategoryId(Integer categoryId) {
        return categoryRepository.countListsByCategoryId(categoryId);
    }
}