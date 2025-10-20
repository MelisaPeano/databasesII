package university.jala.finalProject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import university.jala.finalProject.UI.App;
import university.jala.finalProject.springJPA.entity.AppUser;
import university.jala.finalProject.springJPA.repository.AppUserRepository;
import university.jala.finalProject.springJPA.service.UserService;

import java.util.Optional;

@SpringBootApplication
public class Main {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        javafx.application.Application.launch(App.class, args);
    }

    @Bean
    public CommandLineRunner seed(UserService userService, AppUserRepository appUserRepository) {
        return args -> {
            String email = "admin@example.com";
            String newPassword = "pass_segura123";

            Optional<AppUser> optionalUser = appUserRepository.findByUserEmail(email);

            if (optionalUser.isPresent()) {
                userService.changePassword(email, newPassword);
                System.out.println("Contraseña actualizada para: " + email);
            } else {
                System.out.println("Usuario no encontrado: " + email);
            }
        };
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}