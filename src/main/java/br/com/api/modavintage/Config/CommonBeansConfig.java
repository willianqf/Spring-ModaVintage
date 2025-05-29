package br.com.api.modavintage.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CommonBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("--- Criando PasswordEncoder Bean em CommonBeansConfig ---");
        return new BCryptPasswordEncoder();
    }
}