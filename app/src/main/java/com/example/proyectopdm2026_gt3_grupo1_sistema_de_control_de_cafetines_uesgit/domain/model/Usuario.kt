package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model

data class Usuario(
    val idUsuario: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val carnet: String,
    val idRol: Int,
    val idUbicacion: Int? = null,
    val activo: Boolean = true
)
