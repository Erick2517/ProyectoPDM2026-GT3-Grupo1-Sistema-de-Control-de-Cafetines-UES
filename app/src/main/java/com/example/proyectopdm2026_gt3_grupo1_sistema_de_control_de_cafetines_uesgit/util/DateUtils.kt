package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val fechaHoraFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun obtenerFechaHoraActual(): String {
        return LocalDateTime.now().format(fechaHoraFormatter)
    }
}
