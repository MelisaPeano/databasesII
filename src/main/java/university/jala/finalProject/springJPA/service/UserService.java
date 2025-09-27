package university.jala.finalProject.springJPA.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.repository.AppUserRepository;
import university.jala.finalProject.springJPA.entity.AppUser;

import java.time.Instant;

@Service
public class UserService {
    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public AppUser register(String email, String rawPassword) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email no puede estar vacío");
        }
        if (repo.existsByUserEmail(email)) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        AppUser user = new AppUser();
        user.setUserEmail(email.trim());
        user.setUserPassword(encoder.encode(rawPassword));
        user.setUserName(email.split("@")[0]);
        user.setCreatedIn(Instant.now());

        return repo.save(user);
    }
}