package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class ProductoRepository(private val productoLocalDataSource: ProductoLocalDataSource) {
    fun registrarProducto(producto: Producto): OperationResult<Long> {
        return try {
            if (productoLocalDataSource.existeProductoEnLocal(producto.nombreProducto, producto.idLocal)) {
                return OperationResult.Error("Ya existe un producto con ese nombre en el local seleccionado.")
            }

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

    fun actualizarProducto(producto: Producto): OperationResult<Boolean> {
        return try {
            if (producto.idProducto <= 0) {
                return OperationResult.Error("Debe seleccionar un producto válido.")
            }
            if (
                productoLocalDataSource.existeProductoEnLocalEnOtroRegistro(
                    producto.nombreProducto,
                    producto.idLocal,
                    producto.idProducto
                )
            ) {
                return OperationResult.Error("Ya existe otro producto con ese nombre en el local seleccionado.")
            }

            val filasActualizadas = productoLocalDataSource.actualizarProducto(producto)
            OperationResult.Success(filasActualizadas > 0)
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al actualizar el producto.", exception)
        }
    }

    fun obtenerProductos(): OperationResult<List<Producto>> {
        return try {
            OperationResult.Success(productoLocalDataSource.obtenerProductos())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar productos.", exception)
        }
    }

    fun obtenerProductosPorLocal(idLocal: Int): OperationResult<List<Producto>> {
        return try {
            OperationResult.Success(productoLocalDataSource.obtenerProductosPorLocal(idLocal))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar productos.", exception)
        }
    }

    fun obtenerProductoPorId(idProducto: Int): OperationResult<Producto> {
        return try {
            val producto = productoLocalDataSource.obtenerProductoPorId(idProducto)
            if (producto == null) {
                OperationResult.Error("No se encontró el producto.")
            } else {
                OperationResult.Success(producto)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el producto.", exception)
        }
    }

    fun cambiarDisponibilidadProducto(producto: Producto): OperationResult<Boolean> {
        val nuevaDisponibilidad = if (producto.disponibilidad == com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants.DISPONIBILIDAD_DISPONIBLE) {
            com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants.DISPONIBILIDAD_NO_DISPONIBLE
        } else {
            com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants.DISPONIBILIDAD_DISPONIBLE
        }

        return actualizarProducto(producto.copy(disponibilidad = nuevaDisponibilidad))
    }
}
