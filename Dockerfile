FROM node:22-alpine AS frontend
WORKDIR /app/store-ui
COPY store-ui/package*.json ./
RUN npm ci
COPY store-ui/ ./
RUN npm run build

FROM eclipse-temurin:21-jdk-alpine AS backend
WORKDIR /app
COPY product-service/mvnw ./
COPY product-service/.mvn .mvn
COPY product-service/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline
COPY product-service/src src
COPY --from=frontend /app/store-ui/dist/store-ui/browser src/main/resources/static
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=public-demo"]
