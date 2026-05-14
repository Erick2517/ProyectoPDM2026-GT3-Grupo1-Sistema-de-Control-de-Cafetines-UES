package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.PedidoValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class PedidoRepository(private val pedidoLocalDataSource: PedidoLocalDataSource) {
    fun crearPedido(pedido: Pedido, detalles: List<DetallePedido>): OperationResult<Long> {
        return try {
            val idPedido = pedidoLocalDataSource.insertarPedido(pedido, detalles)
            if (idPedido == -1L) {
                OperationResult.Error("No se pudo crear el pedido.")
            } else {
                OperationResult.Success(idPedido)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al crear el pedido.", exception)
        }
    }

    fun obtenerPedidosPorUsuario(idUsuario: Int): OperationResult<List<Pedido>> {
        return try {
            OperationResult.Success(pedidoLocalDataSource.obtenerPedidosPorUsuario(idUsuario))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar pedidos.", exception)
        }
    }

    fun obtenerPedidosOperativos(): OperationResult<List<Pedido>> {
        return try {
            OperationResult.Success(pedidoLocalDataSource.obtenerPedidosOperativos())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar pedidos para control.", exception)
        }
    }

    fun obtenerPedidoPorId(idPedido: Int): OperationResult<Pedido> {
        return try {
            val pedido = pedidoLocalDataSource.obtenerPedidoPorId(idPedido)
            if (pedido == null) {
                OperationResult.Error("No se encontró el pedido.")
            } else {
                OperationResult.Success(pedido)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el pedido.", exception)
        }
    }

    fun obtenerDetallesPorPedido(idPedido: Int): OperationResult<List<DetallePedido>> {
        return try {
            OperationResult.Success(pedidoLocalDataSource.obtenerDetallesPorPedido(idPedido))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el detalle del pedido.", exception)
        }
    }

    fun actualizarEstadoPedido(idPedido: Int, nuevoEstado: String): OperationResult<Boolean> {
        return try {
            val filasActualizadas = pedidoLocalDataSource.actualizarEstadoPedido(idPedido, nuevoEstado)
            OperationResult.Success(filasActualizadas > 0)
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al actualizar el estado del pedido.", exception)
        }
    }

    fun avanzarEstadoPedido(idPedido: Int, estadoActual: String, nuevoEstado: String): OperationResult<Boolean> {
        val errorValidacion = PedidoValidator.validarTransicionEstado(estadoActual, nuevoEstado)
        if (errorValidacion != null) {
            return OperationResult.Error(errorValidacion)
        }

        return actualizarEstadoPedido(idPedido, nuevoEstado)
    }
}
