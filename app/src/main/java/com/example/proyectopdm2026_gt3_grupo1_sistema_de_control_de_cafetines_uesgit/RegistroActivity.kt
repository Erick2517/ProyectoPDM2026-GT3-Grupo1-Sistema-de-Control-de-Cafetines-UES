package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.RolLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UbicacionLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UsuarioLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.UbicacionRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.UsuarioRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Ubicacion
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Usuario
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.AuthValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.ApiClient

class RegistroActivity : AppCompatActivity() {
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var ubicacionRepository: UbicacionRepository
    private var ubicaciones: List<Ubicacion> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        configurarRepositorios()
        cargarUbicaciones()

        val lblLogin = findViewById<TextView>(R.id.lbl_login)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        lblLogin.setOnClickListener {
            navegarALogin()
        }

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarRepositorios() {
        val databaseHelper = AppDatabaseHelper(this)
        val rolLocalDataSource = RolLocalDataSource(databaseHelper)
        val usuarioLocalDataSource = UsuarioLocalDataSource(databaseHelper)
        val ubicacionLocalDataSource = UbicacionLocalDataSource(databaseHelper)

        usuarioRepository = UsuarioRepository(usuarioLocalDataSource, rolLocalDataSource)
        ubicacionRepository = UbicacionRepository(ubicacionLocalDataSource)
    }

    private fun cargarUbicaciones() {
        when (val resultado = ubicacionRepository.obtenerUbicaciones()) {
            is OperationResult.Success -> {
                ubicaciones = resultado.data
                val nombresUbicaciones = ubicaciones.map { it.nombreUbicacion }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombresUbicaciones
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spUbicacion).adapter = adapter
            }

            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
            }
        }
    }

    private fun registrarUsuario() {
        if (ubicaciones.isEmpty()) {
            mostrarMensaje("No hay ubicaciones disponibles para registrar el usuario.")
            return
        }

        val nombre = findViewById<EditText>(R.id.etNombre).text.toString().trim()
        val email = findViewById<EditText>(R.id.etCorreo).text.toString().trim()
        val carnet = findViewById<EditText>(R.id.etCarnet).text.toString().trim().uppercase()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        val confirmarPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()
        val ubicacionSeleccionada = obtenerUbicacionSeleccionada()

        when (val resultadoRol = usuarioRepository.obtenerIdRolUsuario()) {
            is OperationResult.Error -> mostrarMensaje(resultadoRol.message)
            is OperationResult.Success -> {
                val idRolUsuario = resultadoRol.data
                val errorValidacion = AuthValidator.validarRegistro(
                    nombre = nombre,
                    email = email,
                    password = password,
                    confirmarPassword = confirmarPassword,
                    carnet = carnet,
                    idRol = idRolUsuario,
                    idUbicacion = ubicacionSeleccionada?.idUbicacion
                )

                if (errorValidacion != null) {
                    mostrarMensaje(errorValidacion)
                    return
                }

                val usuario = Usuario(
                    nombre = nombre,
                    email = email,
                    password = password,
                    carnet = carnet,
                    idRol = idRolUsuario,
                    idUbicacion = ubicacionSeleccionada?.idUbicacion
                )

                guardarUsuario(usuario)
            }
        }
    }

    private fun obtenerUbicacionSeleccionada(): Ubicacion? {
        val spinner = findViewById<Spinner>(R.id.spUbicacion)
        return ubicaciones.getOrNull(spinner.selectedItemPosition)
    }

    private fun guardarUsuario(usuario: Usuario) {
        when (val resultado = usuarioRepository.registrarUsuario(usuario)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarMensaje("Usuario registrado localmente.")

                registrarUsuarioEnApi(usuario) {
                    navegarALogin()
                }
            }
        }
    }

    private fun registrarUsuarioEnApi(usuario: Usuario, onFinalizado: () -> Unit) {
        ApiClient.postForm(
            "/usuarios",
            mapOf(
                "nombre" to usuario.nombre,
                "email" to usuario.email,
                "password" to usuario.password,
                "carnet" to usuario.carnet,
                "id_rol" to usuario.idRol.toString(),
                "activo" to "1",
                "id_ubicacion" to (usuario.idUbicacion?.toString() ?: "1")
            ),
            object : ApiClient.ApiCallback {
                override fun onSuccess(response: String) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegistroActivity,
                            "API registro correcto",
                            Toast.LENGTH_LONG
                        ).show()

                        onFinalizado()
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegistroActivity,
                            "Error API registro: $error",
                            Toast.LENGTH_LONG
                        ).show()

                        onFinalizado()
                    }
                }
            }
        )
    }

    private fun navegarALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
