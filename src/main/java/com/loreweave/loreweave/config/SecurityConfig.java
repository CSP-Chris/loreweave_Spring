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
 Updated By: Wyatt Bechtle on 10/02/2025
 Update Notes: Changed defaultSuccessUrl to /profile
               Changed loginBSF->login
               Added in remember me functionality
               Added authenticationProvider bean for custom user details service
 */




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.loreweave.loreweave.security.CustomUserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authProvider) throws Exception {
        http
                .authenticationProvider(authProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/register", "/login",
                                "/css/**", "/js/**","/images/**", "/webjars/**", "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")       
                        .loginProcessingUrl("/login") 
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .rememberMeParameter("remember-me")
                        .key("change-me-in-config")
                        .tokenValiditySeconds(86400) // 1 day
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
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService uds,
                                                            PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

}