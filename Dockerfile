
FROM maven:3.9-eclipse-temurin-17 AS build

# Define o diretório de trabalho onde os comandos serão executados dentro do "container".
WORKDIR /app

# Copia todo o seu projeto para dentro do container.
COPY . .

# Executa o comando do Maven para compilar  projeto e gerar o arquivo .jar executável.
# A opção -DskipTests pula a execução dos testes para acelerar o processo de deploy.
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre-focal

# Define o diretório de trabalho.
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio anterior para a imagem final.
COPY --from=build /app/target/*.jar app.jar

# Informa que a aplicação irá usar a porta 8080.
EXPOSE 8080

# Define o comando que será executado para iniciar sua aplicação.
ENTRYPOINT ["java", "-jar", "app.jar"]