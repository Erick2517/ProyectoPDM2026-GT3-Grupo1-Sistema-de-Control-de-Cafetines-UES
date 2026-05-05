package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PagoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
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
}
