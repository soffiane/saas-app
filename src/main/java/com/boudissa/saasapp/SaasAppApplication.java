package com.boudissa.saasapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaasAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasAppApplication.class, args);
    }

}
