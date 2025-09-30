package university.jala.finalProject.springJPA.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import university.jala.finalProject.springJPA.entity.TaskStatusHistory;

public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Integer> { }
