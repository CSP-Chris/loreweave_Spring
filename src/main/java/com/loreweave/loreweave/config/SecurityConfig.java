package com.loreweave.loreweave.config;


/*
 This has to go here or there's an error loading the beans
 ==========================================
 File Name:    SecurityConfig.java
 Created By:   Chris
 Created On:   2025-09-15
 Purpose:      Initial Spring Security configuration
 Updated By:   Jamie Coker
 Updated On:   2025-09-16
 Update Notes: Integrated JwtAuthenticationFilter, enforced stateless
               sessions, configured /api/auth/** as public, and added
               BCryptPasswordEncoder bean.
  Updated By: Jamie Coker on 9/21/2025
  Update Notes: Configured authenticationManager, JWT filter, password encoder.
  Updated By:   Jamie Coker on 9/25/2025
 Update Notes: Removed JWT, switched to stateful session-based authentication
               with Thymeleaf form login, custom login/logout pages, and
               session cookie handling.
  Updated By:   Chris Ennis on 9/24/2025
  Update Notes: Added /auth/register to permitAll() to fix login redirect loop.
  Updated By:   Jamie Coker on 2025-09-26
 Update Notes: extended static resource paths (CSS, JS, images, favicon).
 */




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/register", "/loginBSF",
                                "/css/**", "/js/**","/images/**", "/webjars/**", "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/loginBSF")       // your custom login page
                        .loginProcessingUrl("/login") // Spring Security handles POST here
                        .defaultSuccessUrl("/welcome", true)
                        .failureUrl("/loginBSF?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/loginBSF?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

}