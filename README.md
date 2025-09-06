# 📚 EntreLibros Backend

Backend de la plataforma EntreLibros construido con Spring Boot 3 y Java 21.

## Requisitos

- Java 21
- Maven 3.9+
- Docker (requerido para pruebas y bases de datos mediante Testcontainers)

## Ejecutar en local

1. Clona el repositorio:
   ```bash
   git clone <repo-url> && cd EntreLibros_Backend
   ```
2. Configura las variables de entorno necesarias (renombrar .env.example a .env):
   ```properties
   SERVER_PORT=4000
   DB_URL=jdbc:postgresql://localhost:5432/entrelibros
   DB_USER=postgres
   DB_PASS=postgres
   JWT_SECRET=super-secret-0123456789abcdef0123456789abcd
   JWT_ISSUER=https://entrelibros.app
   JWT_ACCESS_TTL=PT15M
   JWT_REFRESH_TTL=P14D
   ```
   `JWT_SECRET` es obligatorio y debe tener al menos 32 bytes.
3. Levanta PostgreSQL (por ejemplo con Docker):
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

- Ejecutar pruebas (requiere Docker): `mvn test`
- Compilar sin pruebas: `mvn -DskipTests package`
- Ver dependencias: `mvn dependency:tree`

## Documentación

La documentación completa y los endpoints están en la carpeta [docs](docs/).
