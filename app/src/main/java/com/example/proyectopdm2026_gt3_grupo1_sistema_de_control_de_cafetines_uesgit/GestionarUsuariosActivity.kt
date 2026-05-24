package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.graphics.Typeface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.OpcionMenuLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.RolLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.UsuarioLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PermisoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.UsuarioRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Rol
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Usuario
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.AuthValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class GestionarUsuariosActivity : AppCompatActivity() {
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var permisoRepository: PermisoRepository
    private lateinit var localRepository: LocalRepository
    private var roles: List<Rol> = emptyList()
    private var locales: List<Local> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_usuarios)

        configurarDependencias()
        cargarRoles()
        cargarLocales()
        configurarAcciones()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        val usuarioLocalDataSource = UsuarioLocalDataSource(databaseHelper)
        val rolLocalDataSource = RolLocalDataSource(databaseHelper)
        val opcionMenuLocalDataSource = OpcionMenuLocalDataSource(databaseHelper)
        val localLocalDataSource = LocalLocalDataSource(databaseHelper)
        usuarioRepository = UsuarioRepository(usuarioLocalDataSource, rolLocalDataSource)
        permisoRepository = PermisoRepository(opcionMenuLocalDataSource)
        localRepository = LocalRepository(localLocalDataSource)
    }

    private fun configurarAcciones() {
        findViewById<Button>(R.id.btnCrearUsuarioRol).setOnClickListener {
            crearUsuario()
        }
    }

    private fun cargarRoles() {
        when (val resultado = usuarioRepository.obtenerRoles()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                roles = resultado.data
                configurarSpinnerRoles(findViewById(R.id.spRolNuevoUsuario), null)
            }
        }
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocalesActivos()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                locales = resultado.data
                configurarSpinnerLocales(findViewById(R.id.spLocalNuevoUsuario), null)
            }
        }
    }

    private fun cargarUsuarios() {
        when (val resultado = usuarioRepository.obtenerUsuarios()) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                mostrarUsuarios(emptyList())
            }
            is OperationResult.Success -> mostrarUsuarios(resultado.data)
        }
    }

    private fun crearUsuario() {
        if (roles.isEmpty()) {
            mostrarMensaje("No hay roles disponibles para asignar.")
            return
        }

        val nombre = findViewById<EditText>(R.id.etNombreUsuarioAdmin).text.toString().trim()
        val email = findViewById<EditText>(R.id.etCorreoUsuarioAdmin).text.toString().trim()
        val carnet = findViewById<EditText>(R.id.etCarnetUsuarioAdmin).text.toString().trim().uppercase()
        val password = findViewById<EditText>(R.id.etPasswordUsuarioAdmin).text.toString()
        val confirmarPassword = findViewById<EditText>(R.id.etConfirmPasswordUsuarioAdmin).text.toString()
        val rol = obtenerRolSeleccionado(findViewById(R.id.spRolNuevoUsuario))
        val localAsignado = obtenerLocalSeleccionado(findViewById(R.id.spLocalNuevoUsuario))

        if (rol?.nombreRol == AppConstants.ROL_ENCARGADO && localAsignado == null) {
            mostrarMensaje("Debe asignar un local al usuario encargado.")
            return
        }

        val errorValidacion = AuthValidator.validarRegistro(
            nombre = nombre,
            email = email,
            password = password,
            confirmarPassword = confirmarPassword,
            carnet = carnet,
            idRol = rol?.idRol,
            idUbicacion = null
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
            idRol = rol?.idRol ?: 0,
            idUbicacion = null,
            idLocalAsignado = if (rol?.nombreRol == AppConstants.ROL_ENCARGADO) localAsignado?.idLocal else null
        )

        when (val resultado = usuarioRepository.registrarUsuario(usuario)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarMensaje("Usuario creado correctamente.")
                limpiarFormulario()
                cargarUsuarios()
            }
        }
    }

    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorUsuarios)
        val tvVacios = findViewById<TextView>(R.id.tvUsuariosVacios)

        contenedor.removeAllViews()
        if (usuarios.isEmpty()) {
            tvVacios.visibility = TextView.VISIBLE
            return
        }

        tvVacios.visibility = TextView.GONE
        usuarios.forEach { usuario ->
            contenedor.addView(crearTarjetaUsuario(usuario))
        }
    }

    private fun crearTarjetaUsuario(usuario: Usuario): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(8).toFloat()
            cardElevation = dpToPx(2).toFloat()
            useCompatPadding = true
            setCardBackgroundColor(getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12)
            }
        }

        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        contenido.addView(crearTexto(usuario.nombre, 16f, true))
        contenido.addView(crearTexto("${usuario.email} | ${usuario.carnet}", 13f, false))
        contenido.addView(crearTexto("Rol actual: ${obtenerNombreRol(usuario.idRol)}", 13f, false))
        if (obtenerNombreRol(usuario.idRol) == AppConstants.ROL_ENCARGADO) {
            contenido.addView(crearTexto("Local asignado: ${obtenerNombreLocal(usuario.idLocalAsignado)}", 13f, false))
        }
        contenido.addView(crearTexto("Opciones: ${obtenerResumenOpciones(usuario.idRol)}", 12f, false))
        contenido.addView(crearControlRol(usuario))

        cardView.addView(contenido)
        return cardView
    }

    private fun crearControlRol(usuario: Usuario): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }
        }

        val spinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f).apply {
                marginEnd = dpToPx(8)
            }
        }
        configurarSpinnerRoles(spinner, usuario.idRol)

        val spinnerLocal = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(48), 1f).apply {
                marginEnd = dpToPx(8)
            }
        }
        configurarSpinnerLocales(spinnerLocal, usuario.idLocalAsignado)

        val btnActualizar = Button(this).apply {
            text = "Actualizar"
            textSize = 12f
            setBackgroundColor(getColor(R.color.wine))
            setTextColor(getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(48)
            )
            setOnClickListener {
                val rolSeleccionado = obtenerRolSeleccionado(spinner)
                val localSeleccionado = obtenerLocalSeleccionado(spinnerLocal)
                if (rolSeleccionado == null) {
                    mostrarMensaje("Debe seleccionar un rol válido.")
                    return@setOnClickListener
                }
                actualizarRolUsuario(usuario, rolSeleccionado, localSeleccionado)
            }
        }

        fila.addView(spinner)
        fila.addView(spinnerLocal)
        fila.addView(btnActualizar)
        return fila
    }

    private fun configurarSpinnerRoles(spinner: Spinner, idRolSeleccionado: Int?) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles.map { it.nombreRol }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val posicionSeleccionada = roles.indexOfFirst { it.idRol == idRolSeleccionado }
        if (posicionSeleccionada >= 0) {
            spinner.setSelection(posicionSeleccionada)
        }
    }

    private fun configurarSpinnerLocales(spinner: Spinner, idLocalSeleccionado: Int?) {
        val nombresLocales = listOf("Sin local") + locales.map { it.nombreLocal }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresLocales
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val posicionSeleccionada = locales.indexOfFirst { it.idLocal == idLocalSeleccionado }
        if (posicionSeleccionada >= 0) {
            spinner.setSelection(posicionSeleccionada + 1)
        }
    }

    private fun actualizarRolUsuario(usuario: Usuario, rol: Rol, local: Local?) {
        val idLocalAsignado = if (rol.nombreRol == AppConstants.ROL_ENCARGADO) local?.idLocal else null
        when (
            val resultado = usuarioRepository.actualizarRolYLocalAsignado(
                idUsuario = usuario.idUsuario,
                idRol = rol.idRol,
                idLocalAsignado = idLocalAsignado
            )
        ) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    mostrarMensaje("Rol actualizado a ${rol.nombreRol}.")
                    cargarUsuarios()
                } else {
                    mostrarMensaje("No se pudo actualizar el rol.")
                }
            }
        }
    }

    private fun obtenerRolSeleccionado(spinner: Spinner): Rol? {
        return roles.getOrNull(spinner.selectedItemPosition)
    }

    private fun obtenerLocalSeleccionado(spinner: Spinner): Local? {
        val posicion = spinner.selectedItemPosition
        if (posicion <= 0) return null
        return locales.getOrNull(posicion - 1)
    }

    private fun obtenerNombreRol(idRol: Int): String {
        return roles.firstOrNull { it.idRol == idRol }?.nombreRol ?: "Sin rol"
    }

    private fun obtenerNombreLocal(idLocal: Int?): String {
        if (idLocal == null || idLocal <= 0) return "Sin local asignado"
        return locales.firstOrNull { it.idLocal == idLocal }?.nombreLocal ?: "Local #$idLocal"
    }

    private fun obtenerResumenOpciones(idRol: Int): String {
        return when (val resultado = permisoRepository.obtenerOpcionesPorRol(idRol)) {
            is OperationResult.Error -> "No disponibles"
            is OperationResult.Success -> {
                if (resultado.data.isEmpty()) {
                    "Sin opciones asignadas"
                } else {
                    resultado.data.joinToString(", ") { it.nombreOpcion }
                }
            }
        }
    }

    private fun limpiarFormulario() {
        findViewById<EditText>(R.id.etNombreUsuarioAdmin).text.clear()
        findViewById<EditText>(R.id.etCorreoUsuarioAdmin).text.clear()
        findViewById<EditText>(R.id.etCarnetUsuarioAdmin).text.clear()
        findViewById<EditText>(R.id.etPasswordUsuarioAdmin).text.clear()
        findViewById<EditText>(R.id.etConfirmPasswordUsuarioAdmin).text.clear()
        findViewById<Spinner>(R.id.spRolNuevoUsuario).setSelection(0)
        findViewById<Spinner>(R.id.spLocalNuevoUsuario).setSelection(0)
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean): TextView {
        return TextView(this).apply {
            text = texto
            this.textSize = textSize
            setTextColor(android.graphics.Color.parseColor("#333333"))
            if (negrita) {
                setTypeface(typeface, Typeface.BOLD)
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
