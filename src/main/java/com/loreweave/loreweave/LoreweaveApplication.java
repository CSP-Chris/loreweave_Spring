package com.loreweave.loreweave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoreweaveApplication {
    // Spring will not run the new main method I'll look into a way around this
    static void main(String[] ignoredArgs) {
        SpringApplication.run(LoreweaveApplication.class);
    }
}

