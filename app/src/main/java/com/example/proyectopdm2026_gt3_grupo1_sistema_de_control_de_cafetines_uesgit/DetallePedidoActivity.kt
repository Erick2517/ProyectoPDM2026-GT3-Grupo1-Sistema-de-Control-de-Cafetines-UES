package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PagoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PagoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Locale

class DetallePedidoActivity : AppCompatActivity() {
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var productoRepository: ProductoRepository
    private lateinit var pagoRepository: PagoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_pedido)

        configurarDependencias()
        cargarDetallePedido()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
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
        pedidoRepository = PedidoRepository(PedidoLocalDataSource(databaseHelper))
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        pagoRepository = PagoRepository(PagoLocalDataSource(databaseHelper))
    }

    private fun cargarDetallePedido() {
        val idPedido = intent.getLongExtra(AppConstants.EXTRA_ID_PEDIDO, 0L).toInt()
        if (idPedido <= 0) {
            mostrarMensaje("No se recibió un pedido válido.")
            return
        }

        when (val resultado = pedidoRepository.obtenerPedidoPorId(idPedido)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarPedido(resultado.data)
                cargarProductos(idPedido)
                cargarPago(idPedido)
            }
        }
    }

    private fun mostrarPedido(pedido: Pedido) {
        findViewById<TextView>(R.id.tvNumeroPedidoDetalle).text = "Pedido #${pedido.idPedido}"
        findViewById<TextView>(R.id.tvTotalPedidoDetalle).text = formatearPrecio(pedido.total)
        findViewById<TextView>(R.id.tvTipoPedidoDetalle).text = "Tipo: ${pedido.tipoPedido}"
        findViewById<TextView>(R.id.tvFechaPedidoDetalle).text = "Fecha: ${pedido.fechaPedido}"
        findViewById<TextView>(R.id.tvEstadoPedidoDetalle).apply {
            text = pedido.estadoPedido
            setBackgroundResource(obtenerFondoEstado(pedido.estadoPedido))
        }
        findViewById<TextView>(R.id.tvTipoEntregaDetalle).text = pedido.tipoPedido
        findViewById<TextView>(R.id.tvSubtotalDetalle).text = formatearPrecio(pedido.total)
        findViewById<TextView>(R.id.tvCostoEntregaDetalle).text = formatearPrecio(0.0)
        findViewById<TextView>(R.id.tvTotalResumenDetalle).text = formatearPrecio(pedido.total)
    }

    private fun cargarProductos(idPedido: Int) {
        when (val resultado = pedidoRepository.obtenerDetallesPorPedido(idPedido)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarDetalles(resultado.data)
        }
    }

    private fun mostrarDetalles(detalles: List<DetallePedido>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorProductosDetalle)
        val tvVacio = findViewById<TextView>(R.id.tvProductosVaciosDetalle)

        contenedor.removeAllViews()
        if (detalles.isEmpty()) {
            tvVacio.visibility = TextView.VISIBLE
            contenedor.addView(tvVacio)
            return
        }

        tvVacio.visibility = TextView.GONE
        detalles.forEach { detalle ->
            contenedor.addView(crearTarjetaDetalle(detalle))
        }
    }

    private fun crearTarjetaDetalle(detalle: DetallePedido): CardView {
        val cardView = CardView(this).apply {
            radius = dpToPx(8).toFloat()
            cardElevation = dpToPx(1).toFloat()
            useCompatPadding = true
            setCardBackgroundColor(getColor(android.R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }

        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))
        }

        val nombreProducto = obtenerNombreProducto(detalle.idProducto)
        val producto = TextView(this).apply {
            text = "$nombreProducto x${detalle.cantidad}"
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val subtotal = TextView(this).apply {
            text = formatearPrecio(detalle.subtotal)
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#333333"))
        }

        fila.addView(producto)
        fila.addView(subtotal)
        cardView.addView(fila)
        return cardView
    }

    private fun obtenerNombreProducto(idProducto: Int): String {
        return when (val resultado = productoRepository.obtenerProductoPorId(idProducto)) {
            is OperationResult.Error -> "Producto #$idProducto"
            is OperationResult.Success -> resultado.data.nombreProducto
        }
    }

    private fun cargarPago(idPedido: Int) {
        when (val resultado = pagoRepository.obtenerPagosPorPedido(idPedido)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarPago(resultado.data.firstOrNull())
        }
    }

    private fun mostrarPago(pago: Pago?) {
        val tvMetodoPago = findViewById<TextView>(R.id.tvMetodoPagoDetalle)
        val tvReferenciaPago = findViewById<TextView>(R.id.tvReferenciaPagoDetalle)

        if (pago == null) {
            tvMetodoPago.text = "Sin pago registrado"
            tvReferenciaPago.text = ""
            return
        }

        tvMetodoPago.text = "${pago.metodoPago} - ${pago.estadoPago}"
        tvReferenciaPago.text = if (pago.referencia.isNullOrBlank()) {
            "Fecha de pago: ${pago.fechaPago}"
        } else {
            "Referencia: ${pago.referencia}"
        }
    }

    private fun obtenerFondoEstado(estadoPedido: String): Int {
        return when (estadoPedido) {
            AppConstants.ESTADO_PEDIDO_PAGADO,
            AppConstants.ESTADO_PEDIDO_ENTREGADO -> R.drawable.bg_badge_green
            AppConstants.ESTADO_PEDIDO_PENDIENTE,
            AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO -> R.drawable.bg_badge_orange
            AppConstants.ESTADO_PEDIDO_CANCELADO -> R.drawable.bg_badge_gray
            else -> R.drawable.bg_badge_blue
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
