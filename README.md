# Customers API Backend

Backend Spring Boot para el reto de ciclo de vida con ambientes DEV y PROD simulado.

## Arquitectura

- Arquitectura por capas:
	- `controller`: expone endpoints REST.
	- `service`: contiene reglas de negocio.
	- `repository`: acceso a datos con Spring Data JPA.
	- `config/security/common`: configuraciones transversales (JWT, seguridad, OpenAPI, manejo de errores).
- Seguridad stateless con JWT (`Bearer token`).
- Persistencia con JPA sobre PostgreSQL en perfil `prod`.
- Documentacion API con OpenAPI/Swagger.

## Frameworks y librerias

- Java 21
- Spring Boot 3.5.0
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- PostgreSQL
- JJWT (firma y validacion de JWT)
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito (pruebas unitarias)
- Docker + Docker Compose

## Comandos importantes (Windows)

## Instalar/compilar rapido

```powershell
.\mvnw.cmd clean package -DskipTests
```

## Ejecutar backend en DEV

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

- Puerto: `8080`
- App name: `customers-dev`

## Ejecutar backend en PROD simulado

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

- Puerto: `9090` (o variable `SERVER_PORT`)
- App name: `customers-prod`

## Ejecutar pruebas unitarias

```powershell
.\mvnw.cmd test
```

## Build ejecutable

```powershell
.\mvnw.cmd clean package
```

Salida:

- `target/customers-api-0.0.1-SNAPSHOT.jar`

## Ejecutar JAR

```powershell
java -jar target/customers-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
java -jar target/customers-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Swagger / OpenAPI

Con la API levantada en DEV:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

En PROD simulado usar puerto `9090`.

## Endpoints principales

- Autenticacion:
	- `POST /api/auth/register`
	- `POST /api/auth/login`
- Clientes (requiere JWT):
	- `POST /api/customers`
	- `GET /api/customers`
	- `PUT /api/customers/{id}`
	- `DELETE /api/customers/{id}`
- Ambiente (publico):
	- `GET /api/info/environment`

## Seguridad

- No subir credenciales reales ni secretos al repositorio.
- Variables para JWT:
	- `JWT_SECRET_DEV`
	- `JWT_SECRET_PROD`
	- `JWT_EXPIRATION_MS`

## Docker local completo (front + back + db)

Desde carpeta `kata-customers-backend`:

```powershell
docker compose up --build
```

Servicios:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:9090`
- Swagger: `http://localhost:9090/swagger-ui.html`
- PostgreSQL: `localhost:5432`

Detener stack:

```powershell
docker compose down
```

## Opcion 2 (Docker local + cloud)

Objetivo:

- Local: `docker compose` para desarrollo.
- Cloud: Frontend en Vercel + Backend en Render + DB en Neon.

Archivos de apoyo incluidos en repo:

- `render.yaml` (base para Render)
- `../kata-customers-frontend/vercel.json` (rewrite `/api` al backend cloud)
- `../.env.example` (variables de referencia)

## Despliegue continuo (CD)

Si conectas tu repo en Vercel y Render con `auto deploy` activo:

- Cada push a rama principal dispara build/deploy automatico.
- Esto SI es despliegue continuo (CD).
