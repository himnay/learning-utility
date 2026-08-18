package com.learning.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Baseline auth gate for {@code /totp/**}. This is a demo-scoped fix: it stops the endpoints from
 * being fully anonymous, but it does NOT scope the authenticated principal to the {@code
 * accountName} in the request — any caller holding the one configured credential can still act on
 * any account. A real deployment needs per-account identity (e.g. the authenticated principal's
 * name must equal the {@code accountName} being generated/verified/rendered), not a single shared
 * Basic-auth credential. See {@code spring.security.user.*} in application.yml.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/totp/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .httpBasic(withDefaults());
    return http.build();
  }
}
