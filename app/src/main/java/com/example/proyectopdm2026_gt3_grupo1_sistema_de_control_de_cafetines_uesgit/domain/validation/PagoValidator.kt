package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

object PagoValidator {
    private val metodosPermitidos = setOf(
        AppConstants.METODO_PAGO_EFECTIVO,
        AppConstants.METODO_PAGO_TARJETA,
        AppConstants.METODO_PAGO_BITCOIN
    )

    fun validarPago(idPedido: Int, metodoPago: String, monto: Double?): String? {
        if (idPedido <= 0) return "Debe existir un pedido asociado al pago."
        if (metodoPago.isBlank()) return "Debe seleccionar un método de pago."
        if (!metodosPermitidos.contains(metodoPago)) return "El método de pago no es válido."
        if (monto == null || monto <= 0.0) return "El monto del pago debe ser mayor a cero."

        return null
    }
}
