package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class CarritoItem(
    val producto: Producto,
    val cantidad: Int
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}
