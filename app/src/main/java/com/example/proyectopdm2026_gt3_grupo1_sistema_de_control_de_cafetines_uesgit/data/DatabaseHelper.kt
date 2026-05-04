package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "cafetines.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {

        // =========================
        // ROLES
        // =========================
        db.execSQL("""
            CREATE TABLE roles (
                id_rol INTEGER PRIMARY KEY,
                nombre_rol TEXT NOT NULL,
                descripcion TEXT,
                estado TEXT NOT NULL
            )
        """)

        // =========================
        // USUARIOS
        // =========================
        db.execSQL("""
            CREATE TABLE usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                carnet TEXT NOT NULL UNIQUE,
                id_rol INTEGER NOT NULL,
                FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
            )
        """)

        // =========================
        // UBICACIONES
        // =========================
        db.execSQL("""
            CREATE TABLE ubicaciones (
                id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_ubicacion TEXT NOT NULL,
                descripcion TEXT,
                estado TEXT NOT NULL
            )
        """)

        // =========================
        // LOCALES
        // =========================
        db.execSQL("""
            CREATE TABLE locales (
                id_local INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_local TEXT NOT NULL,
                ubicacion TEXT NOT NULL,
                estado TEXT NOT NULL
            )
        """)

        // =========================
        // PRODUCTOS
        // =========================
        db.execSQL("""
            CREATE TABLE productos (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_producto TEXT NOT NULL,
                precio REAL NOT NULL,
                disponibilidad TEXT NOT NULL,
                id_local INTEGER NOT NULL,
                FOREIGN KEY (id_local) REFERENCES locales(id_local)
            )
        """)

        // =========================
        // PEDIDOS
        // =========================
        db.execSQL("""
            CREATE TABLE pedidos (
                id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_pedido TEXT NOT NULL,
                tipo_pedido TEXT NOT NULL,
                estado_pedido TEXT NOT NULL,
                total REAL NOT NULL,
                id_usuario INTEGER NOT NULL,
                id_ubicacion INTEGER NOT NULL,
                FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
                FOREIGN KEY (id_ubicacion) REFERENCES ubicaciones(id_ubicacion)
            )
        """)

        // =========================
        // DETALLE PEDIDO
        // =========================
        db.execSQL("""
            CREATE TABLE detalle_pedido (
                id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_producto INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
                FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
            )
        """)

        // =========================
        // PAGOS
        // =========================
        db.execSQL("""
            CREATE TABLE pagos (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL UNIQUE,
                metodo_pago TEXT NOT NULL,
                monto REAL NOT NULL,
                fecha_pago TEXT NOT NULL,
                FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
            )
        """)

        // =========================
        // PEDIDOS ESPECIALES
        // =========================
        db.execSQL("""
            CREATE TABLE pedidos_especiales (
                id_pedido_especial INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL UNIQUE,
                descripcion_evento TEXT,
                monto_minimo REAL NOT NULL,
                monto_maximo REAL NOT NULL,
                FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
            )
        """)

        // =========================
        // DATOS INICIALES (LOGIN)
        // =========================
        db.execSQL("""
            INSERT INTO roles (id_rol, nombre_rol, descripcion, estado) VALUES
            (1, 'Usuario', 'Usuario normal', 'Activo'),
            (2, 'Administrador', 'Acceso total', 'Activo'),
            (3, 'Encargado', 'Gestion de cafetines', 'Activo')
        """)

        db.execSQL("""
            INSERT INTO usuarios (nombre, email, password, carnet, id_rol) VALUES
            ('Admin', 'admin@ues.edu.sv', '${hash("admin123")}', 'AD0001', 2),
            ('Juan Perez', 'juan@ues.edu.sv', '${hash("juan123")}', 'DP0001', 1),
            ('Maria Lopez', 'maria@ues.edu.sv', '${hash("maria123")}', 'DP0002', 3)
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pedidos_especiales")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS detalle_pedido")
        db.execSQL("DROP TABLE IF EXISTS pedidos")
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS locales")
        db.execSQL("DROP TABLE IF EXISTS ubicaciones")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS roles")
        onCreate(db)
    }

    // =========================
    // REGISTRO
    // =========================
    fun insertarUsuario(usuario: Usuario): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("email", usuario.email)
            put("password", hash(usuario.password))
            put("carnet", usuario.carnet)
            put("id_rol", usuario.idRol)
        }

        val result = db.insert("usuarios", null, values)

        db.close()

        return result != -1L
    }

    // =========================
    // LOGIN
    // =========================
    fun login(email: String, password: String): Usuario? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, hash(password))
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {

            usuario = Usuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                carnet = cursor.getString(cursor.getColumnIndexOrThrow("carnet")),
                idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"))
            )
        }

        cursor.close()
        db.close()

        return usuario
    }

    // =========================
    // HASH SEGURIDAD
    // =========================
    private fun hash(password: String): String {

        val md = MessageDigest.getInstance("SHA-256")

        return md.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
    // =========================
// VERIFICAR SI EXISTE USUARIO
// =========================
    fun existeUsuario(email: String): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT id_usuario FROM usuarios WHERE email = ?",
            arrayOf(email)
        )

        val existe = cursor.count > 0

        cursor.close()
        db.close()

        return existe
    }
    fun obtenerUsuarioPorId(id: Int): Usuario? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE id_usuario = ?",
            arrayOf(id.toString())
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {

            usuario = Usuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                carnet = cursor.getString(cursor.getColumnIndexOrThrow("carnet")),
                idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"))
            )
        }

        cursor.close()
        db.close()

        return usuario
    }
}
