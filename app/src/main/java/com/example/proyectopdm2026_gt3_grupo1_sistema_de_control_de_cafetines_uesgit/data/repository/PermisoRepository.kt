package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.OpcionMenuLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.OpcionMenu
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class PermisoRepository(private val opcionMenuLocalDataSource: OpcionMenuLocalDataSource) {
    fun obtenerOpcionesPorRol(idRol: Int): OperationResult<List<OpcionMenu>> {
        return try {
            if (idRol <= 0) {
                return OperationResult.Error("Debe existir un rol válido para consultar permisos.")
            }

            OperationResult.Success(opcionMenuLocalDataSource.obtenerOpcionesPorRol(idRol))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar permisos del rol.", exception)
        }
    }

    fun rolTieneOpcion(idRol: Int, nombreOpcion: String): OperationResult<Boolean> {
        return try {
            if (idRol <= 0) {
                return OperationResult.Error("Debe existir un rol válido para validar permisos.")
            }
            if (nombreOpcion.isBlank()) {
                return OperationResult.Error("Debe seleccionar una opción de menú válida.")
            }

            OperationResult.Success(opcionMenuLocalDataSource.rolTieneOpcion(idRol, nombreOpcion))
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al validar permisos del rol.", exception)
        }
    }
}
