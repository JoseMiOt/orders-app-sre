# Imagen base de Java 21
FROM eclipse-temurin:21-jdk

# Crear un directorio para la app dentro del contenedor
WORKDIR /app

# Copiar el JAR generado desde mi máquina al contenedor
COPY target/orders-app-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto interno
EXPOSE 8080

# Comando para ejecutar la app dentro del contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
