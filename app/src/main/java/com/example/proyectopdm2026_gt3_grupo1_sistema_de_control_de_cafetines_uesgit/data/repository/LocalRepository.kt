package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class LocalRepository(private val localLocalDataSource: LocalLocalDataSource) {
    fun registrarLocal(local: Local): OperationResult<Long> {
        return try {
            val idLocal = localLocalDataSource.insertarLocal(local)
            if (idLocal == -1L) {
                OperationResult.Error("No se pudo registrar el local.")
            } else {
                OperationResult.Success(idLocal)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al registrar el local.", exception)
        }
    }

    fun obtenerLocalesActivos(): OperationResult<List<Local>> {
        return try {
            OperationResult.Success(localLocalDataSource.obtenerLocalesActivos())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar locales.", exception)
        }
    }
}
