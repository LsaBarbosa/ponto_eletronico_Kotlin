# Use a imagem oficial do Maven para compilar a aplicação
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o arquivo pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte da aplicação
COPY src ./src

# Compila a aplicação
RUN mvn clean package -DskipTests

# Use uma imagem base do JDK para rodar a aplicação
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o arquivo JAR da aplicação compilada da fase de build
COPY --from=build /app/target/ponto_eletronico-0.0.1-SNAPSHOT.jar /app/app.jar

# Define o comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Exponha a porta padrão da aplicação
EXPOSE 8080
