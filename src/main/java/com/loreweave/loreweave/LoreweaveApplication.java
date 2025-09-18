package com.loreweave.loreweave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan({"com.loreweave.entity", "com.loreweave.loreweave.model"})
public class LoreweaveApplication {
    // Spring will not run the new main method I'll look into a way around this - JDK 25 RIP :(
    public static void main(String[] args) {
        SpringApplication.run(LoreweaveApplication.class, args);
    }
}

