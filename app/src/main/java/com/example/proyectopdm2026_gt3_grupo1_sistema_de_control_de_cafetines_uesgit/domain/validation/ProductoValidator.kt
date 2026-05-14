package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

object ProductoValidator {
    fun validarProducto(
        nombre: String,
        precio: Double?,
        tipo: String,
        disponibilidad: String,
        stock: Int?,
        idLocal: Int?
    ): String? {
        if (nombre.isBlank()) return "El nombre del producto es obligatorio."
        if (precio == null || precio <= 0.0) return "El precio debe ser mayor a cero."
        if (tipo.isBlank()) return "Debe seleccionar el tipo de producto."
        if (disponibilidad.isBlank()) return "Debe seleccionar la disponibilidad del producto."
        if (stock == null || stock < 0) return "El stock no puede ser negativo."
        if (idLocal == null || idLocal <= 0) return "Debe seleccionar un local válido."

        return null
    }
}
