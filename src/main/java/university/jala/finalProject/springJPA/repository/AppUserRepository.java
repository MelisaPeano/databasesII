package university.jala.finalProject.springJPA.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import university.jala.finalProject.springJPA.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUserEmail(String userEmail);
    Optional<AppUser> findByUserEmail(String userEmail);

}
