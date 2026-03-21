package com.fatto.listatarefas.api_lista_tarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.fatto.listatarefas.api_lista_tarefas")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
