## 📡 API — Endpoints del MVP

**Base URL:** `/api/v1`

> Respuestas exitosas envuelven `{ "data": ..., "meta": { ... } }` cuando aplica. Errores siguen **RFC 9457 Problem Details** con extensiones `{ code, messageKey, details }` para i18n del FE.

### 1) Autenticación

#### `POST /auth/login`

Inicia sesión de un usuario válido.

**Request**

```json
{
  "email": "user@entrelibros.com",
  "password": "correcthorsebatterystaple"
}
```

**200**

```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": { "id": "1", "email": "user@entrelibros.com", "role": "user" },
    "messageKey": "auth.success.login"
  }
}
```

Cabecera: `Set-Cookie: sessionToken=...; HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth`

**401**

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "code": "InvalidCredentials",
  "messageKey": "auth.errors.invalid_credentials"
}
```

#### `POST /auth/logout`

Requiere cookie `sessionToken` válida. Invalida refresh y expira cookie.

**200**

```json
{ "data": { "message": "Successfully logged out", "timestamp": "2024-02-20T15:00:00Z" } }
```

#### `GET /auth/me`

Devuelve el usuario autenticado (JWT `Authorization: Bearer <access>` **o** cookie `sessionToken` + refresh-flow server-side).

**200**

```json
{ "data": { "id": "u_1", "email": "demo@entrelibros.app", "roles": ["user"] } }
```

**401** → Problem Details como arriba.

#### `POST /auth/register`

**Request**

```json
{ "name": "Jane Doe", "email": "new@entrelibros.com", "password": "secreta" }
```

**201**

```json
{
  "data": {
    "token": "fake-register-token",
    "user": { "id": "2", "email": "new@entrelibros.com", "role": "user" },
    "messageKey": "auth.success.register"
  }
}
```

**409**

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "code": "EmailExists",
  "messageKey": "auth.errors.email_exists"
}
```

---

### 2) Formularios y contacto

#### `POST /contact/submit`

**Request**

```json
{ "name": "María Pérez", "email": "maria@example.com", "message": "Quisiera más información sobre la plataforma." }
```

**200**

```json
{ "data": { "message": "¡Gracias por tu mensaje! Te responderemos lo antes posible." } }
```

**400/500** → Problem Details con `messageKey` descriptivo.

> **Implementación**: encolar evento `CONTACT_SUBMITTED` y enviar email (Mailpit en dev / SMTP/Resend en prod). Rate-limit por IP.

---

### 3) Libros

#### `GET /books`

Lista pública (paginada) de libros.

**200**

```json
{
  "data": [
    { "title": "1984", "author": "George Orwell", "coverUrl": "https://covers.openlibrary.org/b/id/7222246-L.jpg" }
  ],
  "meta": { "page": 0, "size": 20, "total": 1 }
}
```

#### `GET /books/mine` (auth)

Libros publicados por el usuario autenticado.

**200**

```json
{
  "data": [
    {
      "id": "1",
      "title": "Matisse en Bélgica",
      "author": "Carlos Argan",
      "coverUrl": "https://covers.openlibrary.org/b/id/9875161-L.jpg",
      "condition": "bueno",
      "status": "available",
      "isForSale": true,
      "price": 15000
    }
  ]
}
```

---

### 4) Comunidad

#### `GET /community/stats`

**200**

```json
{
  "data": {
    "kpis": { "exchanges": 134, "activeHouses": 52, "activeUsers": 318, "booksPublished": 2140 },
    "trendExchanges": [65,80,55,90,70,40,85],
    "trendNewBooks": [30,45,35,60,50,40,55]
  }
}
```

#### `GET /community/feed`

Query params: `page` (default 0), `size` (default 8)

**200**

```json
{
  "data": [
    {
      "id": "uuid",
      "user": "Ana",
      "avatar": "https://example.com/avatar.png",
      "time": "hace 2h",
      "likes": 5,
      "type": "book",
      "title": "Dune",
      "author": "Frank Herbert",
      "cover": "https://picsum.photos/seed/1/600/400"
    }
  ],
  "meta": { "page": 0, "size": 8, "total": 1 }
}
```

#### `GET /community/activity`

**200**

```json
{ "data": [ { "id": "uuid", "user": "Lucía", "avatar": "https://example.com/avatar.png" } ] }
```

#### `GET /community/suggestions`

**200**

```json
{ "data": [ { "id": "uuid", "user": "Pedro", "avatar": "https://example.com/avatar.png" } ] }
```

---
