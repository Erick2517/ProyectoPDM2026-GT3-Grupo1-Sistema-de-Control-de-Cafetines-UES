package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.PasswordHasher

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_ROLES_TABLE)
        db.execSQL(CREATE_UBICACIONES_TABLE)
        db.execSQL(CREATE_LOCALES_TABLE)
        db.execSQL(CREATE_USUARIOS_TABLE)
        db.execSQL(CREATE_PRODUCTOS_TABLE)
        db.execSQL(CREATE_PEDIDOS_TABLE)
        db.execSQL(CREATE_DETALLE_PEDIDO_TABLE)
        db.execSQL(CREATE_PAGOS_TABLE)
        db.execSQL(CREATE_PEDIDOS_ESPECIALES_TABLE)
        db.execSQL(CREATE_OPCIONES_MENU_TABLE)
        db.execSQL(CREATE_ROLES_OPCIONES_MENU_TABLE)
        insertarRolesBase(db)
        insertarUbicacionesBase(db)
        insertarCatalogoBase(db)
        insertarUsuariosBase(db)
        insertarOpcionesMenuBase(db)
        insertarPermisosMenuBase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            insertarUbicacionesBase(db)
        }

        if (oldVersion < 3) {
            insertarCatalogoBase(db)
        }

        if (oldVersion < 4) {
            insertarUsuariosBase(db)
        }

        if (oldVersion < 5) {
            db.execSQL(CREATE_OPCIONES_MENU_TABLE)
            db.execSQL(CREATE_ROLES_OPCIONES_MENU_TABLE)
            insertarOpcionesMenuBase(db)
            insertarPermisosMenuBase(db)
        }

        if (oldVersion < 6) {
            db.execSQL(
                "ALTER TABLE ${DatabaseContract.Usuarios.TABLE_NAME} " +
                    "ADD COLUMN ${DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO} INTEGER " +
                    "REFERENCES ${DatabaseContract.Locales.TABLE_NAME}(${DatabaseContract.Locales.ID_LOCAL})"
            )
            asignarLocalBaseAEncargado(db)
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

    private fun insertarUsuariosBase(db: SQLiteDatabase) {
        val idRolAdmin = obtenerIdRolPorNombre(db, "Administrador")
        val idRolEncargado = obtenerIdRolPorNombre(db, "Encargado")
        val idUbicacion = obtenerIdUbicacionPorNombre(db, "Campus Central")
        val idLocalCentral = obtenerIdLocalPorNombre(db, "Cafetín Central")

        if (idRolAdmin != null) {
            insertarUsuarioBase(
                db = db,
                nombre = "Administrador Sistema",
                email = "admin@ues.edu.sv",
                password = "Admin123",
                carnet = "AD00001",
                idRol = idRolAdmin,
                idUbicacion = idUbicacion,
                idLocalAsignado = null
            )
        }

        if (idRolEncargado != null) {
            insertarUsuarioBase(
                db = db,
                nombre = "Encargado Cafetín",
                email = "encargado@ues.edu.sv",
                password = "Encargado123",
                carnet = "EN00001",
                idRol = idRolEncargado,
                idUbicacion = idUbicacion,
                idLocalAsignado = idLocalCentral
            )
        }
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

    private fun insertarOpcionesMenuBase(db: SQLiteDatabase) {
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_VER_LOCALES,
            "Permite consultar cafetines activos y sus productos."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_MIS_PEDIDOS,
            "Permite consultar historial y detalle de pedidos propios."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_PEDIDO_ESPECIAL,
            "Permite solicitar pedidos especiales para eventos."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_GESTIONAR_LOCALES,
            "Permite administrar locales o cafetines."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_GESTIONAR_PRODUCTOS,
            "Permite administrar productos, precios, stock y disponibilidad."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_GESTIONAR_USUARIOS,
            "Permite administrar usuarios y roles."
        )
        insertarOpcionMenuBase(
            db,
            AppConstants.OPCION_CONTROL_PEDIDOS,
            "Permite consultar y actualizar estados de pedidos."
        )
    }

    private fun insertarPermisosMenuBase(db: SQLiteDatabase) {
        asignarOpcionesRol(
            db,
            AppConstants.ROL_USUARIO,
            listOf(
                AppConstants.OPCION_VER_LOCALES,
                AppConstants.OPCION_MIS_PEDIDOS,
                AppConstants.OPCION_PEDIDO_ESPECIAL
            )
        )
        asignarOpcionesRol(
            db,
            AppConstants.ROL_ADMINISTRADOR,
            listOf(
                AppConstants.OPCION_GESTIONAR_LOCALES,
                AppConstants.OPCION_GESTIONAR_PRODUCTOS,
                AppConstants.OPCION_GESTIONAR_USUARIOS,
                AppConstants.OPCION_CONTROL_PEDIDOS
            )
        )
        asignarOpcionesRol(
            db,
            AppConstants.ROL_ENCARGADO,
            listOf(
                AppConstants.OPCION_GESTIONAR_PRODUCTOS,
                AppConstants.OPCION_CONTROL_PEDIDOS
            )
        )
    }

    private fun insertarOpcionMenuBase(db: SQLiteDatabase, nombre: String, descripcion: String) {
        val values = ContentValues().apply {
            put(DatabaseContract.OpcionesMenu.NOMBRE_OPCION, nombre)
            put(DatabaseContract.OpcionesMenu.DESCRIPCION_OPCION, descripcion)
            put(DatabaseContract.OpcionesMenu.ESTADO, AppConstants.ESTADO_ACTIVO)
        }
        db.insertWithOnConflict(
            DatabaseContract.OpcionesMenu.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    private fun asignarOpcionesRol(db: SQLiteDatabase, nombreRol: String, opciones: List<String>) {
        val idRol = obtenerIdRolPorNombre(db, nombreRol) ?: return
        opciones.forEach { nombreOpcion ->
            val idOpcion = obtenerIdOpcionPorNombre(db, nombreOpcion) ?: return@forEach
            val values = ContentValues().apply {
                put(DatabaseContract.RolesOpcionesMenu.ID_ROL, idRol)
                put(DatabaseContract.RolesOpcionesMenu.ID_OPCION, idOpcion)
            }
            db.insertWithOnConflict(
                DatabaseContract.RolesOpcionesMenu.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )
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

    private fun insertarUsuarioBase(
        db: SQLiteDatabase,
        nombre: String,
        email: String,
        password: String,
        carnet: String,
        idRol: Int,
        idUbicacion: Int?,
        idLocalAsignado: Int?
    ) {
        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.NOMBRE, nombre)
            put(DatabaseContract.Usuarios.EMAIL, email)
            put(DatabaseContract.Usuarios.PASSWORD, PasswordHasher.hash(password))
            put(DatabaseContract.Usuarios.CARNET, carnet)
            put(DatabaseContract.Usuarios.ID_ROL, idRol)
            put(DatabaseContract.Usuarios.ID_UBICACION, idUbicacion)
            put(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO, idLocalAsignado)
            put(DatabaseContract.Usuarios.ACTIVO, 1)
        }
        db.insertWithOnConflict(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    private fun asignarLocalBaseAEncargado(db: SQLiteDatabase) {
        val idRolEncargado = obtenerIdRolPorNombre(db, AppConstants.ROL_ENCARGADO) ?: return
        val idLocalCentral = obtenerIdLocalPorNombre(db, "Cafetín Central") ?: return
        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO, idLocalCentral)
        }
        db.update(
            DatabaseContract.Usuarios.TABLE_NAME,
            values,
            "${DatabaseContract.Usuarios.ID_ROL} = ? AND ${DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO} IS NULL",
            arrayOf(idRolEncargado.toString())
        )
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

    private fun obtenerIdRolPorNombre(db: SQLiteDatabase, nombreRol: String): Int? {
        val cursor = db.query(
            DatabaseContract.Roles.TABLE_NAME,
            arrayOf(DatabaseContract.Roles.ID_ROL),
            "${DatabaseContract.Roles.NOMBRE_ROL} = ?",
            arrayOf(nombreRol),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else null
        }
    }

    private fun obtenerIdUbicacionPorNombre(db: SQLiteDatabase, nombreUbicacion: String): Int? {
        val cursor = db.query(
            DatabaseContract.Ubicaciones.TABLE_NAME,
            arrayOf(DatabaseContract.Ubicaciones.ID_UBICACION),
            "${DatabaseContract.Ubicaciones.NOMBRE_UBICACION} = ?",
            arrayOf(nombreUbicacion),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else null
        }
    }

    private fun obtenerIdOpcionPorNombre(db: SQLiteDatabase, nombreOpcion: String): Int? {
        val cursor = db.query(
            DatabaseContract.OpcionesMenu.TABLE_NAME,
            arrayOf(DatabaseContract.OpcionesMenu.ID_OPCION),
            "${DatabaseContract.OpcionesMenu.NOMBRE_OPCION} = ?",
            arrayOf(nombreOpcion),
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
                id_local_asignado INTEGER,
                activo INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
                FOREIGN KEY (id_ubicacion) REFERENCES Ubicaciones(id_ubicacion),
                FOREIGN KEY (id_local_asignado) REFERENCES Locales(id_local)
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

        const val CREATE_OPCIONES_MENU_TABLE = """
            CREATE TABLE IF NOT EXISTS OpcionesMenu (
                id_opcion INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_opcion TEXT NOT NULL UNIQUE,
                descripcion_opcion TEXT,
                estado TEXT NOT NULL
            )
        """

        const val CREATE_ROLES_OPCIONES_MENU_TABLE = """
            CREATE TABLE IF NOT EXISTS Roles_OpcionesMenu (
                id_rol_opcion INTEGER PRIMARY KEY AUTOINCREMENT,
                id_rol INTEGER NOT NULL,
                id_opcion INTEGER NOT NULL,
                UNIQUE (id_rol, id_opcion),
                FOREIGN KEY (id_rol) REFERENCES Roles(id_rol),
                FOREIGN KEY (id_opcion) REFERENCES OpcionesMenu(id_opcion)
            )
        """
    }
}
