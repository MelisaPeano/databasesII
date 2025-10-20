package university.jala.finalProject.springJPA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);

    void deleteByUser(AppUser user);

    Optional<RefreshToken> findByUser(AppUser user);
}