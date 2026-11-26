package br.com.senai.estoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EstoqueApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EstoqueApplication.class);
        application.setAdditionalProfiles("estoque");
        application.run(args);
    }
}
