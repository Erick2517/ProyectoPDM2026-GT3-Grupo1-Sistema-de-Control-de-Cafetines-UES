package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoEspecialLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.PedidoEspecial
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class PedidoEspecialRepository(
    private val pedidoEspecialLocalDataSource: PedidoEspecialLocalDataSource
) {
    fun registrarPedidoEspecial(pedidoEspecial: PedidoEspecial): OperationResult<Long> {
        return try {
            val idPedidoEspecial = pedidoEspecialLocalDataSource.insertarPedidoEspecial(pedidoEspecial)
            if (idPedidoEspecial == -1L) {
                OperationResult.Error("No se pudo registrar el pedido especial.")
            } else {
                OperationResult.Success(idPedidoEspecial)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al registrar el pedido especial.", exception)
        }
    }
    fun obtenerPedidoEspecialPorPorIdPedido(idPedido: Int): OperationResult<PedidoEspecial> {
        return try {
            val pedidoEspecial = pedidoEspecialLocalDataSource.obtenerPedidoEspecialPorPorIdPedido(idPedido)
            if (pedidoEspecial == null) {
                OperationResult.Error("No se encontró el pedido.")
            } else {
                OperationResult.Success(pedidoEspecial)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el pedido.", exception)
        }
    }
}
