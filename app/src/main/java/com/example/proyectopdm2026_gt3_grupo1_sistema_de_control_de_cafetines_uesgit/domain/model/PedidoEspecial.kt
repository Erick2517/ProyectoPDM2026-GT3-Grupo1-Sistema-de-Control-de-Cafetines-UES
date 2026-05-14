package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class PedidoEspecial(
    val idPedidoEspecial: Int = 0,
    val idPedido: Int,
    val descripcionEvento: String,
    val fechaEvento: String,
    val horaEvento: String? = null,
    val numeroPersonas: Int? = null,
    val montoMinimo: Double,
    val montoMaximo: Double,
    val anticipo: Double,
    val referenciaPago: String? = null
)
