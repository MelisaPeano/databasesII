package university.jala.finalProject.springJPA.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import university.jala.finalProject.springJPA.entity.List;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ListRepository extends JpaRepository<List, Integer> {

    // Obtener todas las listas de una categoría
    Collection<List> findByCategoryId(Integer categoryId);

    // Verificar permisos: lista pertenece a usuario a través de la categoría
    @Query("SELECT l FROM List l WHERE l.id = :listId AND l.category.userId = :userId")
    Optional<List> findByIdAndUserId(@Param("listId") Integer listId, @Param("userId") Integer userId);

    // Obtener listas por categoría verificando que el usuario sea dueño
    @Query("SELECT l FROM List l WHERE l.category.id = :categoryId AND l.category.userId = :userId")
    Collection<List> findByCategoryIdAndUserId(@Param("categoryId") Integer categoryId, @Param("userId") Integer userId);

    // Verificar si una lista existe en una categoría específica
    boolean existsByIdAndCategoryId(Integer listId, Integer categoryId);
}