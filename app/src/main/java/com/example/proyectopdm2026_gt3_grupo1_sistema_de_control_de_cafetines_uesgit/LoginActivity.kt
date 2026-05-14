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
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val btnEncargado = findViewById<Button>(R.id.btnEncargado)

        btnLogin.setOnClickListener {
            iniciarSesion()
        }

        btnAdmin.setOnClickListener {
            mostrarMensaje("Ingrese con una cuenta administradora para acceder.")
        }

        btnEncargado.setOnClickListener {
            mostrarMensaje("Ingrese con una cuenta de encargado para acceder.")
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
                    idRol = usuario.idRol,
                    nombreRol = nombreRol,
                    idUbicacion = usuario.idUbicacion
                )
                navegarSegunRol(nombreRol)
            }
        }
    }

    private fun navegarSegunRol(nombreRol: String) {
        val destino = when (nombreRol) {
            AppConstants.ROL_ADMINISTRADOR -> PanelAdminActivity::class.java
            AppConstants.ROL_ENCARGADO -> PanelEncargadoActivity::class.java
            else -> BienvenidaActivity::class.java
        }

        startActivity(Intent(this, destino))
        finish()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
