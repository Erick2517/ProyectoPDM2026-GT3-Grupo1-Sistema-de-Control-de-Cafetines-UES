package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Ubicacion

class UbicacionLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarUbicacion(ubicacion: Ubicacion): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.Ubicaciones.NOMBRE_UBICACION, ubicacion.nombreUbicacion)
            put(DatabaseContract.Ubicaciones.DESCRIPCION, ubicacion.descripcion)
        }

        return databaseHelper.writableDatabase.insert(
            DatabaseContract.Ubicaciones.TABLE_NAME,
            null,
            values
        )
    }

    fun obtenerUbicaciones(): List<Ubicacion> {
        val ubicaciones = mutableListOf<Ubicacion>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Ubicaciones.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            DatabaseContract.Ubicaciones.NOMBRE_UBICACION
        )

        cursor.use {
            while (it.moveToNext()) {
                ubicaciones.add(it.toUbicacion())
            }
        }

        return ubicaciones
    }

    fun obtenerUbicacionPorNombre(nombreUbicacion: String): Ubicacion? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Ubicaciones.TABLE_NAME,
            null,
            "${DatabaseContract.Ubicaciones.NOMBRE_UBICACION} = ?",
            arrayOf(nombreUbicacion),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toUbicacion() else null
        }
    }

    private fun Cursor.toUbicacion(): Ubicacion {
        return Ubicacion(
            idUbicacion = getInt(getColumnIndexOrThrow(DatabaseContract.Ubicaciones.ID_UBICACION)),
            nombreUbicacion = getString(getColumnIndexOrThrow(DatabaseContract.Ubicaciones.NOMBRE_UBICACION)),
            descripcion = getStringOrNull(DatabaseContract.Ubicaciones.DESCRIPCION)
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
