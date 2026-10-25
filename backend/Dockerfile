# Compila o jar com o Maven Wrapper e roda numa imagem só com o Java (JRE).

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Dependências primeiro, para o Docker reaproveitar o cache quando só o código mudar
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B -DskipTests package && cp target/*.jar app.jar


FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd --system --no-create-home nti
COPY --from=build /app/app.jar app.jar
USER nti

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
