-- Sistema de Control de Cafetines UES
-- Script para base de datos de Servicios Web
-- Motor esperado: MySQL/MariaDB

SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Ubicaciones (
    id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre_ubicacion VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE Locales (
    id_local INT AUTO_INCREMENT PRIMARY KEY,
    nombre_local VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(30) NOT NULL,
    imagen_uri TEXT
);

CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    carnet VARCHAR(20) NOT NULL UNIQUE,
    id_rol INT NOT NULL,
    id_ubicacion INT,
    id_local_asignado INT,
    activo TINYINT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion),
    FOREIGN KEY (id_local_asignado) REFERENCES Locales(id_local)
);

CREATE TABLE Productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre_producto VARCHAR(120) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    disponibilidad VARCHAR(30) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    id_local INT NOT NULL,
    imagen_uri TEXT,
    FOREIGN KEY (id_local) REFERENCES Locales(id_local)
);

CREATE TABLE Pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    fecha_pedido DATETIME NOT NULL,
    tipo_pedido VARCHAR(50) NOT NULL,
    estado_pedido VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    id_usuario INT NOT NULL,
    id_ubicacion INT,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion)
);

CREATE TABLE Detalle_Pedido (
    id_detalle_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)
);

CREATE TABLE Pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_pago DATETIME NOT NULL,
    referencia VARCHAR(100),
    estado_pago VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido)
);

CREATE TABLE Pedidos_Especiales (
    id_pedido_especial INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    descripcion_evento TEXT NOT NULL,
    fecha_evento DATE NOT NULL,
    hora_evento TIME,
    numero_personas INT,
    monto_minimo DECIMAL(10, 2) NOT NULL,
    monto_maximo DECIMAL(10, 2) NOT NULL,
    anticipo DECIMAL(10, 2) NOT NULL,
    referencia_pago VARCHAR(100),
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido)
);

CREATE TABLE OpcionesMenu (
    id_opcion INT AUTO_INCREMENT PRIMARY KEY,
    nombre_opcion VARCHAR(100) NOT NULL UNIQUE,
    descripcion_opcion TEXT,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE Roles_OpcionesMenu (
    id_rol_opcion INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    id_opcion INT NOT NULL,
    UNIQUE (id_rol, id_opcion),
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
    FOREIGN KEY (id_opcion) REFERENCES OpcionesMenu(id_opcion)
);
