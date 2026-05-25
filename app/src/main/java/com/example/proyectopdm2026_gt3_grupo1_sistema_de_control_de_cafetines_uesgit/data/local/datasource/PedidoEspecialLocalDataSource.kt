package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.PedidoEspecial

class PedidoEspecialLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarPedidoEspecial(pedidoEspecial: PedidoEspecial): Long {
        return databaseHelper.writableDatabase.insert(
            DatabaseContract.PedidosEspeciales.TABLE_NAME,
            null,
            pedidoEspecial.toContentValues()
        )
    }

    fun obtenerPedidosEspeciales(): List<PedidoEspecial> {
        val pedidosEspeciales = mutableListOf<PedidoEspecial>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.PedidosEspeciales.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.PedidosEspeciales.FECHA_EVENTO} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                pedidosEspeciales.add(it.toPedidoEspecial())
            }
        }

        return pedidosEspeciales
    }
    fun obtenerPedidoEspecialPorPorIdPedido(idPedido: Int): PedidoEspecial? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.PedidosEspeciales.TABLE_NAME,
            null,
            "${DatabaseContract.PedidosEspeciales.ID_PEDIDO} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toPedidoEspecial() else null
        }
    }
    private fun PedidoEspecial.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.PedidosEspeciales.ID_PEDIDO, idPedido)
            put(DatabaseContract.PedidosEspeciales.DESCRIPCION_EVENTO, descripcionEvento)
            put(DatabaseContract.PedidosEspeciales.FECHA_EVENTO, fechaEvento)
            put(DatabaseContract.PedidosEspeciales.HORA_EVENTO, horaEvento)
            put(DatabaseContract.PedidosEspeciales.NUMERO_PERSONAS, numeroPersonas)
            put(DatabaseContract.PedidosEspeciales.MONTO_MINIMO, montoMinimo)
            put(DatabaseContract.PedidosEspeciales.MONTO_MAXIMO, montoMaximo)
            put(DatabaseContract.PedidosEspeciales.ANTICIPO, anticipo)
            put(DatabaseContract.PedidosEspeciales.REFERENCIA_PAGO, referenciaPago)
        }
    }

    private fun Cursor.toPedidoEspecial(): PedidoEspecial {
        return PedidoEspecial(
            idPedidoEspecial = getInt(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.ID_PEDIDO_ESPECIAL)),
            idPedido = getInt(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.ID_PEDIDO)),
            descripcionEvento = getString(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.DESCRIPCION_EVENTO)),
            fechaEvento = getString(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.FECHA_EVENTO)),
            horaEvento = getStringOrNull(DatabaseContract.PedidosEspeciales.HORA_EVENTO),
            numeroPersonas = getIntOrNull(DatabaseContract.PedidosEspeciales.NUMERO_PERSONAS),
            montoMinimo = getDouble(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.MONTO_MINIMO)),
            montoMaximo = getDouble(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.MONTO_MAXIMO)),
            anticipo = getDouble(getColumnIndexOrThrow(DatabaseContract.PedidosEspeciales.ANTICIPO)),
            referenciaPago = getStringOrNull(DatabaseContract.PedidosEspeciales.REFERENCIA_PAGO)
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }
}
