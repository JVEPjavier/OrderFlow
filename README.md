# OrderFlow

API B2B para órdenes e inventario. Primera base del proyecto de portafolio.

## Arranque local con Docker

    docker compose up --build

- Estado del servicio: http://localhost:8080/api/v1/status
- Healthcheck: http://localhost:8080/actuator/health

Para detener y conservar la base: docker compose down.
Para borrar los datos locales: docker compose down -v.

La contraseña por defecto es exclusivamente para desarrollo local. En cualquier entorno compartido se debe definir POSTGRES_PASSWORD.
