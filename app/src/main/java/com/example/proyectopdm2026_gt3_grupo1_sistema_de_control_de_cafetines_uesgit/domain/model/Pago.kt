package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class Pago(
    val idPago: Int = 0,
    val idPedido: Int,
    val metodoPago: String,
    val monto: Double,
    val fechaPago: String,
    val referencia: String? = null,
    val estadoPago: String
)
