package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido

class PedidoLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarPedido(pedido: Pedido, detalles: List<DetallePedido>): Long {
        val db = databaseHelper.writableDatabase
        db.beginTransaction()

        return try {
            val idPedido = db.insert(
                DatabaseContract.Pedidos.TABLE_NAME,
                null,
                pedido.toContentValues()
            )

            if (idPedido == -1L) {
                return -1L
            }

            detalles.forEach { detalle ->
                val idDetalle = db.insert(
                    DatabaseContract.DetallePedido.TABLE_NAME,
                    null,
                    detalle.copy(idPedido = idPedido.toInt()).toContentValues()
                )

                if (idDetalle == -1L) {
                    return -1L
                }
            }

            db.setTransactionSuccessful()
            idPedido
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerPedidosPorUsuario(idUsuario: Int): List<Pedido> {
        val pedidos = mutableListOf<Pedido>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Pedidos.TABLE_NAME,
            null,
            "${DatabaseContract.Pedidos.ID_USUARIO} = ?",
            arrayOf(idUsuario.toString()),
            null,
            null,
            "${DatabaseContract.Pedidos.FECHA_PEDIDO} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                pedidos.add(it.toPedido())
            }
        }

        return pedidos
    }

    fun obtenerPedidoPorId(idPedido: Int): Pedido? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Pedidos.TABLE_NAME,
            null,
            "${DatabaseContract.Pedidos.ID_PEDIDO} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toPedido() else null
        }
    }

    fun obtenerDetallesPorPedido(idPedido: Int): List<DetallePedido> {
        val detalles = mutableListOf<DetallePedido>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.DetallePedido.TABLE_NAME,
            null,
            "${DatabaseContract.DetallePedido.ID_PEDIDO} = ?",
            arrayOf(idPedido.toString()),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                detalles.add(it.toDetallePedido())
            }
        }

        return detalles
    }

    fun actualizarEstadoPedido(idPedido: Int, nuevoEstado: String): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Pedidos.ESTADO_PEDIDO, nuevoEstado)
        }

        return databaseHelper.writableDatabase.update(
            DatabaseContract.Pedidos.TABLE_NAME,
            values,
            "${DatabaseContract.Pedidos.ID_PEDIDO} = ?",
            arrayOf(idPedido.toString())
        )
    }

    private fun Pedido.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.Pedidos.FECHA_PEDIDO, fechaPedido)
            put(DatabaseContract.Pedidos.TIPO_PEDIDO, tipoPedido)
            put(DatabaseContract.Pedidos.ESTADO_PEDIDO, estadoPedido)
            put(DatabaseContract.Pedidos.TOTAL, total)
            put(DatabaseContract.Pedidos.ID_USUARIO, idUsuario)
            put(DatabaseContract.Pedidos.ID_UBICACION, idUbicacion)
        }
    }

    private fun DetallePedido.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.DetallePedido.ID_PEDIDO, idPedido)
            put(DatabaseContract.DetallePedido.ID_PRODUCTO, idProducto)
            put(DatabaseContract.DetallePedido.CANTIDAD, cantidad)
            put(DatabaseContract.DetallePedido.PRECIO_UNITARIO, precioUnitario)
            put(DatabaseContract.DetallePedido.SUBTOTAL, subtotal)
        }
    }

    private fun Cursor.toPedido(): Pedido {
        return Pedido(
            idPedido = getInt(getColumnIndexOrThrow(DatabaseContract.Pedidos.ID_PEDIDO)),
            fechaPedido = getString(getColumnIndexOrThrow(DatabaseContract.Pedidos.FECHA_PEDIDO)),
            tipoPedido = getString(getColumnIndexOrThrow(DatabaseContract.Pedidos.TIPO_PEDIDO)),
            estadoPedido = getString(getColumnIndexOrThrow(DatabaseContract.Pedidos.ESTADO_PEDIDO)),
            total = getDouble(getColumnIndexOrThrow(DatabaseContract.Pedidos.TOTAL)),
            idUsuario = getInt(getColumnIndexOrThrow(DatabaseContract.Pedidos.ID_USUARIO)),
            idUbicacion = getIntOrNull(DatabaseContract.Pedidos.ID_UBICACION)
        )
    }

    private fun Cursor.toDetallePedido(): DetallePedido {
        return DetallePedido(
            idDetallePedido = getInt(getColumnIndexOrThrow(DatabaseContract.DetallePedido.ID_DETALLE_PEDIDO)),
            idPedido = getInt(getColumnIndexOrThrow(DatabaseContract.DetallePedido.ID_PEDIDO)),
            idProducto = getInt(getColumnIndexOrThrow(DatabaseContract.DetallePedido.ID_PRODUCTO)),
            cantidad = getInt(getColumnIndexOrThrow(DatabaseContract.DetallePedido.CANTIDAD)),
            precioUnitario = getDouble(getColumnIndexOrThrow(DatabaseContract.DetallePedido.PRECIO_UNITARIO)),
            subtotal = getDouble(getColumnIndexOrThrow(DatabaseContract.DetallePedido.SUBTOTAL))
        )
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }
}
