package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Rol

class RolLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun obtenerRoles(): List<Rol> {
        val roles = mutableListOf<Rol>()
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.Roles.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            DatabaseContract.Roles.NOMBRE_ROL
        )

        cursor.use {
            while (it.moveToNext()) {
                roles.add(it.toRol())
            }
        }

        return roles
    }

    fun obtenerRolPorNombre(nombreRol: String): Rol? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.Roles.TABLE_NAME,
            null,
            "${DatabaseContract.Roles.NOMBRE_ROL} = ?",
            arrayOf(nombreRol),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toRol() else null
        }
    }

    fun obtenerRolPorId(idRol: Int): Rol? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.Roles.TABLE_NAME,
            null,
            "${DatabaseContract.Roles.ID_ROL} = ?",
            arrayOf(idRol.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toRol() else null
        }
    }

    private fun Cursor.toRol(): Rol {
        return Rol(
            idRol = getInt(getColumnIndexOrThrow(DatabaseContract.Roles.ID_ROL)),
            nombreRol = getString(getColumnIndexOrThrow(DatabaseContract.Roles.NOMBRE_ROL))
        )
    }
}
