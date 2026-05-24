package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class OpcionMenu(
    val idOpcion: Int = 0,
    val nombreOpcion: String,
    val descripcionOpcion: String? = null,
    val estado: String
)
