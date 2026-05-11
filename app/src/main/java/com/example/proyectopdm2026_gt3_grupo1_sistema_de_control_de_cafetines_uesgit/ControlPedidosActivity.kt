package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Locale

class ControlPedidosActivity : AppCompatActivity() {
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var productoRepository: ProductoRepository
    private lateinit var localRepository: LocalRepository
    private var locales: List<Local> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_control_pedidos)

        configurarDependencias()
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

    override fun onResume() {
        super.onResume()
        cargarPedidos()
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        pedidoRepository = PedidoRepository(PedidoLocalDataSource(databaseHelper))
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocalesActivos()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                locales = resultado.data
                val nombresLocales = listOf("Todos los locales") + locales.map { it.nombreLocal }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresLocales)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                val spFiltro = findViewById<Spinner>(R.id.spFiltroLocalPedidos)
                spFiltro.adapter = adapter
                spFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        cargarPedidos()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                }
            }
        }
    }

    private fun cargarPedidos() {
        when (val resultado = pedidoRepository.obtenerPedidosOperativos()) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                mostrarPedidos(emptyList())
            }
            is OperationResult.Success -> mostrarPedidos(filtrarPedidosPorLocal(resultado.data))
        }
    }

    private fun filtrarPedidosPorLocal(pedidos: List<Pedido>): List<Pedido> {
        val idLocal = obtenerIdLocalFiltrado() ?: return pedidos
        return pedidos.filter { pedido -> pedidoPerteneceALocal(pedido.idPedido, idLocal) }
    }

    private fun obtenerIdLocalFiltrado(): Int? {
        val posicion = findViewById<Spinner>(R.id.spFiltroLocalPedidos).selectedItemPosition
        if (posicion <= 0) return null
        return locales.getOrNull(posicion - 1)?.idLocal
    }

    private fun pedidoPerteneceALocal(idPedido: Int, idLocal: Int): Boolean {
        val detalles = obtenerDetallesPedido(idPedido)
        return detalles.any { detalle ->
            obtenerProducto(detalle.idProducto)?.idLocal == idLocal
        }
    }

    private fun mostrarPedidos(pedidos: List<Pedido>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorControlPedidos)
        val tvVacio = findViewById<TextView>(R.id.tvPedidosControlVacios)

        contenedor.removeAllViews()
        if (pedidos.isEmpty()) {
            tvVacio.visibility = TextView.VISIBLE
            return
        }

        tvVacio.visibility = TextView.GONE
        pedidos.forEach { pedido ->
            contenedor.addView(crearTarjetaPedido(pedido))
        }
    }

    private fun crearTarjetaPedido(pedido: Pedido): CardView {
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

        contenido.addView(crearFilaEncabezado(pedido))
        contenido.addView(crearTexto("Local: ${obtenerNombreLocalPedido(pedido.idPedido)}", 13f, false, "#555555"))
        contenido.addView(crearTexto("Fecha: ${pedido.fechaPedido}", 12f, false, "#555555"))
        contenido.addView(crearTexto("Tipo: ${pedido.tipoPedido}", 13f, false, "#555555"))
        contenido.addView(crearTexto("Productos: ${obtenerResumenProductos(pedido.idPedido)}", 12f, false, "#555555"))
        contenido.addView(crearFilaEstado(pedido))
        contenido.addView(crearFilaAcciones(pedido))

        cardView.addView(contenido)
        return cardView
    }

    private fun crearFilaEncabezado(pedido: Pedido): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titulo = crearTexto("Pedido #${pedido.idPedido}", 16f, true, "#111111").apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val total = crearTexto(formatearPrecio(pedido.total), 13f, true, "#7B1A2E")

        fila.addView(titulo)
        fila.addView(total)
        return fila
    }

    private fun crearFilaEstado(pedido: Pedido): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(10)
            }
        }

        val estado = TextView(this).apply {
            text = pedido.estadoPedido
            textSize = 11f
            setTextColor(getColor(android.R.color.white))
            setBackgroundResource(obtenerFondoEstado(pedido.estadoPedido))
            setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
        }

        fila.addView(estado)
        return fila
    }

    private fun crearFilaAcciones(pedido: Pedido): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(12)
            }
        }

        fila.addView(crearBotonSecundario("Detalle") {
            val intent = Intent(this, DetallePedidoActivity::class.java).apply {
                putExtra(AppConstants.EXTRA_ID_PEDIDO, pedido.idPedido.toLong())
            }
            startActivity(intent)
        })

        val siguienteEstado = obtenerSiguienteEstado(pedido.estadoPedido)
        if (siguienteEstado != null) {
            fila.addView(crearBotonPrincipal(obtenerTextoAccion(siguienteEstado)) {
                actualizarEstadoPedido(pedido, siguienteEstado)
            })
        }

        if (puedeCancelarse(pedido.estadoPedido)) {
            fila.addView(crearBotonSecundario("Cancelar") {
                actualizarEstadoPedido(pedido, AppConstants.ESTADO_PEDIDO_CANCELADO)
            })
        }

        return fila
    }

    private fun crearBotonPrincipal(texto: String, accion: () -> Unit): Button {
        return crearBoton(texto, true, accion)
    }

    private fun crearBotonSecundario(texto: String, accion: () -> Unit): Button {
        return crearBoton(texto, false, accion)
    }

    private fun crearBoton(texto: String, principal: Boolean, accion: () -> Unit): Button {
        return Button(this).apply {
            text = texto
            isSingleLine = true
            textSize = 12f
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            includeFontPadding = false
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
                dpToPx(40),
                1f
            ).apply {
                marginEnd = dpToPx(8)
            }
            setOnClickListener { accion() }
        }
    }

    private fun actualizarEstadoPedido(pedido: Pedido, nuevoEstado: String) {
        when (
            val resultado = pedidoRepository.avanzarEstadoPedido(
                idPedido = pedido.idPedido,
                estadoActual = pedido.estadoPedido,
                nuevoEstado = nuevoEstado
            )
        ) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    mostrarMensaje("Estado actualizado a $nuevoEstado.")
                    cargarPedidos()
                } else {
                    mostrarMensaje("No se pudo actualizar el estado del pedido.")
                }
            }
        }
    }

    private fun obtenerResumenProductos(idPedido: Int): String {
        val detalles = obtenerDetallesPedido(idPedido)

        if (detalles.isEmpty()) return "Sin detalle"

        return detalles.joinToString(", ") { detalle ->
            "${obtenerNombreProducto(detalle)} x${detalle.cantidad}"
        }
    }

    private fun obtenerNombreProducto(detalle: DetallePedido): String {
        return obtenerProducto(detalle.idProducto)?.nombreProducto ?: "Producto #${detalle.idProducto}"
    }

    private fun obtenerDetallesPedido(idPedido: Int): List<DetallePedido> {
        return when (val resultado = pedidoRepository.obtenerDetallesPorPedido(idPedido)) {
            is OperationResult.Error -> emptyList()
            is OperationResult.Success -> resultado.data
        }
    }

    private fun obtenerProducto(idProducto: Int): Producto? {
        return when (val resultado = productoRepository.obtenerProductoPorId(idProducto)) {
            is OperationResult.Error -> null
            is OperationResult.Success -> resultado.data
        }
    }

    private fun obtenerNombreLocalPedido(idPedido: Int): String {
        val detalles = obtenerDetallesPedido(idPedido)
        val idLocal = detalles.firstNotNullOfOrNull { detalle -> obtenerProducto(detalle.idProducto)?.idLocal }
        return locales.firstOrNull { it.idLocal == idLocal }?.nombreLocal ?: "No identificado"
    }

    private fun obtenerSiguienteEstado(estadoActual: String): String? {
        return when (estadoActual) {
            AppConstants.ESTADO_PEDIDO_PAGADO -> AppConstants.ESTADO_PEDIDO_EN_PREPARACION
            AppConstants.ESTADO_PEDIDO_EN_PREPARACION -> AppConstants.ESTADO_PEDIDO_LISTO
            AppConstants.ESTADO_PEDIDO_LISTO -> AppConstants.ESTADO_PEDIDO_ENTREGADO
            else -> null
        }
    }

    private fun obtenerTextoAccion(nuevoEstado: String): String {
        return when (nuevoEstado) {
            AppConstants.ESTADO_PEDIDO_EN_PREPARACION -> "Preparar"
            AppConstants.ESTADO_PEDIDO_LISTO -> "Marcar listo"
            AppConstants.ESTADO_PEDIDO_ENTREGADO -> "Entregar"
            else -> nuevoEstado
        }
    }

    private fun puedeCancelarse(estadoActual: String): Boolean {
        return estadoActual == AppConstants.ESTADO_PEDIDO_PAGADO ||
            estadoActual == AppConstants.ESTADO_PEDIDO_EN_PREPARACION ||
            estadoActual == AppConstants.ESTADO_PEDIDO_LISTO
    }

    private fun obtenerFondoEstado(estadoPedido: String): Int {
        return when (estadoPedido) {
            AppConstants.ESTADO_PEDIDO_PAGADO,
            AppConstants.ESTADO_PEDIDO_ENTREGADO -> R.drawable.bg_badge_green
            AppConstants.ESTADO_PEDIDO_EN_PREPARACION,
            AppConstants.ESTADO_PEDIDO_PENDIENTE,
            AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO -> R.drawable.bg_badge_orange
            AppConstants.ESTADO_PEDIDO_CANCELADO -> R.drawable.bg_badge_gray
            else -> R.drawable.bg_badge_blue
        }
    }

    private fun crearTexto(texto: String, textSize: Float, negrita: Boolean, color: String): TextView {
        return TextView(this).apply {
            text = texto
            this.textSize = textSize
            setTextColor(android.graphics.Color.parseColor(color))
            if (negrita) {
                setTypeface(typeface, Typeface.BOLD)
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
