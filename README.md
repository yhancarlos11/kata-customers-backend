# Customers API Backend

Backend Spring Boot para el reto de ciclo de vida con ambientes DEV y PROD simulado.

## 1. Alcance

- Registro e inicio de sesion con JWT
- CRUD de clientes (crear, listar, actualizar, eliminar)
- Endpoint publico para consultar ambiente activo
- Documentacion detallada con Swagger/OpenAPI

## 2. Requisitos

- Java 21
- Maven Wrapper (incluido)

## 3. Ejecutar por ambiente

### DEV

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- Puerto: `8080`
- App name: `customers-dev`

### PROD simulado

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

- Puerto: `9090` (o `SERVER_PORT`)
- App name: `customers-prod`

## 4. Swagger / OpenAPI

Con el backend levantado:

- Swagger UI: `http://localhost:8080/swagger-ui.html` (DEV)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs` (DEV)

En PROD simulado reemplazar puerto por `9090`.

### Autenticacion JWT en Swagger

1. Ejecutar `POST /api/auth/register` o `POST /api/auth/login`.
2. Copiar el `token` de la respuesta.
3. En Swagger UI, usar boton `Authorize`.
4. Pegar el token como `Bearer <token>`.
5. Probar endpoints de `/api/customers` autenticados.

## 5. Endpoints documentados

### Autenticacion

- `POST /api/auth/register`: registra usuario y retorna JWT.
- `POST /api/auth/login`: autentica y retorna JWT.

### Clientes (requiere JWT)

- `POST /api/customers`: crea cliente.
- `GET /api/customers`: lista clientes.
- `PUT /api/customers/{id}`: actualiza cliente.
- `DELETE /api/customers/{id}`: elimina cliente.

### Ambiente (publico)

- `GET /api/info/environment`: retorna app, puerto, mensaje y perfil activo.

## 6. Build

```bash
./mvnw clean package
```

Genera:

- `target/customers-api-0.0.1-SNAPSHOT.jar`

Ejecucion con jar:

```bash
java -jar target/customers-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
java -jar target/customers-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 7. Seguridad

- No subir secretos reales al repositorio.
- JWT configurable por variables de entorno:
	- `JWT_SECRET_DEV`
	- `JWT_SECRET_PROD`
	- `JWT_EXPIRATION_MS`
