-- Sistema de Control de Cafetines UES
-- Script SQLite - Estructura de base de datos local
-- Compatible con DatabaseContract.DATABASE_VERSION = 7

PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS Roles_OpcionesMenu;
DROP TABLE IF EXISTS OpcionesMenu;
DROP TABLE IF EXISTS Pedidos_Especiales;
DROP TABLE IF EXISTS Pagos;
DROP TABLE IF EXISTS Detalle_Pedido;
DROP TABLE IF EXISTS Pedidos;
DROP TABLE IF EXISTS Productos;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Locales;
DROP TABLE IF EXISTS Ubicaciones;
DROP TABLE IF EXISTS Roles;

PRAGMA foreign_keys = ON;

CREATE TABLE Roles (
    id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_rol TEXT NOT NULL UNIQUE
);

CREATE TABLE Ubicaciones (
    id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_ubicacion TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE Locales (
    id_local INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_local TEXT NOT NULL,
    ubicacion TEXT NOT NULL,
    descripcion TEXT,
    estado TEXT NOT NULL,
    imagen_uri TEXT
);

CREATE TABLE Usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    carnet TEXT NOT NULL UNIQUE,
    id_rol INTEGER NOT NULL,
    id_ubicacion INTEGER,
    id_local_asignado INTEGER,
    activo INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion),
    FOREIGN KEY (id_local_asignado) REFERENCES Locales(id_local)
);

CREATE TABLE Productos (
    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_producto TEXT NOT NULL,
    precio REAL NOT NULL,
    disponibilidad TEXT NOT NULL,
    tipo TEXT NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    id_local INTEGER NOT NULL,
    imagen_uri TEXT,
    FOREIGN KEY (id_local) REFERENCES Locales(id_local)
);

CREATE TABLE Pedidos (
    id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_pedido TEXT NOT NULL,
    tipo_pedido TEXT NOT NULL,
    estado_pedido TEXT NOT NULL,
    total REAL NOT NULL,
    id_usuario INTEGER NOT NULL,
    id_ubicacion INTEGER,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion)
);

CREATE TABLE Detalle_Pedido (
    id_detalle_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pedido INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario REAL NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)
);

CREATE TABLE Pagos (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pedido INTEGER NOT NULL,
    metodo_pago TEXT NOT NULL,
    monto REAL NOT NULL,
    fecha_pago TEXT NOT NULL,
    referencia TEXT,
    estado_pago TEXT NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido)
);

CREATE TABLE Pedidos_Especiales (
    id_pedido_especial INTEGER PRIMARY KEY AUTOINCREMENT,
    id_pedido INTEGER NOT NULL,
    descripcion_evento TEXT NOT NULL,
    fecha_evento TEXT NOT NULL,
    hora_evento TEXT,
    numero_personas INTEGER,
    monto_minimo REAL NOT NULL,
    monto_maximo REAL NOT NULL,
    anticipo REAL NOT NULL,
    referencia_pago TEXT,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido)
);

CREATE TABLE OpcionesMenu (
    id_opcion INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_opcion TEXT NOT NULL UNIQUE,
    descripcion_opcion TEXT,
    estado TEXT NOT NULL
);

CREATE TABLE Roles_OpcionesMenu (
    id_rol_opcion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_rol INTEGER NOT NULL,
    id_opcion INTEGER NOT NULL,
    UNIQUE (id_rol, id_opcion),
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
    FOREIGN KEY (id_opcion) REFERENCES OpcionesMenu(id_opcion)
);
