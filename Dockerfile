# ---------- Etapa 1: build (Maven + JDK 21) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---------- Etapa 2: execução (somente JRE 21) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/bairro-em-dia.jar app.jar
# O Render define a variável PORT; a aplicação lê server.port=${PORT:8080}
EXPOSE 10000
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
