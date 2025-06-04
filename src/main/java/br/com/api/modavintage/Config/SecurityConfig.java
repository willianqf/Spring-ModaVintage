package br.com.api.modavintage.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest; // recursos estáticos
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Para desabilitar CSRF 
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer; // Para frameOptions
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.authentication.AuthenticationManager; // Importar
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Importar

import br.com.api.modavintage.Security.JwtRequestFilter; // Importar o filtro JWT

//
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importar



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired // Injeta o filtro JWT
    private JwtRequestFilter jwtRequestFilter;

    /* 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
        */
    //

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    @Order(1) // Adicionar uma ordem para o filtro do H2 console
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Aplicar este filtro apenas para o caminho do H2 Console
            .securityMatcher(PathRequest.toH2Console())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toH2Console()).permitAll() // Permitir todas as requisições para o H2 console
            )
            // Permitir os frames do H2
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            // Desabilitar CSRF para o H2 console
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
                .requestMatchers(HttpMethod.POST, "/auth/registrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/solicitar-reset-senha").permitAll() 
                .requestMatchers(HttpMethod.POST, "/auth/resetar-senha").permitAll()     
                .requestMatchers(HttpMethod.GET, "/produtos").permitAll()
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                .anyRequest().authenticated()
            )
            // Adicionar o filtro JWT ANTES do filtro padrão de username/password
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}