package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun guardarSesion(
        idUsuario: Int,
        idRol: Int,
        nombreRol: String,
        idUbicacion: Int? = null,
        idLocalAsignado: Int? = null
    ) {
        preferences.edit()
            .putInt(KEY_ID_USUARIO, idUsuario)
            .putInt(KEY_ID_ROL, idRol)
            .putString(KEY_NOMBRE_ROL, nombreRol)
            .putInt(KEY_ID_UBICACION, idUbicacion ?: 0)
            .putInt(KEY_ID_LOCAL_ASIGNADO, idLocalAsignado ?: 0)
            .putBoolean(KEY_SESION_ACTIVA, true)
            .apply()
    }

    fun cerrarSesion() {
        preferences.edit().clear().apply()
    }

    fun haySesionActiva(): Boolean {
        return preferences.getBoolean(KEY_SESION_ACTIVA, false)
    }

    fun obtenerIdUsuario(): Int {
        return preferences.getInt(KEY_ID_USUARIO, 0)
    }

    fun obtenerIdRol(): Int {
        return preferences.getInt(KEY_ID_ROL, 0)
    }

    fun obtenerNombreRol(): String? {
        return preferences.getString(KEY_NOMBRE_ROL, null)
    }

    fun obtenerIdUbicacion(): Int? {
        val idUbicacion = preferences.getInt(KEY_ID_UBICACION, 0)
        return if (idUbicacion > 0) idUbicacion else null
    }

    fun obtenerIdLocalAsignado(): Int? {
        val idLocalAsignado = preferences.getInt(KEY_ID_LOCAL_ASIGNADO, 0)
        return if (idLocalAsignado > 0) idLocalAsignado else null
    }

    private companion object {
        const val PREFERENCES_NAME = "cafetines_session"
        const val KEY_ID_USUARIO = "id_usuario"
        const val KEY_ID_ROL = "id_rol"
        const val KEY_NOMBRE_ROL = "nombre_rol"
        const val KEY_ID_UBICACION = "id_ubicacion"
        const val KEY_ID_LOCAL_ASIGNADO = "id_local_asignado"
        const val KEY_SESION_ACTIVA = "sesion_activa"
    }
}
