package university.jala.finalProject.springJPA.service;

import university.jala.finalProject.springJPA.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category updateCategoryName(Integer categoryId, String newName);
    boolean deleteCategory(Integer categoryId);
    List<Category> getCategoriesByUser(Integer userId);
    boolean categoryBelongsToUser(Integer categoryId, Integer userId);
}