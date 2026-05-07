package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario
import java.security.MessageDigest
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Local
import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "cafetines.db", null, 13) {

    // CREAR BD

    override fun onCreate(db: SQLiteDatabase) {

        // LOCALES
        db.execSQL("""
    CREATE TABLE locales (
        id_local INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre_local TEXT NOT NULL UNIQUE,
        ubicacion TEXT NOT NULL,
        descripcion TEXT NOT NULL,
        imagen TEXT,
        entrega_campus INTEGER,
        estado TEXT NOT NULL,
        sincronizado INTEGER DEFAULT 0
    )
""")

        // ROLES
        db.execSQL("""
            CREATE TABLE roles (
                id_rol INTEGER PRIMARY KEY,
                nombre_rol TEXT NOT NULL,
                descripcion TEXT,
                estado TEXT NOT NULL
            )
        """)
        // USUARIOS (CACHE LOCAL)
        db.execSQL("""
            CREATE TABLE usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                carnet TEXT NOT NULL,
                id_rol INTEGER NOT NULL,
                sincronizado INTEGER DEFAULT 0,
                FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
            )
        """)
        // COLA DE SINCRONIZACIÓN
        db.execSQL("""
            CREATE TABLE sync_queue (
                id_sync INTEGER PRIMARY KEY AUTOINCREMENT,
                tabla TEXT NOT NULL,
                accion TEXT NOT NULL,
                datos TEXT NOT NULL,
                estado TEXT DEFAULT 'pendiente'
            )
        """)
        // ROLES INICIALES
        db.execSQL("""
            INSERT INTO roles (id_rol, nombre_rol, descripcion, estado) VALUES
            (1, 'Usuario', 'Usuario normal', 'Activo'),
            (2, 'Administrador', 'Acceso total', 'Activo'),
            (3, 'Encargado', 'Gestion de cafetines', 'Activo')
        """)
    }

    // =========================
    // ACTUALIZAR BD
    // =========================
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS sync_queue")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS roles")
        db.execSQL("DROP TABLE IF EXISTS locales")

        onCreate(db)
    }


    // REGISTRO LOCAL (OFFLINE)

    fun insertarUsuario(usuario: Usuario): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {

            put("nombre", usuario.nombre)
            put("email", usuario.email)
            put("password", hash(usuario.password ?: ""))
            put("carnet", usuario.carnet ?: "")
            put("id_rol", usuario.idRol)
            put("sincronizado", 0)
        }

        val result = db.insert("usuarios", null, values)

        if (result != -1L) {
            agregarAColaSync("usuarios", "INSERT", usuario)
        }

        db.close()

        return result != -1L
    }

    // LOGIN LOCAL (OFFLINE)

    fun login(email: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ?",
            arrayOf(email)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val passwordDB =
                cursor.getString(cursor.getColumnIndexOrThrow("password"))
            try {

                // PHP usa $2y$ y Android BCrypt usa $2a$
                val hashCorregido =
                    passwordDB.replace("$2y$", "$2a$")

                val coincide = BCrypt.checkpw(
                    password,
                    hashCorregido
                )
                if (coincide) {

                    usuario = Usuario(
                        idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        password = passwordDB,
                        carnet = cursor.getString(cursor.getColumnIndexOrThrow("carnet")),
                        idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"))
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
        cursor.close()
        db.close()
        return usuario
    }

    // OBTENER USUARIO POR ID

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


    // EXISTE USUARIO

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


    // AGREGAR A COLA SYNC

    private fun agregarAColaSync(
        tabla: String,
        accion: String,
        usuario: Usuario
    ) {

        val db = writableDatabase

        val datos = """
            {
                "nombre":"${usuario.nombre}",
                "email":"${usuario.email}",
                "password":"${hash(usuario.password ?: "")}",
                "carnet":"${usuario.carnet ?: ""}",
                "id_rol":${usuario.idRol}
            }
        """.trimIndent()

        val values = ContentValues().apply {

            put("tabla", tabla)
            put("accion", accion)
            put("datos", datos)
            put("estado", "pendiente")
        }

        db.insert("sync_queue", null, values)

        db.close()
    }


    // PENDIENTES SYNC

    fun obtenerPendientesSync(): List<String> {

        val lista = mutableListOf<String>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT datos FROM sync_queue WHERE estado = 'pendiente'",
            null
        )

        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }

        cursor.close()
        db.close()

        return lista
    }


    // MARCAR SYNC

    fun marcarSincronizado() {

        val db = writableDatabase

        db.execSQL(
            "UPDATE sync_queue SET estado = 'enviado'"
        )

        db.close()
    }


    // HASH SHA256

    private fun hash(password: String): String {

        val md = MessageDigest.getInstance("SHA-256")

        return md.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }


    // INSERTAR O ACTUALIZAR CACHE

    fun insertarLocal(local: Local): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {

            put("nombre_local", local.nombreLocal)
            put("ubicacion", local.ubicacion)
            put("descripcion", local.descripcion)
            put("imagen", local.imagen)

            put("entrega_campus", local.entregaCampus)

            put("estado", local.estado)
        }

        val result = db.insert("locales", null, values)

        db.close()

        return result != -1L
    }
    fun insertarOActualizarUsuario(usuario: Usuario) {

        val db = writableDatabase

        val values = ContentValues().apply {

            put("id_usuario", usuario.idUsuario)
            put("nombre", usuario.nombre)
            put("email", usuario.email)

            // HASH BCRYPT
            put("password", usuario.password)

            put("carnet", usuario.carnet)
            put("id_rol", usuario.idRol)
        }

        try {

            val cursor = db.rawQuery(
                "SELECT id_usuario FROM usuarios WHERE id_usuario = ?",
                arrayOf(usuario.idUsuario.toString())
            )

            val existe = cursor.moveToFirst()

            cursor.close()

            if (existe) {

                db.update(
                    "usuarios",
                    values,
                    "id_usuario = ?",
                    arrayOf(usuario.idUsuario.toString())
                )

            } else {

                db.insert("usuarios", null, values)
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        db.close()
    }


    // LIMPIAR CACHE

    fun eliminarUsuarios() {

        val db = writableDatabase

        db.execSQL("DELETE FROM usuarios")

        db.close()
    }

}
