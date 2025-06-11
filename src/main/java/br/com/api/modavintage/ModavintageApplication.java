package br.com.api.modavintage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*
 * Spring Initializr: Create a Maven Project
 * v3.5.0
 * JAVA / Pacote JAR / JAVA VERSION 17
 * BR.COM.Modavintagem
 * 
 * 
 * DEPENDÊNCIAS:
 * spring-boot-starter-web (org.springframework.boot)
 * spring-boot-devtools (org.springframework.boot) -> Permite atualizar spring em produção
 * lombok (org.projectlombok) -> Tira a necessidade de trabalhar com Gets e Setters
 * spring-boot-starter-data-jpa (org.springframework.boot) -> Permite criar tabelas do banco de dados nas classes
 * h2database (com.h2database) -> Usar driver h2 da base de dados SQL 
 * spring-boot-starter-validation (org.springframework.boot)
 * spring-boot-starter-security 
 * 
 * 
 */



@SpringBootApplication
public class ModavintageApplication {


	public static void main(String[] args) {
		SpringApplication.run(ModavintageApplication.class, args);
	}

}
