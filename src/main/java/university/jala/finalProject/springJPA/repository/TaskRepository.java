package university.jala.finalProject.springJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import university.jala.finalProject.springJPA.entity.Task;
import university.jala.finalProject.springJPA.entity.TaskState;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByListTable_Id(Integer listId);
    List<Task> findByListTable_IdAndStatus(Integer listId, TaskState status);
    List<Task> findAll();
    boolean existsByListTable_IdAndTitleIgnoreCase(Integer listId, String title);
}
