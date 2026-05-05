package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import java.time.LocalTime

object AntojitosValidator {
    fun validarAntojitos(cantidadAntojitos: Int, horaActual: LocalTime = LocalTime.now()): String? {
        if (cantidadAntojitos > AppConstants.MAX_ANTOJITOS_POR_PEDIDO) {
            return "Solo puede agregar un máximo de ${AppConstants.MAX_ANTOJITOS_POR_PEDIDO} antojitos por pedido."
        }

        val horaInicio = LocalTime.of(AppConstants.HORA_INICIO_ANTOJITOS, 0)
        val horaFin = LocalTime.of(AppConstants.HORA_FIN_ANTOJITOS, 0)
        val estaEnHorarioPermitido = !horaActual.isBefore(horaInicio) && horaActual.isBefore(horaFin)

        if (cantidadAntojitos > 0 && !estaEnHorarioPermitido) {
            return "Los antojitos solo pueden ordenarse entre 2:00 p.m. y 4:00 p.m."
        }

        return null
    }
}
