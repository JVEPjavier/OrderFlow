# Plan del Objetivo 2 — Catálogo e inventario

## Resultado esperado

Un catálogo de productos administrable por API, con inventario separado que exponga stock disponible y reservado. El modelo debe ser estable para que el Objetivo 3 pueda reservar stock concurrentemente sin rediseñar la base de datos.

## Decisiones de diseño

- Producto e inventario son entidades distintas con relación uno a uno.
- El producto contiene identidad comercial y el inventario únicamente cantidades y versión de concurrencia.
- SKU es único, inmutable después de crear el producto y se normaliza antes de persistir.
- Precio se representa con BigDecimal y precisión fija en PostgreSQL; nunca con float o double.
- Las cantidades son enteros no negativos y reservedQuantity comienza en cero.
- Inventory tendrá versión optimista desde el inicio mediante un campo version. La reserva real se implementará en el Objetivo 3.
- La modificación de metadatos de producto se separa de los ajustes de inventario.

## Entidades y esquema

### Product

- UUID como identificador público.
- SKU único.
- Nombre.
- Descripción opcional.
- Precio monetario.
- Estado activo o inactivo.
- createdAt y updatedAt.

### Inventory

- Identificador interno o clave ligada al producto.
- productId único.
- availableQuantity.
- reservedQuantity.
- version para control optimista.
- updatedAt.

## Migraciones Flyway

1. Crear tabla products con restricción única para SKU e índices de consulta necesarios.
2. Crear tabla inventories con relación uno a uno a products, checks de cantidades no negativas y columna de versión.
3. Agregar datos de demostración solo en un mecanismo de desarrollo aislado; no incluir datos de portafolio en producción por defecto.

No se modifica V1. Todo se agrega mediante migraciones nuevas.

## Contratos REST

### Catálogo

- POST /api/v1/products crea un producto con stock inicial disponible.
- GET /api/v1/products lista productos con paginación, ordenamiento y filtro opcional por SKU, nombre o estado.
- GET /api/v1/products/{productId} devuelve producto e inventario.
- PATCH /api/v1/products/{productId} actualiza nombre, descripción, precio o estado, sin permitir cambiar SKU.

### Inventario

- GET /api/v1/products/{productId}/inventory consulta cantidades y versión.
- POST /api/v1/products/{productId}/inventory/adjustments registra un ajuste explícito de stock disponible con cantidad, motivo y referencia opcional.

Los ajustes no pueden dejar stock disponible bajo cero y no deben alterar stock reservado. Las reservas y liberaciones quedan fuera de este objetivo.

## Validaciones y errores

- SKU obligatorio, normalizado y único.
- Nombre obligatorio con límite de longitud.
- Precio positivo y con escala monetaria definida.
- Stock inicial y ajustes como enteros válidos.
- Producto inexistente responde 404.
- SKU duplicado y conflictos de versión responden 409.
- Errores de formato o validación responden ProblemDetail 400 con fieldErrors.

## Seguridad temporal

Durante este objetivo los endpoints se mantienen sin autenticación, porque JWT pertenece al Objetivo 6. Los controllers y contratos se diseñarán para incorporar autorización por rol sin romper rutas ni payloads.

## Pruebas necesarias

- Unidad: validaciones, normalización de SKU y reglas de ajuste.
- Integración: migraciones, persistencia, unicidad de SKU y paginación contra PostgreSQL mediante Testcontainers.
- Concurrencia básica: dos ajustes simultáneos detectan conflicto mediante control optimista.
- API: casos exitosos, 400, 404 y 409 con el formato de error estándar.

## Criterio de cierre

Un consumidor puede crear, consultar, listar y actualizar productos; consultar inventario; realizar ajustes válidos; y no puede crear SKU duplicados ni cantidades negativas. Todo queda documentado en Swagger, cubierto por pruebas relevantes y ejecutable en Docker.
