# Usamos la imagen oficial de Java 25
FROM eclipse-temurin:25-jdk-alpine

# Creamos una carpeta dentro del contenedor para nuestra app
WORKDIR /app

# Copiamos el archivo ejecutable JAR a nuestro contenedor
#COPY target/*.jar app.jar

# Exponemos el puerto de la web
EXPOSE 8080

# Comando se ejecuta al encender el contenedor
#ENTRYPOINT ["java", "-jar", "app.jar"]
# En lugar de usar un .jar, ejecutamos el proyecto en modo "desarrollo"
# usando el wrapper de Maven (mvnw) que viene con Spring Boot
CMD ["./mvnw", "spring-boot:run"]