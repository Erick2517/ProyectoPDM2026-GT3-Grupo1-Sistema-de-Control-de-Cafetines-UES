package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Usuario

class UsuarioLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarUsuario(usuario: Usuario): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.NOMBRE, usuario.nombre)
            put(DatabaseContract.Usuarios.EMAIL, usuario.email)
            put(DatabaseContract.Usuarios.PASSWORD, usuario.password)
            put(DatabaseContract.Usuarios.CARNET, usuario.carnet)
            put(DatabaseContract.Usuarios.ID_ROL, usuario.idRol)
            put(DatabaseContract.Usuarios.ID_UBICACION, usuario.idUbicacion)
            put(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO, usuario.idLocalAsignado)
            put(DatabaseContract.Usuarios.ACTIVO, if (usuario.activo) 1 else 0)
        }

        return databaseHelper.writableDatabase.insert(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            values
        )
    }

    fun actualizarUsuario(usuario: Usuario): Int {
        return databaseHelper.writableDatabase.update(
            DatabaseContract.Usuarios.TABLE_NAME,
            usuario.toContentValues(),
            "${DatabaseContract.Usuarios.ID_USUARIO} = ?",
            arrayOf(usuario.idUsuario.toString())
        )
    }

    fun obtenerUsuarios(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            DatabaseContract.Usuarios.NOMBRE
        )

        cursor.use {
            while (it.moveToNext()) {
                usuarios.add(it.toUsuario())
            }
        }

        return usuarios
    }

    fun actualizarRolUsuario(idUsuario: Int, idRol: Int): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.ID_ROL, idRol)
        }

        return databaseHelper.writableDatabase.update(
            DatabaseContract.Usuarios.TABLE_NAME,
            values,
            "${DatabaseContract.Usuarios.ID_USUARIO} = ?",
            arrayOf(idUsuario.toString())
        )
    }

    fun actualizarRolYLocalAsignado(idUsuario: Int, idRol: Int, idLocalAsignado: Int?): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Usuarios.ID_ROL, idRol)
            put(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO, idLocalAsignado)
        }

        return databaseHelper.writableDatabase.update(
            DatabaseContract.Usuarios.TABLE_NAME,
            values,
            "${DatabaseContract.Usuarios.ID_USUARIO} = ?",
            arrayOf(idUsuario.toString())
        )
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return obtenerUsuarioPorCampo(DatabaseContract.Usuarios.EMAIL, email)
    }

    fun obtenerUsuarioPorCarnet(carnet: String): Usuario? {
        return obtenerUsuarioPorCampo(DatabaseContract.Usuarios.CARNET, carnet)
    }

    fun obtenerUsuarioPorCredenciales(email: String, password: String): Usuario? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            "${DatabaseContract.Usuarios.EMAIL} = ? AND ${DatabaseContract.Usuarios.PASSWORD} = ? AND ${DatabaseContract.Usuarios.ACTIVO} = 1",
            arrayOf(email, password),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toUsuario() else null
        }
    }

    fun existeEmailOCarnet(email: String, carnet: String): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Usuarios.TABLE_NAME,
            arrayOf(DatabaseContract.Usuarios.ID_USUARIO),
            "${DatabaseContract.Usuarios.EMAIL} = ? OR ${DatabaseContract.Usuarios.CARNET} = ?",
            arrayOf(email, carnet),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun existeEmailOCarnetEnOtroUsuario(email: String, carnet: String, idUsuario: Int): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Usuarios.TABLE_NAME,
            arrayOf(DatabaseContract.Usuarios.ID_USUARIO),
            "(${DatabaseContract.Usuarios.EMAIL} = ? OR ${DatabaseContract.Usuarios.CARNET} = ?) AND ${DatabaseContract.Usuarios.ID_USUARIO} != ?",
            arrayOf(email, carnet, idUsuario.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun Usuario.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.Usuarios.NOMBRE, nombre)
            put(DatabaseContract.Usuarios.EMAIL, email)
            put(DatabaseContract.Usuarios.PASSWORD, password)
            put(DatabaseContract.Usuarios.CARNET, carnet)
            put(DatabaseContract.Usuarios.ID_ROL, idRol)
            put(DatabaseContract.Usuarios.ID_UBICACION, idUbicacion)
            put(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO, idLocalAsignado)
            put(DatabaseContract.Usuarios.ACTIVO, if (activo) 1 else 0)
        }
    }

    private fun obtenerUsuarioPorCampo(columnName: String, value: String): Usuario? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Usuarios.TABLE_NAME,
            null,
            "$columnName = ?",
            arrayOf(value),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toUsuario() else null
        }
    }

    private fun Cursor.toUsuario(): Usuario {
        return Usuario(
            idUsuario = getInt(getColumnIndexOrThrow(DatabaseContract.Usuarios.ID_USUARIO)),
            nombre = getString(getColumnIndexOrThrow(DatabaseContract.Usuarios.NOMBRE)),
            email = getString(getColumnIndexOrThrow(DatabaseContract.Usuarios.EMAIL)),
            password = getString(getColumnIndexOrThrow(DatabaseContract.Usuarios.PASSWORD)),
            carnet = getString(getColumnIndexOrThrow(DatabaseContract.Usuarios.CARNET)),
            idRol = getInt(getColumnIndexOrThrow(DatabaseContract.Usuarios.ID_ROL)),
            idUbicacion = getIntOrNull(DatabaseContract.Usuarios.ID_UBICACION),
            idLocalAsignado = getIntOrNull(DatabaseContract.Usuarios.ID_LOCAL_ASIGNADO),
            activo = getInt(getColumnIndexOrThrow(DatabaseContract.Usuarios.ACTIVO)) == 1
        )
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }
}
