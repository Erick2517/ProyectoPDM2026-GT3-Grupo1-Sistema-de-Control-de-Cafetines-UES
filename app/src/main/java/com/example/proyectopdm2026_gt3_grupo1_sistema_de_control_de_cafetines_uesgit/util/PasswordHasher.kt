package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import java.security.MessageDigest

object PasswordHasher {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
