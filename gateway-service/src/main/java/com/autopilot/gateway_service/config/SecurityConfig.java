package com.autopilot.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Gateway uses WebFlux (reactive) under the hood, not standard Spring Web
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        // For now, require the user to be logged in for ANY request
                        .anyExchange().authenticated()
                )
                // Tell Spring to expect an OAuth2 JWT token
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // Disable CSRF for local testing with Postman/cURL
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
