package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

object PedidoValidator {
    private val estadosPermitidos = setOf(
        AppConstants.ESTADO_PEDIDO_PENDIENTE,
        AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO,
        AppConstants.ESTADO_PEDIDO_PAGADO,
        AppConstants.ESTADO_PEDIDO_EN_PREPARACION,
        AppConstants.ESTADO_PEDIDO_LISTO,
        AppConstants.ESTADO_PEDIDO_ENTREGADO,
        AppConstants.ESTADO_PEDIDO_CANCELADO
    )

    fun validarPedido(pedido: Pedido, detalles: List<DetallePedido>): String? {
        if (pedido.idUsuario <= 0) return "Debe existir un usuario asociado al pedido."
        if (pedido.fechaPedido.isBlank()) return "La fecha del pedido es obligatoria."
        if (pedido.tipoPedido.isBlank()) return "Debe seleccionar el tipo de pedido."
        if (!estadosPermitidos.contains(pedido.estadoPedido)) return "El estado del pedido no es válido."
        if (detalles.isEmpty()) return "No se puede crear un pedido sin productos."
        if (pedido.total <= 0.0) return "El total del pedido debe ser mayor a cero."

        detalles.forEach { detalle ->
            if (detalle.idProducto <= 0) return "Cada detalle debe estar asociado a un producto válido."
            if (detalle.cantidad <= 0) return "La cantidad debe ser mayor a cero."
            if (detalle.precioUnitario <= 0.0) return "El precio unitario debe ser mayor a cero."
            if (detalle.subtotal <= 0.0) return "El subtotal debe ser mayor a cero."
        }

        return null
    }

    fun calcularTotal(detalles: List<DetallePedido>): Double {
        return detalles.sumOf { it.subtotal }
    }
}
