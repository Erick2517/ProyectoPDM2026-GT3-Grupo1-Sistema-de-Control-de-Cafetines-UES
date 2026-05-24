package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.OpcionMenu
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

class OpcionMenuLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun obtenerOpcionesPorRol(idRol: Int): List<OpcionMenu> {
        val opciones = mutableListOf<OpcionMenu>()
        val db = databaseHelper.readableDatabase
        val query = """
            SELECT om.*
            FROM ${DatabaseContract.OpcionesMenu.TABLE_NAME} om
            INNER JOIN ${DatabaseContract.RolesOpcionesMenu.TABLE_NAME} rom
                ON om.${DatabaseContract.OpcionesMenu.ID_OPCION} = rom.${DatabaseContract.RolesOpcionesMenu.ID_OPCION}
            WHERE rom.${DatabaseContract.RolesOpcionesMenu.ID_ROL} = ?
                AND om.${DatabaseContract.OpcionesMenu.ESTADO} = ?
            ORDER BY om.${DatabaseContract.OpcionesMenu.NOMBRE_OPCION}
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idRol.toString(), AppConstants.ESTADO_ACTIVO))
        cursor.use {
            while (it.moveToNext()) {
                opciones.add(it.toOpcionMenu())
            }
        }

        return opciones
    }

    fun rolTieneOpcion(idRol: Int, nombreOpcion: String): Boolean {
        val db = databaseHelper.readableDatabase
        val query = """
            SELECT 1
            FROM ${DatabaseContract.OpcionesMenu.TABLE_NAME} om
            INNER JOIN ${DatabaseContract.RolesOpcionesMenu.TABLE_NAME} rom
                ON om.${DatabaseContract.OpcionesMenu.ID_OPCION} = rom.${DatabaseContract.RolesOpcionesMenu.ID_OPCION}
            WHERE rom.${DatabaseContract.RolesOpcionesMenu.ID_ROL} = ?
                AND om.${DatabaseContract.OpcionesMenu.NOMBRE_OPCION} = ?
                AND om.${DatabaseContract.OpcionesMenu.ESTADO} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            query,
            arrayOf(idRol.toString(), nombreOpcion, AppConstants.ESTADO_ACTIVO)
        )
        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun Cursor.toOpcionMenu(): OpcionMenu {
        return OpcionMenu(
            idOpcion = getInt(getColumnIndexOrThrow(DatabaseContract.OpcionesMenu.ID_OPCION)),
            nombreOpcion = getString(getColumnIndexOrThrow(DatabaseContract.OpcionesMenu.NOMBRE_OPCION)),
            descripcionOpcion = getStringOrNull(DatabaseContract.OpcionesMenu.DESCRIPCION_OPCION),
            estado = getString(getColumnIndexOrThrow(DatabaseContract.OpcionesMenu.ESTADO))
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
