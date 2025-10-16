package edu.eci.arsw.blueprintsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ComponentScan(basePackages = {"edu.eci.arsw.blueprints"})
@SpringBootApplication
public class BlueprintsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueprintsApiApplication.class, args);
	}

}

