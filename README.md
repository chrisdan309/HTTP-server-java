# Servicio almacen
Contienen server, app, BD
- BD: Un archivo txt
- Server: Con las operaciones CRUD básicas de la tabla almacén
- App: Tiene las reglas para poder administrar la BD

La BD ALMACEN tiene
ID_PROD
NAME_PROD
DETAIL
UNIT
AMOUNT
COST


# Servicio Ventas
Contienen server, app, BD
- BD: Un archivo txt
- Server: Con las operaciones CRUD básicas de la tabla "Ventas" y "DetalleVentas"
- App: Tiene las reglas para poder administrar la tabla venta y su detalle

Ventas
ID_SALES
RUC
NAME
COST TOTAL


ID_SALES
ID_PROD
NOMBRE
NAME_PROD
UNIT
AMOUNT
COST
TOTAL

# Servicio Cliente
Contiene server, app
- Server: Tendrá implementado el servidor web donde podrá hacer las ventas y sus items en el detalle ventas
- App: Que tiene las reglas pra realizar la venta con el "servicio ventas"y consultas si se tienen existencias en el almacen con el "servicio almacen"

