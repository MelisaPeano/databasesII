package university.jala.finalProject;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;

@SpringBootApplication
@RestController
public class Main {

	@GetMapping("/")
	public String home() {
		return "Spring is here!";
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
