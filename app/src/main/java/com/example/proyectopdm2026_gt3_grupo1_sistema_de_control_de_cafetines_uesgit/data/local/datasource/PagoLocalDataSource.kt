package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago

class PagoLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarPago(pago: Pago): Long {
        return databaseHelper.writableDatabase.insert(
            DatabaseContract.Pagos.TABLE_NAME,
            null,
            pago.toContentValues()
        )
    }

    fun obtenerPagosPorPedido(idPedido: Int): List<Pago> {
        val pagos = mutableListOf<Pago>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Pagos.TABLE_NAME,
            null,
            "${DatabaseContract.Pagos.ID_PEDIDO} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            "${DatabaseContract.Pagos.FECHA_PAGO} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                pagos.add(it.toPago())
            }
        }

        return pagos
    }

    private fun Pago.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.Pagos.ID_PEDIDO, idPedido)
            put(DatabaseContract.Pagos.METODO_PAGO, metodoPago)
            put(DatabaseContract.Pagos.MONTO, monto)
            put(DatabaseContract.Pagos.FECHA_PAGO, fechaPago)
            put(DatabaseContract.Pagos.REFERENCIA, referencia)
            put(DatabaseContract.Pagos.ESTADO_PAGO, estadoPago)
        }
    }

    private fun Cursor.toPago(): Pago {
        return Pago(
            idPago = getInt(getColumnIndexOrThrow(DatabaseContract.Pagos.ID_PAGO)),
            idPedido = getInt(getColumnIndexOrThrow(DatabaseContract.Pagos.ID_PEDIDO)),
            metodoPago = getString(getColumnIndexOrThrow(DatabaseContract.Pagos.METODO_PAGO)),
            monto = getDouble(getColumnIndexOrThrow(DatabaseContract.Pagos.MONTO)),
            fechaPago = getString(getColumnIndexOrThrow(DatabaseContract.Pagos.FECHA_PAGO)),
            referencia = getStringOrNull(DatabaseContract.Pagos.REFERENCIA),
            estadoPago = getString(getColumnIndexOrThrow(DatabaseContract.Pagos.ESTADO_PAGO))
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
