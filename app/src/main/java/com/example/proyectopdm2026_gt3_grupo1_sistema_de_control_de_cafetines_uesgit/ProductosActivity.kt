package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.graphics.Color
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.CarritoManager
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.ImageViewLoader
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Locale

class ProductosActivity : AppCompatActivity() {
    private lateinit var productoRepository: ProductoRepository
    private var idLocal: Int = 0
    private var nombreLocal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)

        configurarRepositorio()
        cargarDatosLocal()
        cargarProductos()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnVerCarrito = findViewById<Button>(R.id.btnVerCarrito)
        val imgCarrito = findViewById<ImageView>(R.id.btnCarrito)

        btnBack.setOnClickListener {
            finish()
        }

        btnVerCarrito.setOnClickListener {
            val intent = Intent(
                this,
                CarritoActivity::class.java
            )
            startActivity(intent)
        }

        imgCarrito.setOnClickListener {
            val intent = Intent(
                this,
                CarritoActivity::class.java
            )
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarRepositorio() {
        val databaseHelper = AppDatabaseHelper(this)
        val productoLocalDataSource = ProductoLocalDataSource(databaseHelper)
        productoRepository = ProductoRepository(productoLocalDataSource)
    }

    private fun cargarDatosLocal() {
        idLocal = intent.getIntExtra(AppConstants.EXTRA_ID_LOCAL, 0)
        nombreLocal = intent.getStringExtra(AppConstants.EXTRA_NOMBRE_LOCAL).orEmpty()

        findViewById<TextView>(R.id.tvNombreLocal).text =
            nombreLocal.ifBlank { "Seleccione un local" }
    }

    private fun cargarProductos() {
        if (idLocal <= 0) {
            mostrarProductos(emptyList())
            mostrarMensaje("Debe seleccionar un local para consultar productos.")
            return
        }

        when (val resultado = productoRepository.obtenerProductosPorLocal(idLocal)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarProductos(resultado.data)
        }
    }

    private fun mostrarProductos(productos: List<Producto>) {
        val contenedorProductos = findViewById<LinearLayout>(R.id.contenedorProductos)
        val tvProductosVacios = findViewById<TextView>(R.id.tvProductosVacios)

        contenedorProductos.removeAllViews()
        tvProductosVacios.visibility = if (productos.isEmpty()) TextView.VISIBLE else TextView.GONE

        productos.forEach { producto ->
            contenedorProductos.addView(crearTarjetaProducto(producto))
        }
    }

    private fun crearTarjetaProducto(producto: Producto): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(10).toFloat()
            cardElevation = dpToPx(3).toFloat()
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
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
        }

        val imagenProducto = ImageView(this).apply {
            ImageViewLoader.cargarImagen(this, producto.imagenUri, R.drawable.logo_ues)
            contentDescription = "Imagen del producto ${producto.nombreProducto}"
            layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(80))
            scaleType = ImageView.ScaleType.CENTER_CROP
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

        informacion.addView(crearFilaNombrePrecio(producto))
        informacion.addView(crearTexto("Estado: ${producto.disponibilidad}", 13f))
        informacion.addView(crearTexto("Tipo: ${producto.tipo}", 13f))
        informacion.addView(crearTexto("Stock: ${producto.stock}", 13f))
        informacion.addView(crearBotonAgregar(producto))

        contenido.addView(imagenProducto)
        contenido.addView(informacion)
        cardView.addView(contenido)

        return cardView
    }

    private fun crearFilaNombrePrecio(producto: Producto): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val nombreProducto = crearTexto(producto.nombreProducto, 16f, true).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val precio = crearTexto(formatearPrecio(producto.precio), 16f, true)

        fila.addView(nombreProducto)
        fila.addView(precio)

        return fila
    }

    private fun crearBotonAgregar(producto: Producto): Button {
        val estaDisponible = producto.disponibilidad == AppConstants.DISPONIBILIDAD_DISPONIBLE &&
            producto.stock > 0

        return Button(this).apply {
            text = if (estaDisponible) "Agregar" else "No disponible"
            isEnabled = estaDisponible
            setTextColor(Color.WHITE)
            setBackgroundColor(if (estaDisponible) getColor(R.color.wine) else Color.GRAY)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }
            setOnClickListener {
                agregarAlCarrito(producto)
            }
        }
    }

    private fun agregarAlCarrito(producto: Producto) {
        when (val resultado = CarritoManager.agregarProducto(producto, nombreLocal)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarMensaje("${resultado.data.producto.nombreProducto} agregado al carrito.")
            }
        }
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = texto
            this.textSize = textSize
            if (negrita) {
                setTypeface(typeface, Typeface.BOLD)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }
    }

    private fun formatearPrecio(precio: Double): String {
        return String.format(Locale.US, "$%.2f", precio)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
