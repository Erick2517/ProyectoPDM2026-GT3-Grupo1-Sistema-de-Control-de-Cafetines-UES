package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

class LocalLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarLocal(local: Local): Long {
        return databaseHelper.writableDatabase.insert(
            DatabaseContract.Locales.TABLE_NAME,
            null,
            local.toContentValues()
        )
    }

    fun actualizarLocal(local: Local): Int {
        return databaseHelper.writableDatabase.update(
            DatabaseContract.Locales.TABLE_NAME,
            local.toContentValues(),
            "${DatabaseContract.Locales.ID_LOCAL} = ?",
            arrayOf(local.idLocal.toString())
        )
    }

    fun obtenerLocalPorId(idLocal: Int): Local? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Locales.TABLE_NAME,
            null,
            "${DatabaseContract.Locales.ID_LOCAL} = ?",
            arrayOf(idLocal.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toLocal() else null
        }
    }

    fun obtenerLocales(): List<Local> {
        return consultarLocales(null, null)
    }

    fun existeNombreLocal(nombreLocal: String): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Locales.TABLE_NAME,
            arrayOf(DatabaseContract.Locales.ID_LOCAL),
            "LOWER(${DatabaseContract.Locales.NOMBRE_LOCAL}) = LOWER(?)",
            arrayOf(nombreLocal),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun existeNombreLocalEnOtroRegistro(nombreLocal: String, idLocal: Int): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Locales.TABLE_NAME,
            arrayOf(DatabaseContract.Locales.ID_LOCAL),
            "LOWER(${DatabaseContract.Locales.NOMBRE_LOCAL}) = LOWER(?) AND ${DatabaseContract.Locales.ID_LOCAL} != ?",
            arrayOf(nombreLocal, idLocal.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun obtenerLocalesActivos(): List<Local> {
        return consultarLocales(
            "${DatabaseContract.Locales.ESTADO} = ?",
            arrayOf(AppConstants.ESTADO_ACTIVO)
        )
    }

    private fun consultarLocales(selection: String?, selectionArgs: Array<String>?): List<Local> {
        val locales = mutableListOf<Local>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Locales.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            DatabaseContract.Locales.NOMBRE_LOCAL
        )

        cursor.use {
            while (it.moveToNext()) {
                locales.add(it.toLocal())
            }
        }

        return locales
    }

    private fun Local.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.Locales.NOMBRE_LOCAL, nombreLocal)
            put(DatabaseContract.Locales.UBICACION, ubicacion)
            put(DatabaseContract.Locales.DESCRIPCION, descripcion)
            put(DatabaseContract.Locales.ESTADO, estado)
        }
    }

    private fun Cursor.toLocal(): Local {
        return Local(
            idLocal = getInt(getColumnIndexOrThrow(DatabaseContract.Locales.ID_LOCAL)),
            nombreLocal = getString(getColumnIndexOrThrow(DatabaseContract.Locales.NOMBRE_LOCAL)),
            ubicacion = getString(getColumnIndexOrThrow(DatabaseContract.Locales.UBICACION)),
            descripcion = getStringOrNull(DatabaseContract.Locales.DESCRIPCION),
            estado = getString(getColumnIndexOrThrow(DatabaseContract.Locales.ESTADO))
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
