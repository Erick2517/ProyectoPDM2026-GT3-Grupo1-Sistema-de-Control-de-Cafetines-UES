package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class Local(
    val idLocal: Int = 0,
    val nombreLocal: String,
    val ubicacion: String,
    val descripcion: String? = null,
    val estado: String,
    val imagenUri: String? = null
)
