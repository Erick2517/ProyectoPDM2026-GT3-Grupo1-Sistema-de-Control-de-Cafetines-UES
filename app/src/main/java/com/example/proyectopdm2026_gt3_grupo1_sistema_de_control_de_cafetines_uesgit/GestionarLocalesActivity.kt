package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class GestionarLocalesActivity : AppCompatActivity() {
    private lateinit var localRepository: LocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_locales)

        configurarDependencias()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAddLocal = findViewById<Button>(R.id.btnAgregarLocal)
        btnAddLocal.setOnClickListener {
            startActivity(Intent(this, NuevoLocalActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLocales()
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocales()) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                mostrarLocales(emptyList())
            }
            is OperationResult.Success -> mostrarLocales(resultado.data)
        }
    }

    private fun mostrarLocales(locales: List<Local>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorLocalesAdmin)
        val tvVacios = findViewById<TextView>(R.id.tvLocalesAdminVacios)

        contenedor.removeAllViews()
        if (locales.isEmpty()) {
            tvVacios.visibility = TextView.VISIBLE
            return
        }

        tvVacios.visibility = TextView.GONE
        locales.forEach { local ->
            contenedor.addView(crearTarjetaLocal(local))
        }
    }

    private fun crearTarjetaLocal(local: Local): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(8).toFloat()
            cardElevation = dpToPx(2).toFloat()
            useCompatPadding = true
            setCardBackgroundColor(
                if (local.estado == AppConstants.ESTADO_ACTIVO) {
                    getColor(android.R.color.white)
                } else {
                    android.graphics.Color.parseColor("#F3F3F3")
                }
            )
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

        contenido.addView(crearTexto(local.nombreLocal, 16f, true))
        contenido.addView(crearTexto("Ubicación: ${local.ubicacion}", 13f, false))
        contenido.addView(crearTexto(local.descripcion.orEmpty().ifBlank { "Sin descripción" }, 13f, false))
        contenido.addView(crearEstado(local.estado))
        if (local.estado == AppConstants.ESTADO_INACTIVO) {
            contenido.addView(crearTexto("No visible para usuarios finales.", 12f, false))
        }
        contenido.addView(crearAcciones(local))

        cardView.addView(contenido)
        return cardView
    }

    private fun crearEstado(estado: String): TextView {
        return TextView(this).apply {
            text = "Estado: $estado"
            textSize = 13f
            setTextColor(
                if (estado == AppConstants.ESTADO_ACTIVO) {
                    android.graphics.Color.parseColor("#1B5E20")
                } else {
                    android.graphics.Color.parseColor("#666666")
                }
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }
    }

    private fun crearAcciones(local: Local): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(10)
            }
        }

        fila.addView(crearBoton("Editar", true) {
            val intent = Intent(this, NuevoLocalActivity::class.java).apply {
                putExtra(AppConstants.EXTRA_ID_LOCAL, local.idLocal)
                putExtra(AppConstants.EXTRA_MODO_EDICION_LOCAL, true)
            }
            startActivity(intent)
        })

        val textoEstado = if (local.estado == AppConstants.ESTADO_ACTIVO) "Desactivar" else "Activar"
        fila.addView(crearBoton(textoEstado, false) {
            cambiarEstado(local)
        })

        return fila
    }

    private fun crearBoton(texto: String, principal: Boolean, accion: () -> Unit): Button {
        return Button(this).apply {
            text = texto
            isSingleLine = true
            textSize = 12f
            minWidth = 0
            minimumWidth = 0
            setPadding(dpToPx(10), 0, dpToPx(10), 0)
            if (principal) {
                setBackgroundColor(getColor(R.color.wine))
                setTextColor(getColor(android.R.color.white))
            } else {
                setBackgroundResource(R.drawable.bg_btn_outline)
                setTextColor(getColor(android.R.color.black))
            }
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(44),
                1f
            ).apply {
                marginEnd = dpToPx(8)
            }
            setOnClickListener { accion() }
        }
    }

    private fun cambiarEstado(local: Local) {
        when (val resultado = localRepository.cambiarEstadoLocal(local)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    val mensaje = if (local.estado == AppConstants.ESTADO_ACTIVO) {
                        "Local desactivado correctamente."
                    } else {
                        "Local activado correctamente."
                    }
                    mostrarMensaje(mensaje)
                    cargarLocales()
                } else {
                    mostrarMensaje("No se pudo actualizar el estado del local.")
                }
            }
        }
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean): TextView {
        return TextView(this).apply {
            this.text = texto
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
