package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants

object LocalValidator {
    private val estadosPermitidos = setOf(
        AppConstants.ESTADO_ACTIVO,
        AppConstants.ESTADO_INACTIVO
    )

    fun validarLocal(nombre: String, ubicacion: String, estado: String): String? {
        if (nombre.isBlank()) return "El nombre del local es obligatorio."
        if (ubicacion.isBlank()) return "La ubicación del local es obligatoria."
        if (estado.isBlank()) return "Debe seleccionar el estado del local."
        if (!estadosPermitidos.contains(estado)) return "El estado del local no es válido."

        return null
    }
}
