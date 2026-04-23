package com.taekwondo.miwool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MiwoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiwoolApplication.class, args);
	}

}
