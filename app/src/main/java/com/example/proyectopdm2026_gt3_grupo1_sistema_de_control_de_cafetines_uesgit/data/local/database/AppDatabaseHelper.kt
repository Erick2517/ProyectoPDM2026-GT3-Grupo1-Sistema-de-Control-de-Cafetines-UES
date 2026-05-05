package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_ROLES_TABLE)
        db.execSQL(CREATE_UBICACIONES_TABLE)
        db.execSQL(CREATE_USUARIOS_TABLE)
        db.execSQL(CREATE_LOCALES_TABLE)
        db.execSQL(CREATE_PRODUCTOS_TABLE)
        db.execSQL(CREATE_PEDIDOS_TABLE)
        db.execSQL(CREATE_DETALLE_PEDIDO_TABLE)
        db.execSQL(CREATE_PAGOS_TABLE)
        db.execSQL(CREATE_PEDIDOS_ESPECIALES_TABLE)
        insertarRolesBase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.PedidosEspeciales.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Pagos.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.DetallePedido.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Pedidos.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Productos.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Locales.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Usuarios.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Ubicaciones.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Roles.TABLE_NAME}")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun insertarRolesBase(db: SQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO ${DatabaseContract.Roles.TABLE_NAME}
            (${DatabaseContract.Roles.NOMBRE_ROL})
            VALUES ('Usuario'), ('Administrador'), ('Encargado')
            """.trimIndent()
        )
    }

    private companion object {
        const val CREATE_ROLES_TABLE = """
            CREATE TABLE Roles (
                id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_rol TEXT NOT NULL UNIQUE
            )
        """

        const val CREATE_UBICACIONES_TABLE = """
            CREATE TABLE Ubicaciones (
                id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_ubicacion TEXT NOT NULL UNIQUE,
                descripcion TEXT
            )
        """

        const val CREATE_USUARIOS_TABLE = """
            CREATE TABLE Usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                carnet TEXT NOT NULL UNIQUE,
                id_rol INTEGER NOT NULL,
                id_ubicacion INTEGER,
                activo INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
                FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion)
            )
        """

        const val CREATE_LOCALES_TABLE = """
            CREATE TABLE Locales (
                id_local INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_local TEXT NOT NULL,
                ubicacion TEXT NOT NULL,
                descripcion TEXT,
                estado TEXT NOT NULL
            )
        """

        const val CREATE_PRODUCTOS_TABLE = """
            CREATE TABLE Productos (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_producto TEXT NOT NULL,
                precio REAL NOT NULL,
                disponibilidad TEXT NOT NULL,
                tipo TEXT NOT NULL,
                stock INTEGER NOT NULL DEFAULT 0,
                id_local INTEGER NOT NULL,
                FOREIGN KEY (id_local) REFERENCES Locales(id_local)
            )
        """

        const val CREATE_PEDIDOS_TABLE = """
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
            )
        """

        const val CREATE_DETALLE_PEDIDO_TABLE = """
            CREATE TABLE Detalle_Pedido (
                id_detalle_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_producto INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precio_unitario REAL NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),
                FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)
            )
        """

        const val CREATE_PAGOS_TABLE = """
            CREATE TABLE Pagos (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                metodo_pago TEXT NOT NULL,
                monto REAL NOT NULL,
                fecha_pago TEXT NOT NULL,
                referencia TEXT,
                estado_pago TEXT NOT NULL,
                FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido)
            )
        """

        const val CREATE_PEDIDOS_ESPECIALES_TABLE = """
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
            )
        """
    }
}
