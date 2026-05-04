package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.repository

import android.content.Context
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario

class UsuarioRepositorySQLite(context: Context) : IUsuarioRepository {

    private val dbHelper = DatabaseHelper(context)

    override fun registrar(usuario: Usuario): Boolean {

        if (dbHelper.existeUsuario(usuario.email)) {
            return false
        }

        return dbHelper.insertarUsuario(usuario)
    }
}