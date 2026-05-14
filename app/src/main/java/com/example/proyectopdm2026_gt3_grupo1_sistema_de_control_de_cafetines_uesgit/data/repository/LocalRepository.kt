package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class LocalRepository(private val localLocalDataSource: LocalLocalDataSource) {
    fun registrarLocal(local: Local): OperationResult<Long> {
        return try {
            if (localLocalDataSource.existeNombreLocal(local.nombreLocal)) {
                return OperationResult.Error("Ya existe un local con ese nombre.")
            }

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

    fun actualizarLocal(local: Local): OperationResult<Boolean> {
        return try {
            if (local.idLocal <= 0) {
                return OperationResult.Error("Debe seleccionar un local válido.")
            }
            if (localLocalDataSource.existeNombreLocalEnOtroRegistro(local.nombreLocal, local.idLocal)) {
                return OperationResult.Error("Ya existe otro local con ese nombre.")
            }

            val filasActualizadas = localLocalDataSource.actualizarLocal(local)
            OperationResult.Success(filasActualizadas > 0)
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al actualizar el local.", exception)
        }
    }

    fun obtenerLocales(): OperationResult<List<Local>> {
        return try {
            OperationResult.Success(localLocalDataSource.obtenerLocales())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar locales.", exception)
        }
    }

    fun obtenerLocalPorId(idLocal: Int): OperationResult<Local> {
        return try {
            val local = localLocalDataSource.obtenerLocalPorId(idLocal)
            if (local == null) {
                OperationResult.Error("No se encontró el local.")
            } else {
                OperationResult.Success(local)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el local.", exception)
        }
    }

    fun obtenerLocalesActivos(): OperationResult<List<Local>> {
        return try {
            OperationResult.Success(localLocalDataSource.obtenerLocalesActivos())
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar locales.", exception)
        }
    }

    fun cambiarEstadoLocal(local: Local): OperationResult<Boolean> {
        val nuevoEstado = if (local.estado == AppConstants.ESTADO_ACTIVO) {
            AppConstants.ESTADO_INACTIVO
        } else {
            AppConstants.ESTADO_ACTIVO
        }

        return actualizarLocal(local.copy(estado = nuevoEstado))
    }
}
