# OrderFlow

API B2B para órdenes e inventario construida con Java 21, Spring Boot, PostgreSQL y Flyway.

## Requisitos

Solo Docker Engine y Docker Compose. No se requiere Java instalado en el host.

## Arranque local

1. Copiar .env.example a .env.
2. Reemplazar POSTGRES_PASSWORD por un secreto local.
3. Ejecutar docker compose up --build.

## Rutas operativas

- Estado del servicio: http://localhost:8080/api/v1/status
- Health: http://localhost:8080/actuator/health
- Información: http://localhost:8080/actuator/info
- Swagger UI: http://localhost:8080/swagger-ui
- OpenAPI: http://localhost:8080/v3/api-docs

## Base de datos y migraciones

Flyway administra el esquema desde src/main/resources/db/migration. No se deben editar migraciones que ya hayan sido aplicadas. Para eliminar exclusivamente los datos locales de Docker: docker compose down -v.

## Seguridad

La contraseña de PostgreSQL se proporciona por POSTGRES_PASSWORD y el archivo .env está ignorado por Git. El valor del archivo de ejemplo no es un secreto válido.
