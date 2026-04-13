package com.jobportal.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Enable CORS using our corsConfigurationSource() bean below.
                // This MUST come before authorizeHttpRequests so that Spring Security
                // processes CORS headers and permits OPTIONS preflight requests
                // before any authentication checks run.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Define endpoint access rules
                .authorizeHttpRequests(auth -> auth

                        // ✅ Explicitly permit all OPTIONS preflight requests.
                        // Browsers send OPTIONS before every cross-origin request.
                        // Without this, Spring Security intercepts the preflight,
                        // finds no JWT token, and returns 403 — killing the actual request.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints — no token needed
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/applications/job/**").hasAuthority("ROLE_ADMIN")

                        // User endpoints
                        .requestMatchers("/api/applications/my").hasAuthority("ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/api/applications/**").hasAuthority("ROLE_USER")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Stateless session — JWT handles auth, no server session needed
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register our authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring's default username/password filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Define CORS rules here — inside Spring Security's filter chain.
    //
    // WHY NOT CorsConfig.java (CorsFilter bean)?
    // Your existing CorsConfig registers a plain servlet-level CorsFilter.
    // Spring Security runs its own filter chain BEFORE servlet filters,
    // so the CorsFilter bean never gets a chance to respond to OPTIONS preflights —
    // Spring Security blocks them first with 403.
    //
    // Defining CorsConfigurationSource here and wiring it via .cors() above
    // makes Spring Security itself handle CORS, so OPTIONS preflights are
    // allowed BEFORE any authentication logic runs.
    //
    // ✅ You can now safely DELETE CorsConfig.java — it is no longer needed.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // Allow both origins your frontend may be served from
        config.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}