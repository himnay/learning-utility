# learning-utility

Two small, independent demo REST APIs in one Spring Boot app:

- **`com.learning.qr`** — upload an image, get back the QR code it contains decoded (ZXing).
- **`com.learning.totp`** — generate and verify RFC 6238 time-based one-time codes
  (`dev.samstevens.totp`), with the seed persisted in Postgres.

Request/response DTOs for both packages (plus the shared `ApiError` shape) are **generated at
build time** from OpenAPI specs under `src/main/resources/openapi/` via
`openapi-generator-maven-plugin` — they are not hand-written. Controllers and services are
hand-written and reference the generated classes directly.

---

## Tech Stack

| Concern           | Technology                                                                                                                             |
|-------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Language          | Java 25                                                                                                                                |
| Framework         | Spring Boot 4.1.0, Spring MVC                                                                                                          |
| QR decoding       | ZXing (`com.google.zxing:core` + `javase`)                                                                                             |
| TOTP              | `dev.samstevens.totp:totp`                                                                                                             |
| Persistence       | Spring JDBC (`JdbcTemplate`) + PostgreSQL + Flyway                                                                                     |
| API docs          | springdoc-openapi (Swagger UI)                                                                                                         |
| Model generation  | `openapi-generator-maven-plugin` (generator: `spring`, models only)                                                                    |
| Build             | Maven — parent `com.org.llm:super-pom`, deps from `com.org.learning:learning-bom` (no version is hardcoded in this module's `pom.xml`) |

---

## Project Structure

```
src/main/java/com/learning/
  qr/
    web/QrCodeController.java        POST /qr/scan
    service/QrCodeService.java       ZXing decode logic
    exception/QrDecodeException.java
  totp/
    web/TotpController.java          POST /totp/generate, POST /totp/verify
    service/TotpService.java         dev.samstevens.totp generate/verify + otpauth URI building
    domain/TotpSeed.java             persisted row (hand-written, not OpenAPI-generated)
    repository/TotpSeedRepository.java  JdbcTemplate, upsert-on-conflict
    exception/TotpAccountNotFoundException.java
  common/web/GlobalExceptionHandler.java   shared @RestControllerAdvice

src/main/resources/
  openapi/
    common-api.yaml   -> generates com.learning.common.web.dto.ApiError
    qr-api.yaml       -> generates com.learning.qr.web.dto.QrDecodeResponse
    totp-api.yaml     -> generates com.learning.totp.web.dto.Totp*Request/Response
  db/migration/V1__create_totp_seed.sql
```

---

## API

### `POST /qr/scan` — decode a QR code from an uploaded image

```bash
curl -s -X POST http://localhost:8095/qr/scan -F "file=@/path/to/qr.png"
```

```json
{ "text": "https://example.com", "format": "QR_CODE" }
```

`400` with an `ApiError` body if the file is empty, isn't a readable image, or contains no QR code.

### `POST /totp/generate` — create and persist a new seed

```bash
curl -s -X POST http://localhost:8095/totp/generate \
  -H "Content-Type: application/json" \
  -d '{"accountName": "alice@example.com"}'
```

```json
{
  "accountName": "alice@example.com",
  "secret": "JBSWY3DPEHPK3PXP",
  "currentCode": "123456",
  "otpAuthUri": "otpauth://totp/alice%40example.com?secret=JBSWY3DPEHPK3PXP&issuer=learning-utility&algorithm=SHA1&digits=6&period=30"
}
```

`secret` is stored in Postgres (`totp_seed` table, unique on `account_name`); calling `/generate`
again for the same account **rotates** the secret. `currentCode` is the 6-digit code valid right
now — included purely for demo convenience (normally an authenticator app computes this from the
`otpAuthUri`/secret on the client side, not the server).

### `POST /totp/verify` — verify a code against the saved seed

```bash
curl -s -X POST http://localhost:8095/totp/verify \
  -H "Content-Type: application/json" \
  -d '{"accountName": "alice@example.com", "code": "123456"}'
```

```json
{ "valid": true }
```

`404` if no seed has been generated yet for that account.

### `GET /totp/{accountName}/qrcode` — render the enrollment URI as a scannable QR code

```bash
curl -s http://localhost:8095/totp/alice@example.com/qrcode -o qr.png
```

Returns an `image/png` body — open `qr.png` and scan it with Google Authenticator/Authy to enroll
without typing the secret by hand. Built with `dev.samstevens.totp`'s `ZxingPngQrGenerator`
(backed by the same ZXing library `com.learning.qr` uses to decode). `404` if no seed exists yet
for the account.

An Insomnia collection covering all four requests is at `insomnia-collection.json` (import it
directly — no auth/environment setup needed beyond the `baseUrl` variable, defaulted to
`http://localhost:8095`).

---

## Running

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

Swagger UI: `http://localhost:8095/swagger-ui.html`

---

## Testing

```bash
./mvnw test
```

- `QrCodeServiceTest` — generates a real QR PNG in-memory (ZXing encoder) and asserts the service
  decodes it back to the original text; also covers the no-QR-found and not-an-image error paths.
- `QrCodeControllerTest` / `TotpControllerTest` — `@WebMvcTest` slices with the service mocked.
- `TotpServiceTest` — verifies a code the same `generate()` call just produced is accepted, and a
  wrong code / unknown account are rejected appropriately.
- `TotpSeedRepositoryTest` — Testcontainers Postgres, round-trips `upsert`/`findByAccountName`
  including the on-conflict secret-rotation behavior.

---

## Configuration

| Property                    | Env var                                       | Default                                    |
|-----------------------------|-----------------------------------------------|--------------------------------------------|
| `server.port`               | `SERVER_PORT`                                 | `8095`                                     |
| `spring.datasource.url`     | `POSTGRES_HOST`/`POSTGRES_PORT`/`POSTGRES_DB` | `jdbc:postgresql://localhost:5433/utility` |
| multipart max file size     | —                                             | `10MB`                                     |
