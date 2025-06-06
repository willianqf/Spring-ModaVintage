# Estágio 1: Build da Aplicação com Maven
# Usamos uma imagem que já tem o Maven e o JDK 17 instalados.
FROM maven:3.8-eclipse-temurin-17 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo pom.xml para baixar as dependências primeiro (cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia todo o código fonte do projeto
COPY src ./src

# Executa o comando para construir o projeto e gerar o .jar, pulando os testes
RUN mvn clean package -DskipTests


# Estágio 2: Execução da Aplicação
# Usamos uma imagem leve, que só tem o necessário para rodar Java (JRE)
FROM eclipse-temurin:17-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio de build
COPY --from=build /app/target/modavintage-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que a aplicação Spring usa (padrão 8080)
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java", "-jar", "app.jar"]