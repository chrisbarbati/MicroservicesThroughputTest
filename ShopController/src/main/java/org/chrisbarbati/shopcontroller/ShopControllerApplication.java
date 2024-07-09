package org.chrisbarbati.shopcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopControllerApplication.class, args);
	}

}
