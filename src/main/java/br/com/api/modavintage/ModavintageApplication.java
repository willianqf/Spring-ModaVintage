package br.com.api.modavintage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*
 * Spring Initializr: Create a Maven Project
 * v3.5.2
 * JAVA / Pacote JAR / JAVA VERSION 17
 * BR.COM.Modavintagem
 * 
 * 
 * DEPENDÊNCIAS:
 * spring-boot-starter-web (org.springframework.boot)
 * spring-boot-devtools (org.springframework.boot) -> Permite atualizar spring em produção
 * lombok (org.projectlombok) -> Tira a necessidade de trabalhar com Gets e Setters
 * spring-boot-starter-data-jpa (org.springframework.boot) -> Permite criar tabelas do banco de dados nas classes
 * h2database (com.h2database) -> Usar driver h2 da base de dados SQL em ambiente de testes 
 * spring-boot-starter-validation (org.springframework.boot)
 * spring-boot-starter-security 
 * spring-boot-starter-mail (org.springframework.boot):
 * postgresql (org.postgresql) : - Base de dados em produção
 * jwt-api, jjwt-impl, jjwt-jackson (io.jsonwebtoken) - Conjunto de bibliotecas para criar e validar JSON Web Tokens (JWT)
 * spring-boot-starter-test (org.springframework.boot) - Servidor de testes para API
 */



@SpringBootApplication
public class ModavintageApplication {


	public static void main(String[] args) {
		SpringApplication.run(ModavintageApplication.class, args);
	}

}
