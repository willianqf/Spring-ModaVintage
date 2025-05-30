package br.com.api.modavintage.Config; // Seu pacote de configuração

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Pega o nome da pasta do application.properties para flexibilidade
    @Value("${app.upload.dir:${user.dir}/uploads}") // Padrão é uma pasta 'uploads' na raiz do projeto
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path resolvedUploadPath = Paths.get(uploadDir).toAbsolutePath();
        String resourceLocation = "file:///" + resolvedUploadPath.toString().replace("\\", "/") + "/";

        System.out.println("Configurando resource handler para /uploads/** em: " + resourceLocation);

        registry.addResourceHandler("/uploads/**") // O caminho HTTP que o frontend usará
                .addResourceLocations(resourceLocation); // O caminho no sistema de arquivos
    }
}