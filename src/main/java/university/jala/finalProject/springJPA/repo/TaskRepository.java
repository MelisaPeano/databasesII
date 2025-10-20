package university.jala.finalProject.springJPA.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import university.jala.finalProject.springJPA.entity.Task;
import university.jala.finalProject.springJPA.entity.TaskState;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByList_Id(Integer listId);
    List<Task> findByList_IdAndStatus(Integer listId, TaskState status);
    boolean existsByList_IdAndTitleIgnoreCase(Integer listId, String title);
}
