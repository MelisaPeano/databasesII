package university.jala.finalProject.springJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import university.jala.finalProject.springJPA.entity.Task;
import university.jala.finalProject.springJPA.entity.TaskState;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByListTable_Id(Integer listId);
    List<Task> findByListTable_IdAndStatus(Integer listId, TaskState status);
    List<Task> findAll();
    boolean existsByListTable_IdAndTitleIgnoreCase(Integer listId, String title);
    @Query("SELECT t FROM Task t WHERE t.listTable.category.userId = :userId")
    List<Task> findAllByUserId(@Param("userId") Integer userId);

    @Query("SELECT t FROM Task t WHERE t.listTable.category.userId = :userId AND t.status = :status")
    List<Task> findAllByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") TaskState status);

    List<Task> findByListTable_Category_UserId(Integer userId);

}
