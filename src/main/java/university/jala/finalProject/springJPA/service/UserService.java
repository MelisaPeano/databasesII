package university.jala.finalProject.springJPA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.repository.AppUserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private volatile AppUser currentUser;

    public AppUser register(String email, String password) {
        return register(email, email, password);
    }

    public AppUser register(String username, String email, String password) {

        if (appUserRepository.existsByUserEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (appUserRepository.existsByUserName(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        AppUser user = new AppUser();
        user.setUserName(username);
        user.setUserEmail(email);
        user.setUserPassword(passwordEncoder.encode(password));

        return appUserRepository.save(user);
    }

    public AppUser createUser(AppUser user) {

        if (appUserRepository.existsByUserEmail(user.getUserEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (appUserRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (!user.getUserPassword().startsWith("$2a$")) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        return appUserRepository.save(user);
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByUserEmail(email);
    }

    public Optional<AppUser> authenticate(String email, String password) {
        return appUserRepository.findByUserEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getUserPassword()))
                .map(user -> {
                    currentUser = user;
                    return user;
                });
    }

    public Optional<AppUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void logout() {
        currentUser = null;
    }

    public boolean emailExists(String email) {
        return appUserRepository.existsByUserEmail(email);
    }

    public boolean usernameExists(String username) {
        return appUserRepository.existsByUserName(username);
    }

    public AppUser changePassword(String email, String newPassword) {

        AppUser user = appUserRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setUserPassword(passwordEncoder.encode(newPassword));

        return appUserRepository.save(user);
    }

}