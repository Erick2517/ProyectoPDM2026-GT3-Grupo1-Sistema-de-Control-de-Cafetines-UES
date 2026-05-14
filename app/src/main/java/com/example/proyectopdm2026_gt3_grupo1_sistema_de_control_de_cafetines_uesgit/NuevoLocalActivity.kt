package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.LocalValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class NuevoLocalActivity : AppCompatActivity() {
    private lateinit var localRepository: LocalRepository
    private val estados = listOf(AppConstants.ESTADO_ACTIVO, AppConstants.ESTADO_INACTIVO)
    private var localEnEdicion: Local? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevo_local)

        configurarDependencias()
        configurarSpinnerEstado()
        cargarLocalSiEsEdicion()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAddLocal = findViewById<Button>(R.id.btnGuardar)
        btnAddLocal.setOnClickListener {
            guardarLocal()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
    }

    private fun configurarSpinnerEstado() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinnerEstado).adapter = adapter
    }

    private fun cargarLocalSiEsEdicion() {
        val esEdicion = intent.getBooleanExtra(AppConstants.EXTRA_MODO_EDICION_LOCAL, false)
        val idLocal = intent.getIntExtra(AppConstants.EXTRA_ID_LOCAL, 0)
        if (!esEdicion || idLocal <= 0) return

        when (val resultado = localRepository.obtenerLocalPorId(idLocal)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarLocal(resultado.data)
        }
    }

    private fun mostrarLocal(local: Local) {
        localEnEdicion = local
        findViewById<TextView>(R.id.tvTituloFormularioLocal).text = "Editar local"
        findViewById<Button>(R.id.btnGuardar).text = "Actualizar local"
        findViewById<EditText>(R.id.etNombreLocal).setText(local.nombreLocal)
        findViewById<EditText>(R.id.etUbicacionLocal).setText(local.ubicacion)
        findViewById<EditText>(R.id.etDescripcionLocal).setText(local.descripcion.orEmpty())

        val posicionEstado = estados.indexOf(local.estado)
        if (posicionEstado >= 0) {
            findViewById<Spinner>(R.id.spinnerEstado).setSelection(posicionEstado)
        }
    }

    private fun guardarLocal() {
        val nombre = findViewById<EditText>(R.id.etNombreLocal).text.toString().trim()
        val ubicacion = findViewById<EditText>(R.id.etUbicacionLocal).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.etDescripcionLocal).text.toString().trim()
        val estado = findViewById<Spinner>(R.id.spinnerEstado).selectedItem?.toString().orEmpty()

        val errorValidacion = LocalValidator.validarLocal(nombre, ubicacion, estado)
        if (errorValidacion != null) {
            mostrarMensaje(errorValidacion)
            return
        }

        val localActual = localEnEdicion
        val local = Local(
            idLocal = localActual?.idLocal ?: 0,
            nombreLocal = nombre,
            ubicacion = ubicacion,
            descripcion = descripcion.ifBlank { null },
            estado = estado
        )

        if (localActual == null) {
            registrarLocal(local)
        } else {
            actualizarLocal(local)
        }
    }

    private fun registrarLocal(local: Local) {
        when (val resultado = localRepository.registrarLocal(local)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarMensaje("Local registrado correctamente.")
                finish()
            }
        }
    }

    private fun actualizarLocal(local: Local) {
        when (val resultado = localRepository.actualizarLocal(local)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    mostrarMensaje("Local actualizado correctamente.")
                    finish()
                } else {
                    mostrarMensaje("No se pudo actualizar el local.")
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
