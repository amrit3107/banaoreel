package com.banaoreel.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SCAFFOLD ONLY: all requests are currently permitted so the API is
 * runnable/testable end to end before auth is wired in.
 * TODO before any real deployment:
 *  1. Add a JwtAuthFilter that validates the token from AuthController and
 *     populates the SecurityContext (replacing the X-User-Id header trick).
 *  2. Restrict /internal/** to the internal-api-key check only (already done
 *     in InternalWorkerController) and/or network-level isolation.
 *  3. Require authentication on /wallet/** and /videos/**.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
