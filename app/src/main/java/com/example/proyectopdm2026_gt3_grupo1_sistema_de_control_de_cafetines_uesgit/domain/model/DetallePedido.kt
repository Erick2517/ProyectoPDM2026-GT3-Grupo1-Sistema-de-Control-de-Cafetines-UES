package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class DetallePedido(
    val idDetallePedido: Int = 0,
    val idPedido: Int,
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
