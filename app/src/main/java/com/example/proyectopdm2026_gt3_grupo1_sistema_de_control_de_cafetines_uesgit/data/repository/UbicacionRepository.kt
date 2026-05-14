package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UbicacionLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Ubicacion
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class UbicacionRepository(private val ubicacionLocalDataSource: UbicacionLocalDataSource) {
    fun obtenerUbicaciones(): OperationResult<List<Ubicacion>> {
        return try {
            OperationResult.Success(ubicacionLocalDataSource.obtenerUbicaciones())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar ubicaciones.", exception)
        }
    }
}
