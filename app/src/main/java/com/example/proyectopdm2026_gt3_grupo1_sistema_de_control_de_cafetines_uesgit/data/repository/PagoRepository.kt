package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PagoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class PagoRepository(private val pagoLocalDataSource: PagoLocalDataSource) {
    fun registrarPago(pago: Pago): OperationResult<Long> {
        return try {
            val idPago = pagoLocalDataSource.insertarPago(pago)
            if (idPago == -1L) {
                OperationResult.Error("No se pudo registrar el pago.")
            } else {
                OperationResult.Success(idPago)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al registrar el pago.", exception)
        }
    }

    fun registrarPagoConfirmado(pago: Pago): OperationResult<Long> {
        return try {
            val pagoConfirmado = pago.copy(estadoPago = AppConstants.ESTADO_PAGO_CONFIRMADO)
            val idPago = pagoLocalDataSource.registrarPagoYActualizarPedido(
                pago = pagoConfirmado,
                nuevoEstadoPedido = AppConstants.ESTADO_PEDIDO_PAGADO
            )

            if (idPago == -1L) {
                OperationResult.Error("No se pudo registrar el pago y actualizar el pedido.")
            } else {
                OperationResult.Success(idPago)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al confirmar el pago.", exception)
        }
    }

    fun obtenerPagosPorPedido(idPedido: Int): OperationResult<List<Pago>> {
        return try {
            OperationResult.Success(pagoLocalDataSource.obtenerPagosPorPedido(idPedido))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar pagos del pedido.", exception)
        }
    }
    fun obtenerPagoPorId(idPago: Int): OperationResult<Pago> {
        return try {
            val pago = pagoLocalDataSource.obtenerPagoPorId(idPago)
            if (pago == null) {
                OperationResult.Error("No se encontró el pago.")
            } else {
                OperationResult.Success(pago)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el pago.", exception)
        }
    }
}
