package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource

import android.content.ContentValues
import android.database.Cursor
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.DatabaseContract
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

class ProductoLocalDataSource(private val databaseHelper: AppDatabaseHelper) {
    fun insertarProducto(producto: Producto): Long {
        return databaseHelper.writableDatabase.insert(
            DatabaseContract.Productos.TABLE_NAME,
            null,
            producto.toContentValues()
        )
    }

    fun actualizarProducto(producto: Producto): Int {
        return databaseHelper.writableDatabase.update(
            DatabaseContract.Productos.TABLE_NAME,
            producto.toContentValues(),
            "${DatabaseContract.Productos.ID_PRODUCTO} = ?",
            arrayOf(producto.idProducto.toString())
        )
    }

    fun obtenerProductosPorLocal(idLocal: Int): List<Producto> {
        return consultarProductos(
            "${DatabaseContract.Productos.ID_LOCAL} = ?",
            arrayOf(idLocal.toString())
        )
    }

    fun obtenerProductos(): List<Producto> {
        return consultarProductos(null, null)
    }

    fun obtenerProductoPorId(idProducto: Int): Producto? {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Productos.TABLE_NAME,
            null,
            "${DatabaseContract.Productos.ID_PRODUCTO} = ?",
            arrayOf(idProducto.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) it.toProducto() else null
        }
    }

    fun obtenerProductosDisponiblesPorLocal(idLocal: Int): List<Producto> {
        return consultarProductos(
            "${DatabaseContract.Productos.ID_LOCAL} = ? AND ${DatabaseContract.Productos.DISPONIBILIDAD} = ? AND ${DatabaseContract.Productos.STOCK} > 0",
            arrayOf(idLocal.toString(), AppConstants.DISPONIBILIDAD_DISPONIBLE)
        )
    }

    fun existeProductoEnLocal(nombreProducto: String, idLocal: Int): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Productos.TABLE_NAME,
            arrayOf(DatabaseContract.Productos.ID_PRODUCTO),
            "LOWER(${DatabaseContract.Productos.NOMBRE_PRODUCTO}) = LOWER(?) AND ${DatabaseContract.Productos.ID_LOCAL} = ?",
            arrayOf(nombreProducto, idLocal.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    fun existeProductoEnLocalEnOtroRegistro(nombreProducto: String, idLocal: Int, idProducto: Int): Boolean {
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Productos.TABLE_NAME,
            arrayOf(DatabaseContract.Productos.ID_PRODUCTO),
            "LOWER(${DatabaseContract.Productos.NOMBRE_PRODUCTO}) = LOWER(?) AND ${DatabaseContract.Productos.ID_LOCAL} = ? AND ${DatabaseContract.Productos.ID_PRODUCTO} != ?",
            arrayOf(nombreProducto, idLocal.toString(), idProducto.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun consultarProductos(selection: String?, selectionArgs: Array<String>?): List<Producto> {
        val productos = mutableListOf<Producto>()
        val cursor = databaseHelper.readableDatabase.query(
            DatabaseContract.Productos.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            DatabaseContract.Productos.NOMBRE_PRODUCTO
        )

        cursor.use {
            while (it.moveToNext()) {
                productos.add(it.toProducto())
            }
        }

        return productos
    }

    private fun Producto.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(DatabaseContract.Productos.NOMBRE_PRODUCTO, nombreProducto)
            put(DatabaseContract.Productos.PRECIO, precio)
            put(DatabaseContract.Productos.DISPONIBILIDAD, disponibilidad)
            put(DatabaseContract.Productos.TIPO, tipo)
            put(DatabaseContract.Productos.STOCK, stock)
            put(DatabaseContract.Productos.ID_LOCAL, idLocal)
            put(DatabaseContract.Productos.IMAGEN_URI, imagenUri)
        }
    }

    private fun Cursor.toProducto(): Producto {
        return Producto(
            idProducto = getInt(getColumnIndexOrThrow(DatabaseContract.Productos.ID_PRODUCTO)),
            nombreProducto = getString(getColumnIndexOrThrow(DatabaseContract.Productos.NOMBRE_PRODUCTO)),
            precio = getDouble(getColumnIndexOrThrow(DatabaseContract.Productos.PRECIO)),
            disponibilidad = getString(getColumnIndexOrThrow(DatabaseContract.Productos.DISPONIBILIDAD)),
            tipo = getString(getColumnIndexOrThrow(DatabaseContract.Productos.TIPO)),
            stock = getInt(getColumnIndexOrThrow(DatabaseContract.Productos.STOCK)),
            idLocal = getInt(getColumnIndexOrThrow(DatabaseContract.Productos.ID_LOCAL)),
            imagenUri = getStringOrNull(DatabaseContract.Productos.IMAGEN_URI)
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val columnIndex = getColumnIndexOrThrow(columnName)
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
