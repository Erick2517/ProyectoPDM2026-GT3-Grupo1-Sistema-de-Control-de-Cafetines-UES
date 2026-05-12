package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model

data class Carrito(

    val idProducto: Int,

    val nombreProducto: String,

    val precio: Double,

    var cantidad: Int,

    val subtotal: Double,

    val imagen: String,

    val idLocal: Int
)