# Plan de producto — OrderFlow

## Resultado final

Una API backend B2B para administrar productos, inventario y órdenes de compra. Debe proteger el stock ante solicitudes concurrentes, evitar órdenes duplicadas, aplicar permisos por rol y poder ejecutarse de forma reproducible con Docker Compose.

## Principios de alcance

- Construir primero un monolito modular; no introducir microservicios, mensajería ni caché antes de cerrar el MVP.
- Todo cambio de esquema se realiza con Flyway.
- Las operaciones que alteran stock deben ser transaccionales y trazables.
- La API se documenta al mismo ritmo que se implementa.
- Secretos y credenciales se obtienen solo mediante variables de entorno.

## Objetivo 1 — Fundación del servicio

**Entregable:** aplicación Spring Boot conectada a PostgreSQL, ejecutable mediante Docker Compose.

- Consolidar configuración por perfiles y variables de entorno.
- Mantener Flyway como única fuente de cambios de base de datos.
- Exponer healthcheck e información básica mediante Actuator.
- Definir formato estándar de errores HTTP basado en RFC 7807.
- Incorporar OpenAPI/Swagger y convenciones de versionado bajo /api/v1.

**Criterio de cierre:** docker compose up --build deja la aplicación disponible; /actuator/health responde UP y Swagger está accesible.

## Objetivo 2 — Catálogo e inventario

**Entregable:** productos administrables con stock disponible y reservado.

- Modelar Product con identificador, SKU único, nombre, descripción opcional, precio y estado.
- Modelar inventario asociado al producto con availableQuantity, reservedQuantity y control de versión para concurrencia.
- Implementar endpoints de creación, consulta, listado y actualización del catálogo.
- Validar SKU, precios, cantidades y reglas de modificación.
- Cargar datos de desarrollo reproducibles.

**Criterio de cierre:** es posible administrar productos y consultar de forma fiable el stock disponible y reservado de cada uno.

## Objetivo 3 — Órdenes y reserva segura de stock

**Entregable:** creación de órdenes con líneas de producto y reserva atómica de inventario.

- Modelar Order y OrderItem, incluyendo cantidades, precios registrados y fechas de auditoría.
- Definir estados: PENDING, CONFIRMED, CANCELLED y FULFILLED.
- Crear órdenes validando producto, cantidad y disponibilidad de inventario.
- Reservar todo el stock requerido en una transacción; si una línea falla, no alterar ninguna cantidad.
- Aplicar bloqueo o control optimista adecuado para impedir sobreventa con solicitudes concurrentes.

**Criterio de cierre:** una orden válida reserva el inventario completo y solicitudes simultáneas no pueden superar el stock disponible.

## Objetivo 4 — Ciclo de vida y trazabilidad de órdenes

**Entregable:** transiciones de estado explícitas, seguras y auditables.

- Definir transiciones permitidas entre estados.
- Confirmar y completar órdenes según reglas de negocio.
- Cancelar una orden liberando exactamente el stock que había reservado.
- Persistir fecha, actor y motivo cuando corresponda para permitir auditoría.
- Rechazar transiciones inválidas mediante errores de dominio claros.

**Criterio de cierre:** cancelar una orden devuelve el stock una sola vez y no es posible aplicar cambios de estado incompatibles.

## Objetivo 5 — Idempotencia de creación

**Entregable:** protección contra órdenes duplicadas por reintentos del cliente.

- Aceptar una clave de idempotencia en la creación de órdenes.
- Guardar la solicitud o su huella y asociarla con el resultado producido.
- Para una clave ya procesada, devolver la misma orden sin repetir reserva ni crear registros nuevos.
- Rechazar la reutilización de una misma clave con un payload diferente.

**Criterio de cierre:** reintentos idénticos generan una sola orden y no alteran el inventario más de una vez.

## Objetivo 6 — Autenticación y autorización

**Entregable:** acceso protegido mediante JWT y roles.

- Implementar autenticación JWT.
- Definir permisos mínimos: ADMIN administra catálogo y usuarios; OPERATOR opera órdenes; CLIENT consulta y crea sus propias órdenes según las reglas definidas.
- Restringir endpoints y datos por rol o propietario.
- Documentar el flujo de autenticación para desarrollo sin incorporar secretos al repositorio.

**Criterio de cierre:** rutas protegidas rechazan solicitudes sin token o sin rol adecuado, y cada rol accede únicamente a sus operaciones permitidas.

## Objetivo 7 — Calidad, pruebas y operación local

**Entregable:** sistema verificable y reproducible para un revisor técnico.

- Crear pruebas unitarias para reglas de negocio, transiciones y validaciones.
- Crear pruebas de integración con PostgreSQL real mediante Testcontainers.
- Añadir pruebas de concurrencia para reserva y cancelación de stock.
- Mantener una colección Postman o Bruno versionada con los flujos principales.
- Documentar arquitectura, decisiones técnicas, comandos de ejecución y variables requeridas.
- Configurar GitHub Actions para compilar, ejecutar pruebas y construir la imagen Docker.

**Criterio de cierre:** un clon limpio del repositorio puede levantarse, probarse y explorarse mediante documentación y colección de API sin configuración manual oculta.

## Mejoras posteriores al MVP

- Redis para almacenamiento de idempotencia o caché, solo si se justifica tras medir una necesidad.
- Publicación de eventos mediante Kafka o RabbitMQ.
- Notificaciones o integraciones externas.
- Métricas, logs estructurados y trazas distribuidas más completas.
