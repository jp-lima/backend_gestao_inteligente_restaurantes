# Build stage
FROM eclipse-temurin:26-jdk AS build

WORKDIR /app

# Copia os arquivos do Maven Wrapper e do POM primeiro para aproveitar o cache de camadas
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Baixa as dependências (camada cacheável)
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte e empacota a aplicação
COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:26-jre

WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Render injeta a variável PORT; usamos 8080 como fallback
ENV PORT=8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
