package university.jala.finalProject.springJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import university.jala.finalProject.springJPA.entity.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUserEmail(String userEmail);
    Optional<AppUser> findByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserName(String userName);
}