package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model

data class Local(

    var idLocal: Int = 0,

    var nombreLocal: String,

    var ubicacion: String,

    var descripcion: String,

    var imagen: String,

    var entregaCampus: Int,

    var estado: String
)