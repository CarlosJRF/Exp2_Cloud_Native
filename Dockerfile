# 1. Usamos una imagen base oficial de Java 21 ligera (Alpine)
FROM eclipse-temurin:21-jdk-alpine

# 2. Indicamos en qué puerto se comunicará el contenedor con el exterior
EXPOSE 8080

# 3. Copiamos el archivo .jar de tu carpeta target hacia dentro del contenedor y lo renombramos a "app.jar"
COPY target/registration-0.0.1-SNAPSHOT.jar app.jar

# 4. Le decimos a Docker qué comando ejecutar de forma predeterminada cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "/app.jar"]