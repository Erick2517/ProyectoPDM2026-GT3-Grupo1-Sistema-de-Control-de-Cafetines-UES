package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.RolLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UsuarioLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.UsuarioRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Usuario
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.AuthValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        configurarDependencias()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val lblRegistrarse = findViewById<TextView>(R.id.lbl_registrarse)

        btnLogin.setOnClickListener {
            iniciarSesion()
        }

        lblRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        val usuarioLocalDataSource = UsuarioLocalDataSource(databaseHelper)
        val rolLocalDataSource = RolLocalDataSource(databaseHelper)

        usuarioRepository = UsuarioRepository(usuarioLocalDataSource, rolLocalDataSource)
        sessionManager = SessionManager(this)
    }

    private fun iniciarSesion() {
        val email = findViewById<EditText>(R.id.txtEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.txtPass).text.toString()
        val errorValidacion = AuthValidator.validarLogin(email, password)

        if (errorValidacion != null) {
            mostrarMensaje(errorValidacion)
            return
        }

        when (val resultado = usuarioRepository.iniciarSesion(email, password)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> iniciarSesionConUsuario(resultado.data)
        }
    }

    private fun iniciarSesionConUsuario(usuario: Usuario) {
        when (val resultadoRol = usuarioRepository.obtenerNombreRol(usuario.idRol)) {
            is OperationResult.Error -> mostrarMensaje(resultadoRol.message)
            is OperationResult.Success -> {
                val nombreRol = resultadoRol.data
                sessionManager.guardarSesion(
                    idUsuario = usuario.idUsuario,
                    nombreUsuario = usuario.nombre,
                    idRol = usuario.idRol,
                    nombreRol = nombreRol,
                    idUbicacion = usuario.idUbicacion,
                    idLocalAsignado = usuario.idLocalAsignado
                )
                navegarSegunRol(nombreRol, usuario)
            }
        }
    }

    private fun navegarSegunRol(nombreRol: String, usuario: Usuario) {
        val destino = when (nombreRol) {
            AppConstants.ROL_ADMINISTRADOR -> PanelAdminActivity::class.java
            AppConstants.ROL_ENCARGADO -> PanelEncargadoActivity::class.java
            else -> BienvenidaActivity::class.java
        }

        val intent = Intent(this, destino)

        intent.putExtra("usuario", usuario.nombre)

        startActivity(intent)
        finish()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
