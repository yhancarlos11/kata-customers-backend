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

- Variables para JWT:
	- `JWT_SECRET_DEV`
	- `JWT_SECRET_PROD`
	- `JWT_EXPIRATION_MS`

## Docker local por ambientes (profiles)

Desde carpeta `kata-customers-backend` crea un archivo `.env` local con las llaves JWT:

```dotenv
JWT_SECRET_DEV=<tu_clave_dev>
JWT_SECRET_PROD=<tu_clave_prod>
```

### Levantar ambiente DEV (front + back)

```powershell
docker compose down
docker compose --profile dev up --build -d
```

Servicios en `dev`:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

### Levantar ambiente PROD simulado (front + back + db)

```powershell
docker compose down
docker compose --profile prod up --build -d
```

Servicios en `prod`:

- Frontend: `http://localhost:4201`
- Backend: `http://localhost:9090`
- Swagger: `http://localhost:9090/swagger-ui.html`
- PostgreSQL: `localhost:5432`

Detener stack:

```powershell
docker compose down
```

### Atajos PowerShell (dev-up / prod-up)

Se incluyen dos scripts para cambiar ambiente en un solo comando sin cruces:

- `dev-up.ps1` -> baja contenedores previos y levanta perfil `dev`
- `prod-up.ps1` -> baja contenedores previos y levanta perfil `prod`

Uso:

```powershell
cd .\kata-customers-backend
powershell -ExecutionPolicy Bypass -File .\dev-up.ps1
powershell -ExecutionPolicy Bypass -File .\prod-up.ps1
```

## Opcion 2 (Docker local + cloud)

Objetivo:

- Local: `docker compose` para desarrollo.
- Cloud: Frontend en Vercel + Backend en Render + DB en Neon

## Configuracion de despliegue backend (Render + Neon)

Variables requeridas en Render:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://<NEON_HOST>:5432/<NEON_DB>?sslmode=require`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver`
- `SPRING_DATASOURCE_USERNAME=<NEON_USER>`
- `SPRING_DATASOURCE_PASSWORD=<NEON_PASSWORD>`
- `JWT_SECRET_PROD=<secreto_largo>`
- `JWT_EXPIRATION_MS=900000`

Validacion cloud backend:

- `https://kata-customers-backend.onrender.com/swagger-ui.html`
- `https://kata-customers-backend.onrender.com/v3/api-docs`

## Despliegue continuo (CD)

Se conecta render con `auto deploy` activo:

- Cada push a rama principal dispara build/deploy automatico.

## Integracion continua (CI) y gate de calidad

Se agrego workflow de CI en:

- `.github/workflows/backend-ci.yml`

El workflow ejecuta en cada push/PR a `main`:

- pruebas unitarias (`./mvnw -B test`)
- build del jar (`./mvnw -B -DskipTests package`)

Para que el despliegue a produccion quede condicionado a CI:

- Se habilita Branch protection sobre `main`
