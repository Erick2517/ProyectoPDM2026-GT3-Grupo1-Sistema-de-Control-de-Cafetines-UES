package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : OperationResult<Nothing>()
}
