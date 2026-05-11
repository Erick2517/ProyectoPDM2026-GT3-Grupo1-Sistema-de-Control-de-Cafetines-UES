package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Locale

class GestionarProductos : AppCompatActivity() {
    private lateinit var productoRepository: ProductoRepository
    private lateinit var localRepository: LocalRepository
    private var locales: List<Local> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_productos)
        configurarDependencias()
        cargarLocales()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAgregarProd = findViewById<Button>(R.id.btnAgregarProducto)
        btnAgregarProd.setOnClickListener {
            val intent = Intent(this, NuevoProductoActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocales()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                locales = resultado.data
                val nombres = listOf("Todos los locales") + locales.map { it.nombreLocal }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spFiltroLocal).adapter = adapter
                findViewById<Spinner>(R.id.spFiltroLocal).onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                            cargarProductos()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                    }
            }
        }
    }

    private fun cargarProductos() {
        if (!::productoRepository.isInitialized) return

        val posicion = findViewById<Spinner>(R.id.spFiltroLocal).selectedItemPosition
        val resultado = if (posicion > 0) {
            productoRepository.obtenerProductosPorLocal(locales.getOrNull(posicion - 1)?.idLocal ?: 0)
        } else {
            productoRepository.obtenerProductos()
        }

        when (resultado) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                mostrarProductos(emptyList())
            }
            is OperationResult.Success -> mostrarProductos(resultado.data)
        }
    }

    private fun mostrarProductos(productos: List<Producto>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorProductosAdmin)
        val tvVacios = findViewById<TextView>(R.id.tvProductosAdminVacios)
        contenedor.removeAllViews()

        if (productos.isEmpty()) {
            tvVacios.visibility = TextView.VISIBLE
            return
        }

        tvVacios.visibility = TextView.GONE
        productos.forEach { producto ->
            contenedor.addView(crearTarjetaProducto(producto))
        }
    }

    private fun crearTarjetaProducto(producto: Producto): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(8).toFloat()
            cardElevation = dpToPx(2).toFloat()
            useCompatPadding = true
            setCardBackgroundColor(getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dpToPx(12) }
        }

        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        contenido.addView(crearFilaNombrePrecio(producto))
        contenido.addView(crearTexto("Local: ${obtenerNombreLocal(producto.idLocal)}", 13f, false))
        contenido.addView(crearTexto("Tipo: ${producto.tipo} | Stock: ${producto.stock}", 13f, false))
        contenido.addView(crearTexto("Disponibilidad: ${producto.disponibilidad}", 13f, false))
        contenido.addView(crearAcciones(producto))

        cardView.addView(contenido)
        return cardView
    }

    private fun crearFilaNombrePrecio(producto: Producto): LinearLayout {
        val fila = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val nombre = crearTexto(producto.nombreProducto, 16f, true).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val precio = crearTexto(String.format(Locale.US, "$%.2f", producto.precio), 16f, true)
        fila.addView(nombre)
        fila.addView(precio)
        return fila
    }

    private fun crearAcciones(producto: Producto): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dpToPx(10) }
        }

        fila.addView(crearBoton("Editar", true) {
            val intent = Intent(this, NuevoProductoActivity::class.java).apply {
                putExtra(AppConstants.EXTRA_ID_PRODUCTO, producto.idProducto)
                putExtra(AppConstants.EXTRA_MODO_EDICION_PRODUCTO, true)
            }
            startActivity(intent)
        })

        val textoDisponibilidad = if (producto.disponibilidad == AppConstants.DISPONIBILIDAD_DISPONIBLE) {
            "No disponible"
        } else {
            "Disponible"
        }
        fila.addView(crearBoton(textoDisponibilidad, false) {
            cambiarDisponibilidad(producto)
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
            if (principal) {
                setBackgroundColor(getColor(R.color.wine))
                setTextColor(getColor(android.R.color.white))
            } else {
                setBackgroundResource(R.drawable.bg_btn_outline)
                setTextColor(getColor(android.R.color.black))
            }
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(44), 1f).apply {
                marginEnd = dpToPx(8)
            }
            setOnClickListener { accion() }
        }
    }

    private fun cambiarDisponibilidad(producto: Producto) {
        when (val resultado = productoRepository.cambiarDisponibilidadProducto(producto)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    mostrarMensaje("Disponibilidad actualizada.")
                    cargarProductos()
                } else {
                    mostrarMensaje("No se pudo actualizar el producto.")
                }
            }
        }
    }

    private fun obtenerNombreLocal(idLocal: Int): String {
        return locales.firstOrNull { it.idLocal == idLocal }?.nombreLocal ?: "Local #$idLocal"
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean): TextView {
        return TextView(this).apply {
            this.text = texto
            this.textSize = textSize
            setTextColor(android.graphics.Color.parseColor("#333333"))
            if (negrita) setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
