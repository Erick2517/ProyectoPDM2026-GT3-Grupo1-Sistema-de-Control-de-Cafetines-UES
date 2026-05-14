package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation

object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val carnetRegex = Regex("^[A-Za-z]{2}\\d{5}$")

    fun validarRegistro(
        nombre: String,
        email: String,
        password: String,
        confirmarPassword: String,
        carnet: String,
        idRol: Int?,
        idUbicacion: Int?
    ): String? {
        if (nombre.isBlank()) return "El nombre es obligatorio."
        if (email.isBlank()) return "El correo es obligatorio."
        if (!emailRegex.matches(email)) return "El formato del correo no es válido."
        if (!email.endsWith("@ues.edu.sv", ignoreCase = true)) {
            return "El correo debe pertenecer al dominio institucional @ues.edu.sv."
        }
        if (password.isBlank()) return "La contraseña es obligatoria."
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres."
        }
        if (confirmarPassword.isBlank()) return "Debe confirmar la contraseña."
        if (password != confirmarPassword) return "Las contraseñas no coinciden."
        if (carnet.isBlank()) return "El carnet es obligatorio."
        if (!carnetRegex.matches(carnet)) return "El formato del carnet no es válido."
        if (idRol == null || idRol <= 0) return "Debe asignar un rol válido."
        if (idUbicacion != null && idUbicacion <= 0) return "Debe seleccionar una ubicación válida."

        return null
    }

    fun validarLogin(email: String, password: String): String? {
        if (email.isBlank()) return "El correo es obligatorio."
        if (!emailRegex.matches(email)) return "El formato del correo no es válido."
        if (password.isBlank()) return "La contraseña es obligatoria."

        return null
    }

    private const val MIN_PASSWORD_LENGTH = 6
}
