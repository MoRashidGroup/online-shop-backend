package com.morashid.OnlineShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CorsConfig - inaruhusu frontend (React) ku-connect na backend (Spring Boot).
 * 
 * Bila hii, browser ina-block requests kutoka http://localhost:5173.
 * 
 * Kwa production, badilisha allowedOrigins kuwa domain yako halisi.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Ruhusu origins (frontend)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",   // Vite default
                "http://localhost:5174",   // Vite fallback
                "http://localhost:3000"    // React default (kama unatumia CRA)
        ));

        // Ruhusu methods zote
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Ruhusu headers zote
        configuration.setAllowedHeaders(List.of("*"));

        // Ruhusu credentials (kwa JWT tokens)
        configuration.setAllowCredentials(true);

        // Ruhusu browsers ku-cache preflight kwa masaa 1
        configuration.setMaxAge(3600L);

        // Apply kwa endpoints zote
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}