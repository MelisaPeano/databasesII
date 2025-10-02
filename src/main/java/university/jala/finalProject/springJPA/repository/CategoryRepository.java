package university.jala.finalProject.springJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import university.jala.finalProject.springJPA.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByUserIdAndName(Integer userId, String name);

    // Obtener categorías por usuario ordenadas por nombre
    List<Category> findByUserIdOrderByName(Integer userId);

    // Buscar categoría por ID y usuario (para verificar permisos)
    Optional<Category> findByIdAndUserId(Integer id, Integer userId);

    // Contar listas asociadas a una categoría
    @Query("SELECT COUNT(l) FROM List l WHERE l.category.id = :categoryId")
    int countListsByCategoryId(@Param("categoryId") Integer categoryId);

    // Método adicional usando el nombre alternativo que tienes en la entidad
    default boolean existsByUserIdAndCategoryName(Integer userId, String categoryName) {
        return existsByUserIdAndName(userId, categoryName);
    }
}