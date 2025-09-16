package com.loreweave.loreweave;

/*
 * ---------------------------------------------------------------
 * File: LoreweaveApplication.java
 * Author: Chris
 * Date: 2025-09-16
 * Purpose: Entry point for the Loreweave Spring Boot application.
 *
 * Update History:
 *   2025-09-16 (Jamie Coker)
 *   - Fixed the main method signature from
 *       static void main(String[] ignoredArgs)
 *     to
 *       public static void main(String[] args)
 *     so the JVM recognizes the entry point and the
 *     Spring Boot application can start correctly.
 * ---------------------------------------------------------------
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories

public class LoreweaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoreweaveApplication.class, args);
    }

}
