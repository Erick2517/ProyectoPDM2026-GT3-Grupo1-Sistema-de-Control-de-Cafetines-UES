package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.CarritoItem
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.CarritoManager
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Locale

class CarritoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrito)

        mostrarCarrito()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPedido)
        btnConfirmar.setOnClickListener {
            continuarAlPago()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarCarrito() {
        val items = CarritoManager.obtenerItems()
        val contenedorCarrito = findViewById<LinearLayout>(R.id.contenedorCarrito)
        val tvCarritoVacio = findViewById<TextView>(R.id.tvCarritoVacio)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPedido)

        findViewById<TextView>(R.id.tvNombreLocalCarrito).text =
            CarritoManager.obtenerNombreLocal() ?: "Carrito"

        contenedorCarrito.removeAllViews()
        tvCarritoVacio.visibility = if (items.isEmpty()) TextView.VISIBLE else TextView.GONE
        btnConfirmar.isEnabled = items.isNotEmpty()

        items.forEach { item ->
            contenedorCarrito.addView(crearTarjetaItem(item))
        }

        actualizarTotal()
    }

    private fun crearTarjetaItem(item: CarritoItem): CardView {
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
            setImageResource(R.drawable.logo_ues)
            contentDescription = "Imagen del producto ${item.producto.nombreProducto}"
            layoutParams = LinearLayout.LayoutParams(dpToPx(90), dpToPx(76))
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

        informacion.addView(crearFilaNombreSubtotal(item))
        informacion.addView(crearTexto("Precio: ${formatearPrecio(item.producto.precio)}", 13f))
        informacion.addView(crearTexto("Cantidad: ${item.cantidad}", 13f))
        informacion.addView(crearControlesCantidad(item))

        contenido.addView(imagenProducto)
        contenido.addView(informacion)
        cardView.addView(contenido)

        return cardView
    }

    private fun crearFilaNombreSubtotal(item: CarritoItem): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val nombre = crearTexto(item.producto.nombreProducto, 16f, true).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val subtotal = crearTexto(formatearPrecio(item.subtotal), 16f, true)

        fila.addView(nombre)
        fila.addView(subtotal)
        return fila
    }

    private fun crearControlesCantidad(item: CarritoItem): LinearLayout {
        val controles = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }
        }

        val btnDisminuir = crearBotonControl("-", dpToPx(44)).apply {
            setOnClickListener {
                when (val resultado = CarritoManager.disminuirCantidad(item.producto.idProducto)) {
                    is OperationResult.Error -> mostrarMensaje(resultado.message)
                    is OperationResult.Success -> mostrarCarrito()
                }
            }
        }

        val btnAumentar = crearBotonControl("+", dpToPx(44)).apply {
            setOnClickListener {
                when (val resultado = CarritoManager.aumentarCantidad(item.producto.idProducto)) {
                    is OperationResult.Error -> mostrarMensaje(resultado.message)
                    is OperationResult.Success -> mostrarCarrito()
                }
            }
        }

        val spacer = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val btnQuitar = crearBotonControl("Quitar", dpToPx(96)).apply {
            setOnClickListener {
                CarritoManager.quitarProducto(item.producto.idProducto)
                mostrarCarrito()
            }
        }

        controles.addView(btnDisminuir)
        controles.addView(btnAumentar)
        controles.addView(spacer)
        controles.addView(btnQuitar)

        return controles
    }

    private fun crearBotonControl(texto: String, ancho: Int): Button {
        return Button(this).apply {
            text = texto
            isSingleLine = true
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            includeFontPadding = false
            setPadding(dpToPx(10), 0, dpToPx(10), 0)
            setBackgroundColor(getColor(R.color.wine))
            setTextColor(getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                ancho,
                dpToPx(40)
            ).apply {
                marginEnd = dpToPx(8)
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

    private fun actualizarTotal() {
        findViewById<TextView>(R.id.tvTotalPagar).text = formatearPrecio(CarritoManager.calcularTotal())
    }

    private fun continuarAlPago() {
        if (CarritoManager.estaVacio()) {
            mostrarMensaje("No se puede continuar con un carrito vacío.")
            return
        }

        mostrarMensaje("El registro del pedido en SQLite se implementará en la siguiente parte de RF03.")
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
