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
    Collection<List> findByCategory_Id(Integer categoryId);

    @Query("SELECT l FROM List l LEFT JOIN FETCH l.tasks WHERE l.category.id = :categoryId")
    Collection<List> findByCategory_IdWithTasks(@Param("categoryId") Integer categoryId);

    @Query("SELECT l FROM List l WHERE l.id = :listId AND l.category.userId = :userId")
    Optional<List> findByIdAndUserId(@Param("listId") Integer listId, @Param("userId") Integer userId);

    @Query("SELECT l FROM List l WHERE l.category.id = :categoryId AND l.category.userId = :userId")
    Collection<List> findByCategoryIdAndUserId(@Param("categoryId") Integer categoryId, @Param("userId") Integer userId);

    // USA @Query PARA MANTENER EL NOMBRE:
    @Query("SELECT COUNT(l) > 0 FROM List l WHERE l.id = :listId AND l.category.id = :categoryId")
    boolean existsByIdAndCategoryId(@Param("listId") Integer listId, @Param("categoryId") Integer categoryId);

    @Query("SELECT l.id FROM List l WHERE l.id = :listId")
    int findIdByListId(@Param("listId") Integer listId);
}