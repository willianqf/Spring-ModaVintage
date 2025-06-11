package br.com.api.modavintage.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import br.com.api.modavintage.Security.JwtRequestFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    @Order(1)
    @Profile("dev")
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(PathRequest.toH2Console())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toH2Console()).permitAll()
            )
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .csrf(AbstractHttpConfigurer::disable); 

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permite todos os endpoints de autenticação
                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                
                // Permite acesso público para visualizar produtos
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                
                // ===== CORREÇÃO PRINCIPAL =====
                // Adicionando regras explícitas para os endpoints GET de vendas que estavam retornando 403.
                // Esta especificidade garante que o Spring Security aplique a permissão corretamente.
                .requestMatchers(HttpMethod.GET, "/vendas/cliente/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/vendas/data").authenticated()

                // Mantemos a regra genérica para os outros métodos de /vendas (como POST e DELETE)
                .requestMatchers("/vendas/**").authenticated()

                // Exige autenticação para todas as outras requisições
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}