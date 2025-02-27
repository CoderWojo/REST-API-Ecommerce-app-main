package pl.wojo.app.ecommerce_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
    
    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        /*CSRF jest potrzebne, gdy sesja użytkownika jest przechowywana w przeglądarce (cookies).
🔹 CSRF NIE jest potrzebne, gdy autoryzacja działa na tokenach JWT w nagłówkach.
 */
        // csrf wymagany na endpointach POST DETELE PATCH UPDATE  
        http.csrf(csrf -> csrf.disable());  // wyłączamy csrf, bo domyślnie spring security wyszukuje csrf header w żądaniach
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/register", "/auth/login", "/products", "/auth/verify").permitAll()  // Publiczne endpointy 
            .anyRequest().authenticated()); // Każde inne żądanie musi być uwierzytelnione
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));// nie używamy sesji, bazujemy na jwt
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}