package com.duocconecta.ms_publicaciones.config;

import com.duocconecta.ms_publicaciones.security.JwtUserContextFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;


import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUserContextFilter jwtUserContextFilter;

    @Value("${duocconecta.seguridad.habilitada:true}")
    private boolean seguridadHabilitada;

    @Value("${duocconecta.azure.client-id:}")
    private String azureClientId;

    @Value("${duocconecta.cors.origenes-permitidos:http://localhost:5173}")
    private List<String> origenesPermitidos;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (seguridadHabilitada) {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/actuator/health", "/api/publicaciones/ping").permitAll()
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                    .addFilterAfter(jwtUserContextFilter, BearerTokenAuthenticationFilter.class);
        } else {
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .addFilterBefore(usuarioDePruebaFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    /**
     * Además de validar firma/emisor (lo hace NimbusJwtDecoder por defecto),
     * exige que el token haya sido emitido específicamente para esta API
     * (aud debe incluir el client-id de la app registrada del backend).
     */
    private JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

        OAuth2TokenValidator<Jwt> validadorEmisor = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> validadorAudiencia = jwt -> {
            boolean valida = jwt.getAudience() != null && jwt.getAudience().contains(azureClientId);
            return valida
                    ? org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success()
                    : org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                            new org.springframework.security.oauth2.core.OAuth2Error(
                                    "invalid_token", "El token no fue emitido para esta API", null));
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validadorEmisor, validadorAudiencia));
        return decoder;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenesPermitidos);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private OncePerRequestFilter usuarioDePruebaFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                             HttpServletResponse response,
                                             FilterChain chain) throws ServletException, IOException {
                request.setAttribute("currentUserId", "usuario-prueba-local");
                request.setAttribute("currentUserEmail", "prueba@duocuc.cl");
                chain.doFilter(request, response);
            }
        };
    }
}