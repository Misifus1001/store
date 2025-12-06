# ============================
# 1) Build Stage
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear carpeta de uploads
RUN mkdir -p /app/uploads/images

# Copiar el jar
COPY --from=build /app/target/*.jar app.jar

# Montar volumen (opcional pero recomendado)
# VOLUME ["/app/uploads/images"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
