package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.RolLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UsuarioLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Usuario
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.PasswordHasher

class UsuarioRepository(
    private val usuarioLocalDataSource: UsuarioLocalDataSource,
    private val rolLocalDataSource: RolLocalDataSource
) {
    fun registrarUsuario(usuario: Usuario): OperationResult<Long> {
        return try {
            if (usuarioLocalDataSource.existeEmailOCarnet(usuario.email, usuario.carnet)) {
                return OperationResult.Error("Ya existe un usuario con ese correo o carnet.")
            }

            val usuarioConPasswordProtegida = usuario.copy(password = PasswordHasher.hash(usuario.password))
            val idUsuario = usuarioLocalDataSource.insertarUsuario(usuarioConPasswordProtegida)
            if (idUsuario == -1L) {
                OperationResult.Error("No se pudo registrar el usuario.")
            } else {
                OperationResult.Success(idUsuario)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al registrar el usuario.", exception)
        }
    }

    fun iniciarSesion(email: String, password: String): OperationResult<Usuario> {
        return try {
            val usuario = usuarioLocalDataSource.obtenerUsuarioPorCredenciales(
                email,
                PasswordHasher.hash(password)
            )
            if (usuario == null) {
                OperationResult.Error("Correo o contraseña incorrectos.")
            } else {
                OperationResult.Success(usuario)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al iniciar sesión.", exception)
        }
    }

    fun obtenerIdRolUsuario(): OperationResult<Int> {
        return try {
            val rol = rolLocalDataSource.obtenerRolPorNombre(AppConstants.ROL_USUARIO)
            if (rol == null) {
                OperationResult.Error("No se encontró el rol Usuario.")
            } else {
                OperationResult.Success(rol.idRol)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el rol Usuario.", exception)
        }
    }

    fun obtenerNombreRol(idRol: Int): OperationResult<String> {
        return try {
            val rol = rolLocalDataSource.obtenerRolPorId(idRol)
            if (rol == null) {
                OperationResult.Error("No se encontró el rol del usuario.")
            } else {
                OperationResult.Success(rol.nombreRol)
            }
        } catch (exception: Exception) {
            OperationResult.Error("Ocurrió un error al consultar el rol del usuario.", exception)
        }
    }
}
