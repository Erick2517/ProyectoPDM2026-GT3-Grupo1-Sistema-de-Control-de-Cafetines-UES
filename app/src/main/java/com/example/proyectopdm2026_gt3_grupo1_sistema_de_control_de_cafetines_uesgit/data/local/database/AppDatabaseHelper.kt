package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database

import android.content.Context
import android.content.ContentValues
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
        insertarUbicacionesBase(db)
        insertarCatalogoBase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            insertarUbicacionesBase(db)
        }

        if (oldVersion < 3) {
            insertarCatalogoBase(db)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun insertarRolesBase(db: SQLiteDatabase) {
        db.execSQL(
            """
            INSERT OR IGNORE INTO ${DatabaseContract.Roles.TABLE_NAME}
            (${DatabaseContract.Roles.NOMBRE_ROL})
            VALUES ('Usuario'), ('Administrador'), ('Encargado')
            """.trimIndent()
        )
    }

    private fun insertarUbicacionesBase(db: SQLiteDatabase) {
        db.execSQL(
            """
            INSERT OR IGNORE INTO ${DatabaseContract.Ubicaciones.TABLE_NAME}
            (${DatabaseContract.Ubicaciones.NOMBRE_UBICACION}, ${DatabaseContract.Ubicaciones.DESCRIPCION})
            VALUES
            ('Campus Central', 'Ubicación general dentro del campus universitario.'),
            ('Facultad de Ingeniería', 'Zona de Ingeniería dentro del campus universitario.')
            """.trimIndent()
        )
    }

    private fun insertarCatalogoBase(db: SQLiteDatabase) {
        if (contarRegistros(db, DatabaseContract.Locales.TABLE_NAME) == 0) {
            insertarLocalBase(
                db,
                nombre = "Cafetín Central",
                ubicacion = "Plaza central",
                descripcion = "Local principal con desayunos, almuerzos y bebidas.",
                estado = "Activo"
            )
            insertarLocalBase(
                db,
                nombre = "Cafetín Ingeniería",
                ubicacion = "Facultad de Ingeniería",
                descripcion = "Local cercano a edificios de aulas y laboratorios.",
                estado = "Activo"
            )
            insertarLocalBase(
                db,
                nombre = "Cafetín Biblioteca",
                ubicacion = "Biblioteca central",
                descripcion = "Punto de venta de refrigerios y bebidas.",
                estado = "Activo"
            )
        }

        if (contarRegistros(db, DatabaseContract.Productos.TABLE_NAME) == 0) {
            insertarProductosBase(db)
        }
    }

    private fun insertarProductosBase(db: SQLiteDatabase) {
        val idCentral = obtenerIdLocalPorNombre(db, "Cafetín Central")
        val idIngenieria = obtenerIdLocalPorNombre(db, "Cafetín Ingeniería")
        val idBiblioteca = obtenerIdLocalPorNombre(db, "Cafetín Biblioteca")

        if (idCentral != null) {
            insertarProductoBase(db, "Desayuno típico", 2.50, "Disponible", "Desayuno", 20, idCentral)
            insertarProductoBase(db, "Café americano", 0.75, "Disponible", "Bebida", 35, idCentral)
            insertarProductoBase(db, "Empanadas de leche", 1.00, "Disponible", "Antojito", 12, idCentral)
        }

        if (idIngenieria != null) {
            insertarProductoBase(db, "Sándwich de pollo", 2.25, "Disponible", "Almuerzo", 18, idIngenieria)
            insertarProductoBase(db, "Jugo natural", 1.00, "Disponible", "Bebida", 25, idIngenieria)
            insertarProductoBase(db, "Nuegados", 1.25, "Disponible", "Antojito", 10, idIngenieria)
        }

        if (idBiblioteca != null) {
            insertarProductoBase(db, "Pan dulce", 0.60, "Disponible", "Refrigerio", 30, idBiblioteca)
            insertarProductoBase(db, "Chocolate caliente", 0.90, "Disponible", "Bebida", 20, idBiblioteca)
            insertarProductoBase(db, "Tamal de elote", 1.50, "No disponible", "Antojito", 0, idBiblioteca)
        }
    }

    private fun insertarLocalBase(
        db: SQLiteDatabase,
        nombre: String,
        ubicacion: String,
        descripcion: String,
        estado: String
    ) {
        val values = ContentValues().apply {
            put(DatabaseContract.Locales.NOMBRE_LOCAL, nombre)
            put(DatabaseContract.Locales.UBICACION, ubicacion)
            put(DatabaseContract.Locales.DESCRIPCION, descripcion)
            put(DatabaseContract.Locales.ESTADO, estado)
        }
        db.insert(DatabaseContract.Locales.TABLE_NAME, null, values)
    }

    private fun insertarProductoBase(
        db: SQLiteDatabase,
        nombre: String,
        precio: Double,
        disponibilidad: String,
        tipo: String,
        stock: Int,
        idLocal: Int
    ) {
        val values = ContentValues().apply {
            put(DatabaseContract.Productos.NOMBRE_PRODUCTO, nombre)
            put(DatabaseContract.Productos.PRECIO, precio)
            put(DatabaseContract.Productos.DISPONIBILIDAD, disponibilidad)
            put(DatabaseContract.Productos.TIPO, tipo)
            put(DatabaseContract.Productos.STOCK, stock)
            put(DatabaseContract.Productos.ID_LOCAL, idLocal)
        }
        db.insert(DatabaseContract.Productos.TABLE_NAME, null, values)
    }

    private fun contarRegistros(db: SQLiteDatabase, tableName: String): Int {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)
        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    private fun obtenerIdLocalPorNombre(db: SQLiteDatabase, nombreLocal: String): Int? {
        val cursor = db.query(
            DatabaseContract.Locales.TABLE_NAME,
            arrayOf(DatabaseContract.Locales.ID_LOCAL),
            "${DatabaseContract.Locales.NOMBRE_LOCAL} = ?",
            arrayOf(nombreLocal),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else null
        }
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
