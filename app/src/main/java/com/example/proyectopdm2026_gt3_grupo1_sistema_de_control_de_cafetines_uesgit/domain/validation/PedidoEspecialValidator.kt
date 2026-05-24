package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

object PedidoEspecialValidator {
    fun validarPedidoEspecial(
        descripcionEvento: String,
        fechaEvento: String,
        numeroPersonas: Int?,
        presupuesto: Double?,
        totalProductos: Double,
        montoMinimo: Double,
        montoMaximo: Double,
        anticipo: Double?
    ): String? {
        if (descripcionEvento.isBlank()) return "La descripción del evento es obligatoria."
        if (fechaEvento.isBlank()) return "La fecha del evento es obligatoria."
        if (numeroPersonas == null || numeroPersonas <= 0) return "La cantidad de personas debe ser mayor a cero."
        if (presupuesto == null || presupuesto <= 0.0) return "El presupuesto debe ser mayor a cero."
        if (totalProductos <= 0.0) return "Debe seleccionar al menos un producto para el evento."
        if (totalProductos < montoMinimo) return "El total de productos debe ser al menos de $${String.format("%.2f", montoMinimo)}."
        if (totalProductos > montoMaximo) return "El total de productos no debe superar $${String.format("%.2f", montoMaximo)}."
        if (totalProductos > presupuesto) return "El total de productos no debe exceder el presupuesto del evento."
        if (anticipo == null || anticipo <= 0.0) return "Debe registrar un anticipo mayor a cero."
        if (anticipo > totalProductos) return "El anticipo no puede ser mayor al total de productos."

        return null
    }
}
