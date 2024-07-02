package org.chrisbarbati.databasecontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatabaseControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseControllerApplication.class, args);
	}

}
