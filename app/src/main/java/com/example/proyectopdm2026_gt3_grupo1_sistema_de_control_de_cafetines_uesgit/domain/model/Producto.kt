package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class Producto(
    val idProducto: Int = 0,
    val nombreProducto: String,
    val precio: Double,
    val disponibilidad: String,
    val tipo: String,
    val stock: Int,
    val idLocal: Int,
    val imagenUri: String? = null
)
