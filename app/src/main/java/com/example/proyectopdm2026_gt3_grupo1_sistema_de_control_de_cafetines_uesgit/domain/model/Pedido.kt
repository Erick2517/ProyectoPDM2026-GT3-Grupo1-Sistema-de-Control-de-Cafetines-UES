package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class Pedido(
    val idPedido: Int = 0,
    val fechaPedido: String,
    val tipoPedido: String,
    val estadoPedido: String,
    val total: Double,
    val idUsuario: Int,
    val idUbicacion: Int? = null
)
