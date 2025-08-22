# 📚 EntreLibros Backend

Backend de la plataforma EntreLibros construido con Spring Boot 3 y Java 21.

## Requisitos

- Java 21
- Maven 3.9+
- Docker (para ejecutar PostgreSQL y los tests)

## Ejecutar en local

1. Clona el repositorio:
   ```bash
   git clone <repo-url> && cd EntreLibros_Backend
   ```
2. Configura las variables de entorno necesarias (ejemplo):
   ```properties
   SERVER_PORT=4000
   DB_URL=jdbc:postgresql://localhost:5432/entrelibros
   DB_USER=postgres
   DB_PASS=postgres
   JWT_ISSUER=https://entrelibros.app
   JWT_ACCESS_TTL=PT15M
   JWT_REFRESH_TTL=P14D
   ```
3. Levanta PostgreSQL (opcional mediante Docker):
   ```bash
   docker run --name entrelibros-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=entrelibros -p 5432:5432 -d postgres:16-alpine
   ```
4. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```
   o bien empaqueta y ejecuta el jar:
   ```bash
   mvn clean package
   java -jar target/entrelibros-backend-0.0.1-SNAPSHOT.jar
   ```

## Comandos útiles

- Ejecutar pruebas: `mvn test`
- Compilar sin pruebas: `mvn -DskipTests package`
- Ver dependencias: `mvn dependency:tree`

## Problemas comunes

### Docker no disponible en tests

Los tests usan Testcontainers para levantar PostgreSQL en un contenedor. Si al ejecutar `mvn test` aparece `IllegalState: Could not find a valid Docker environment`:

1. Asegúrate de que Docker esté instalado y en ejecución:
   ```bash
   docker ps
   ```
2. En Linux, agrega tu usuario al grupo `docker` y reinicia sesión:
   ```bash
   sudo usermod -aG docker $USER
   ```
3. Si Docker no está disponible, salta las pruebas con `mvn -DskipTests test` o anota los tests con `@Testcontainers(disabledWithoutDocker = true)`.

## Documentación

La documentación completa y los endpoints están en la carpeta [docs](docs/).
