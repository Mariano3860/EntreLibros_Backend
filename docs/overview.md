# Documentación adicional

## 🧱 Stack

* **Runtime**: Java 21 (virtual threads habilitable con `spring.threads.virtual.enabled=true`)
* **Framework**: Spring Boot 3.x (Web, Validation)
* **Seguridad**: Spring Security 6 (OAuth2 Resource Server), JWT access + **refresh en cookie httpOnly/SameSite=strict**
* **Persistencia**: PostgreSQL 16, Spring Data JPA (Hibernate), **Flyway** para migraciones
* **Contratos**: springdoc-openapi (Swagger UI `/docs`), OpenAPI generado en build
* **Mapper**: MapStruct
* **Caching / Rate-limit**: Caffeine (local), Bucket4j (filtro) para protección básica
* **Observabilidad**: Micrometer + Prometheus, OpenTelemetry OTLP, logs JSON (Logback) con `traceId`/`spanId`
* **Testing**: JUnit 5, PostgreSQL (Testcontainers), MockMvc, AssertJ, Mockito
* **Build**: Gradle (Kotlin DSL), Dockerfile multi-stage + Jib opcional

---

## 🎯 Principios

* **Contrato primero**: cualquier cambio en endpoints debe actualizar OpenAPI y pruebas de contrato.
* **Seguridad por defecto**: mínimos privilegios, headers seguros, saneamiento y validación.
* **Compatibilidad FE**: claves de i18n en errores (p.ej. `auth.errors.invalid_credentials`).
* **Trazabilidad**: `X-Request-Id` → MDC → logs JSON + métricas por endpoint.

---

## 🔐 Autenticación y sesión

* **Login** devuelve `accessToken` (JWT corto, p.ej. 15 min) en el body **y** setea cookie `sessionToken` (refresh) **httpOnly, Secure, SameSite=Strict**, con rotación automática en `/auth/refresh` (interno).
* **Logout** invalida el refresh server-side (lista de deny / token version) y expira la cookie.
* **Protección**: CSRF no aplica a endpoints `stateless` con JWT; para formularios con cookie se añade **CSRF** si se habilita modo `session`.
* **Hash** de contraseñas: **Argon2id** (`PasswordEncoder` de Spring Security).

---

## 🧩 Modelos básicos (concepto)

* **User**: id, email (único), name, roles\[], passwordHash, language, createdAt
* **Book**: id, title, author(s), coverUrl, tags\[]
* **BookOwnership**: id, userId, bookId, condition, status, isForSale, price
* **FeedItem**: id, type(`book|swap|sale|seeking`), payload(JSONB), createdAt
* **ContactMessage**: id, name, email, message, createdAt

---

## 🔧 Configuración

Variables de entorno (validadas con `@ConfigurationProperties` + `jakarta.validation`):

```properties
SERVER_PORT=4000
API_PREFIX=/api
API_VERSION=v1
CORS_ORIGINS=http://localhost:3000

DB_URL=jdbc:postgresql://postgres:5432/entrelibros
DB_USER=postgres
DB_PASS=postgres

JWT_ISSUER=https://entrelibros.app
JWT_ACCESS_TTL=PT15M
JWT_REFRESH_TTL=P14D
JWT_SECRET=super-secret-0123456789abcdef0123456789abcd
JWT_ACCESS_PUBLIC_KEY=... # RS256 recomendado
JWT_ACCESS_PRIVATE_KEY=...

RATE_LIMIT_WINDOW=60s
RATE_LIMIT_MAX=120
```

`JWT_SECRET` es obligatorio y debe tener al menos 32 bytes.

---

## 🐳 Docker / Dev

* **PostgreSQL** + **Mailpit** en `docker-compose.dev.yml`.
* Ejecutar app: `./gradlew bootRun` (o `java -jar` del jar empaquetado).

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: entrelibros
    ports: ["5432:5432"]
    volumes: ["db_data:/var/lib/postgresql/data"]
  mailpit:
    image: axllent/mailpit
    ports: ["8025:8025", "1025:1025"]
volumes:
  db_data:
```

---

## 🔒 Buenas prácticas aplicadas

* **JWT RS256** con rotación de refresh, cookie httpOnly/Secure/SameSite=Strict, `Path` limitado.
* **Argon2id** para contraseñas; política de bloqueo por intentos fallidos.
* **CORS** explícito por entorno; **Helmet-equivalente** en Spring Security (CSP, HSTS, X-Frame-Options, etc.).
* **Validación** con Bean Validation + sanitización; límites de tamaño (`spring.servlet.multipart` y `maxPayloadSize`).
* **Rate-limit** Bucket4j por IP/route; **circuit breakers** opcionales con Resilience4j.
* **Observabilidad** out-of-the-box: `/actuator/health`, `/actuator/metrics`, trazas OTel.
* **Migrations** con Flyway y `baselineOnMigrate=true` en entornos existentes.
* **Zero-downtime**: `readiness/liveness` probes; **layered jar** para imágenes pequeñas.

---

## 🧪 Pruebas

* **Unitarias**: servicios, mappers, validaciones
* **Integración**: repos + seguridad con PostgreSQL (Testcontainers)
* **E2E (contrato)**: WebTestClient contra contexto real y verificación OpenAPI
* **Escenarios críticos**: login/logout, `/books/mine`, feed paginado, envío de contacto

---

## 📄 Documentación OpenAPI

* Swagger UI: `GET /docs`
* OpenAPI JSON: `GET /docs/openapi.json` (se commitea en `docs/openapi.json`)

---

## 🧷 Ejemplos de seguridad (headers)

* `Strict-Transport-Security: max-age=31536000; includeSubDomains` (prod)
* `Content-Security-Policy: default-src 'self'; img-src 'self' data: https://covers.openlibrary.org`
* `X-Content-Type-Options: nosniff`
* `X-Frame-Options: DENY`

---

## ✅ Checklists de entrega

* [ ] OpenAPI actualizado y publicado en `/docs`
* [ ] Flyway migrations aplicadas en CI + entorno
* [ ] `CORS_ORIGINS` correcto y pruebas de navegador OK
* [ ] Rate-limit y logs JSON verificados (con `X-Request-Id`)
* [ ] Tests verdes (unit/integration/e2e) con PostgreSQL (Testcontainers)

---

## 🤝 Contribución

1. Branch `feat/<feature>`
2. Añadí tests y actualizá OpenAPI si toca endpoints
3. `./gradlew check` y calidad estática
4. PR con descripción de contrato, riesgos y migración

---

## 🔎 Notas de diseño

* **Errores** usan Problem Details; el FE puede leer `code`/`messageKey` para i18n.
* **Paginación**: endpoints públicos con `page/size` (compatibilidad actual del FE). En V1 considerar **cursor** donde corresponda.
* **Contact**: asincronía por evento + email; dev: Mailpit, prod: SMTP/Resend.

---

## 🧠 Decisiones (y qué descartamos)

**Elegimos**: Spring Boot 3.x (Java 21), Security 6 con JWT + refresh cookie, Postgres + JPA/Flyway, OpenAPI springdoc, PostgreSQL para tests (Testcontainers), Argon2id, Bucket4j y observabilidad OTel/Micrometer. **Descartamos**: NestJS/Node para este repo (duplicaba tipos y no cumple tu pedido de Spring), sesiones de servidor puras (preferimos JWT stateless + refresh cookie), y jOOQ por ahora (JPA suficiente en el MVP; jOOQ se evalúa para consultas complejas). **Criterio**: máxima seguridad/patrón actual de la industria, compatibilidad con tu FE y time-to-value rápido sin deuda innecesaria.
