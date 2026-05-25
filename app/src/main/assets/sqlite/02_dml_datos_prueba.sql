-- Sistema de Control de Cafetines UES
-- Script SQLite - Datos de prueba
-- Ejecutar despues de 01_schema_sqlite.sql
-- Incluye mas de 15 registros para pruebas funcionales RF01-RF10.

PRAGMA foreign_keys = ON;

INSERT OR IGNORE INTO Roles (id_rol, nombre_rol) VALUES
(1, 'Usuario'),
(2, 'Administrador'),
(3, 'Encargado');

INSERT OR IGNORE INTO Ubicaciones (id_ubicacion, nombre_ubicacion, descripcion) VALUES
(1, 'Campus Central', 'Ubicacion general dentro del campus universitario.'),
(2, 'Facultad de Ingenieria', 'Zona de Ingenieria dentro del campus universitario.'),
(3, 'Biblioteca Central', 'Zona de biblioteca central y areas de estudio.');

INSERT OR IGNORE INTO Locales (id_local, nombre_local, ubicacion, descripcion, estado, imagen_uri) VALUES
(1, 'Cafetín Central', 'Plaza central', 'Local principal con desayunos, almuerzos y bebidas.', 'Activo', NULL),
(2, 'Cafetín Ingeniería', 'Facultad de Ingeniería', 'Local cercano a edificios de aulas y laboratorios.', 'Activo', NULL),
(3, 'Cafetín Biblioteca', 'Biblioteca central', 'Punto de venta de refrigerios y bebidas.', 'Activo', NULL),
(4, 'Cafetín Salud', 'Facultad de Medicina', 'Local de comidas rápidas y bebidas frías.', 'Inactivo', NULL);

INSERT OR IGNORE INTO Usuarios
(id_usuario, nombre, email, password, carnet, id_rol, id_ubicacion, id_local_asignado, activo)
VALUES
(1, 'Administrador Sistema', 'admin@ues.edu.sv', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'AD00001', 2, 1, NULL, 1),
(2, 'Encargado Cafetin', 'encargado@ues.edu.sv', 'd30bbd4ee45b2bc2c6e586d198e2a7fb03a633f9a6e1fc58ee5ff9fd975be6e0', 'EN00001', 3, 1, 1, 1),
(3, 'Maria Lopez', 'maria.lopez@ues.edu.sv', '66d4fca6f91a71a033d2369ad7a302bf83b925364cb73bcc9677e378c4685437', 'ML23001', 1, 2, NULL, 1),
(4, 'Carlos Perez', 'carlos.perez@ues.edu.sv', '66d4fca6f91a71a033d2369ad7a302bf83b925364cb73bcc9677e378c4685437', 'CP23002', 1, 3, NULL, 1);

INSERT OR IGNORE INTO Productos
(id_producto, nombre_producto, precio, disponibilidad, tipo, stock, id_local, imagen_uri)
VALUES
(1, 'Desayuno típico', 2.50, 'Disponible', 'Desayuno', 20, 1, NULL),
(2, 'Café americano', 0.75, 'Disponible', 'Bebida', 35, 1, NULL),
(3, 'Empanadas de leche', 1.00, 'Disponible', 'Antojito', 12, 1, NULL),
(4, 'Sándwich de pollo', 2.25, 'Disponible', 'Almuerzo', 18, 2, NULL),
(5, 'Jugo natural', 1.00, 'Disponible', 'Bebida', 25, 2, NULL),
(6, 'Nuegados', 1.25, 'Disponible', 'Antojito', 10, 2, NULL),
(7, 'Pan dulce', 0.60, 'Disponible', 'Refrigerio', 30, 3, NULL),
(8, 'Chocolate caliente', 0.90, 'Disponible', 'Bebida', 20, 3, NULL),
(9, 'Tamal de elote', 1.50, 'No disponible', 'Antojito', 0, 3, NULL),
(10, 'Almuerzo ejecutivo', 3.25, 'Disponible', 'Almuerzo', 15, 1, NULL);

INSERT OR IGNORE INTO OpcionesMenu
(id_opcion, nombre_opcion, descripcion_opcion, estado)
VALUES
(1, 'Ver locales', 'Permite consultar cafetines activos y sus productos.', 'Activo'),
(2, 'Mis pedidos', 'Permite consultar historial y detalle de pedidos propios.', 'Activo'),
(3, 'Pedido especial', 'Permite solicitar pedidos especiales para eventos.', 'Activo'),
(4, 'Gestionar locales', 'Permite administrar locales o cafetines.', 'Activo'),
(5, 'Gestionar productos', 'Permite administrar productos, precios, stock y disponibilidad.', 'Activo'),
(6, 'Gestionar usuarios', 'Permite administrar usuarios y roles.', 'Activo'),
(7, 'Control de pedidos', 'Permite consultar y actualizar estados de pedidos.', 'Activo');

INSERT OR IGNORE INTO Roles_OpcionesMenu (id_rol_opcion, id_rol, id_opcion) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 2, 4),
(5, 2, 5),
(6, 2, 6),
(7, 2, 7),
(8, 3, 5),
(9, 3, 7);

INSERT OR IGNORE INTO Pedidos
(id_pedido, fecha_pedido, tipo_pedido, estado_pedido, total, id_usuario, id_ubicacion)
VALUES
(1, '2026-05-20 10:15:00', 'Reserva', 'Pagado', 3.25, 3, 2),
(2, '2026-05-20 14:20:00', 'Entrega', 'En preparacion', 2.50, 4, 3),
(3, '2026-05-21 09:30:00', 'Reserva', 'Pendiente', 6.50, 3, 1),
(4, '2026-05-22 13:45:00', 'Pedido especial', 'Pendiente', 18.00, 4, 1);

INSERT OR IGNORE INTO Detalle_Pedido
(id_detalle_pedido, id_pedido, id_producto, cantidad, precio_unitario, subtotal)
VALUES
(1, 1, 1, 1, 2.50, 2.50),
(2, 1, 2, 1, 0.75, 0.75),
(3, 2, 3, 2, 1.00, 2.00),
(4, 2, 5, 1, 1.00, 1.00),
(5, 3, 10, 2, 3.25, 6.50),
(6, 4, 4, 4, 2.25, 9.00),
(7, 4, 5, 9, 1.00, 9.00);

INSERT OR IGNORE INTO Pagos
(id_pago, id_pedido, metodo_pago, monto, fecha_pago, referencia, estado_pago)
VALUES
(1, 1, 'Efectivo', 3.25, '2026-05-20 10:20:00', 'EF-0001', 'Confirmado'),
(2, 2, 'Tarjeta', 2.50, '2026-05-20 14:25:00', 'TJ-0002', 'Confirmado'),
(3, 3, 'Bitcoin', 3.00, '2026-05-21 09:40:00', 'BTC-0003', 'Pendiente'),
(4, 4, 'Efectivo', 5.00, '2026-05-22 13:50:00', 'ANT-0004', 'Confirmado');

INSERT OR IGNORE INTO Pedidos_Especiales
(id_pedido_especial, id_pedido, descripcion_evento, fecha_evento, hora_evento, numero_personas, monto_minimo, monto_maximo, anticipo, referencia_pago)
VALUES
(1, 4, 'Refrigerio para jornada académica de Ingeniería', '2026-05-30', '09:00', 12, 15.00, 25.00, 5.00, 'ANT-0004');
