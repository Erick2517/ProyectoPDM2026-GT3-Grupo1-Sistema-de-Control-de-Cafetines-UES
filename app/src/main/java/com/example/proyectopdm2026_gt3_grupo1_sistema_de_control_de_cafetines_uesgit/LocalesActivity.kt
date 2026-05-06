package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
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

class LocalesActivity : AppCompatActivity() {
    private lateinit var localRepository: LocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_locales)

        configurarRepositorio()
        cargarLocales()

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

    private fun configurarRepositorio() {
        val databaseHelper = AppDatabaseHelper(this)
        val localLocalDataSource = LocalLocalDataSource(databaseHelper)
        localRepository = LocalRepository(localLocalDataSource)
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocalesActivos()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarLocales(resultado.data)
        }
    }

    private fun mostrarLocales(locales: List<Local>) {
        val contenedorLocales = findViewById<LinearLayout>(R.id.contenedorLocales)
        val tvLocalesVacios = findViewById<TextView>(R.id.tvLocalesVacios)

        contenedorLocales.removeAllViews()
        tvLocalesVacios.visibility = if (locales.isEmpty()) TextView.VISIBLE else TextView.GONE

        locales.forEach { local ->
            contenedorLocales.addView(crearTarjetaLocal(local))
        }
    }

    private fun crearTarjetaLocal(local: Local): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(12).toFloat()
            cardElevation = dpToPx(4).toFloat()
            useCompatPadding = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12)
            }
        }

        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
            setOnClickListener { abrirProductos(local) }
        }

        val imagenLocal = ImageView(this).apply {
            setImageResource(R.drawable.logo_ues)
            contentDescription = "Imagen del local ${local.nombreLocal}"
            layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(80))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        val informacion = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(12), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        informacion.addView(crearTexto(local.nombreLocal, 16f, true))
        informacion.addView(crearTexto("Ubicación: ${local.ubicacion}", 13f))
        informacion.addView(crearTexto("Estado: ${local.estado}", 13f))
        local.descripcion?.takeIf { it.isNotBlank() }?.let { descripcion ->
            informacion.addView(crearTexto(descripcion, 13f))
        }
        informacion.addView(crearTexto("Ver menú >", 13f, true))

        contenido.addView(imagenLocal)
        contenido.addView(informacion)
        cardView.addView(contenido)

        return cardView
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = texto
            this.textSize = textSize
            if (negrita) {
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }
    }

    private fun abrirProductos(local: Local) {
        val intent = Intent(this, ProductosActivity::class.java).apply {
            putExtra(AppConstants.EXTRA_ID_LOCAL, local.idLocal)
            putExtra(AppConstants.EXTRA_NOMBRE_LOCAL, local.nombreLocal)
        }
        startActivity(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
