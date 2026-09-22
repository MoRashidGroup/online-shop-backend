package com.morashid.OnlineShop.config;

import com.morashid.OnlineShop.auth.security.CustomUserDetailsService;
import com.morashid.OnlineShop.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;  // ← MPYA

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS - MUHIMU! Lazima iwe kabla ya csrf
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                // Uploads folder - public (kwa picha ku-display)
                .requestMatchers("/uploads/**").permitAll()

                // File upload endpoints - authenticated (SELLER na ADMIN)
                .requestMatchers("/api/files/**").authenticated()

                .requestMatchers("/api/cart/**").hasRole("BUYER")
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("BUYER")
                .requestMatchers("/api/orders/my-orders").hasRole("BUYER")
                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()

                .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("BUYER")
  
                // PAYMENTs
                .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("BUYER")
                .requestMatchers(HttpMethod.POST, "/api/payments/*/confirm").hasRole("BUYER")
                .requestMatchers(HttpMethod.GET, "/api/payments/**").authenticated()
                // Admin payments
                .requestMatchers("/api/payments/admin/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("BUYER")
                .requestMatchers(HttpMethod.GET, "/api/payments/**").authenticated()

                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/seller/**").hasRole("SELLER")

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}