package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class ProductoRepository(private val productoLocalDataSource: ProductoLocalDataSource) {
    fun registrarProducto(producto: Producto): OperationResult<Long> {
        return try {
            val idProducto = productoLocalDataSource.insertarProducto(producto)
            if (idProducto == -1L) {
                OperationResult.Error("No se pudo registrar el producto.")
            } else {
                OperationResult.Success(idProducto)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al registrar el producto.", exception)
        }
    }

    fun obtenerProductosPorLocal(idLocal: Int): OperationResult<List<Producto>> {
        return try {
            OperationResult.Success(productoLocalDataSource.obtenerProductosPorLocal(idLocal))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar productos.", exception)
        }
    }
}
