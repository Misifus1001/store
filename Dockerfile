# ============================
# 1) Build Stage (Maven + JDK 17)
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ============================
# 2) Run Stage (JRE 17)
# ============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear carpeta para uploads
RUN mkdir -p /app/uploads/images

# Copiar jar generado
COPY --from=build /app/target/*.jar app.jar

# Hacer la carpeta accesible
VOLUME ["/app/uploads"]

EXPOSE 8080

# Permitir variables desde Render
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
