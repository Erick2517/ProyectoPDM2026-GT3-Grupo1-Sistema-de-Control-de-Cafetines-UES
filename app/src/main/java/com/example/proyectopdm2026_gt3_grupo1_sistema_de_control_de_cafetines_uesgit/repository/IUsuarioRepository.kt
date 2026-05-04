package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario

interface IUsuarioRepository {
    fun registrar(usuario: Usuario): Boolean
}