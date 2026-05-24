package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.graphics.Typeface
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.SessionManager
import java.util.Locale

class MisPedidosActivity : AppCompatActivity() {
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_pedidos)

        configurarDependencias()

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
        sessionManager = SessionManager(this)
    }

    private fun cargarPedidos() {
        if (!sessionManager.haySesionActiva()) {
            mostrarMensaje("Debe iniciar sesión para consultar sus pedidos.")
            mostrarPedidos(emptyList())
            return
        }

        when (val resultado = pedidoRepository.obtenerPedidosPorUsuario(sessionManager.obtenerIdUsuario())) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                mostrarPedidos(emptyList())
            }
            is OperationResult.Success -> mostrarPedidos(resultado.data)
        }
    }

    private fun mostrarPedidos(pedidos: List<Pedido>) {
        val contenedorPedidos = findViewById<LinearLayout>(R.id.contenedorPedidos)
        val tvPedidosVacios = findViewById<TextView>(R.id.tvPedidosVacios)

        contenedorPedidos.removeAllViews()
        if (pedidos.isEmpty()) {
            tvPedidosVacios.visibility = TextView.VISIBLE
            return
        }
        tvPedidosVacios.visibility = TextView.GONE

        pedidos.forEach { pedido ->
            contenedorPedidos.addView(crearTarjetaPedido(pedido))
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

        contenido.addView(crearTexto("Pedido #${pedido.idPedido}", 16f, true, "#111111"))
        contenido.addView(crearTexto("Tipo: ${pedido.tipoPedido}", 13f, false, "#555555"))
        contenido.addView(crearFilaFechaEstado(pedido))
        contenido.addView(crearFilaTotalDetalle(pedido))

        cardView.addView(contenido)
        return cardView
    }

    private fun crearFilaFechaEstado(pedido: Pedido): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(6)
            }
        }

        val fecha = crearTexto("Fecha: ${pedido.fechaPedido}", 12f, false, "#555555").apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val estado = crearBadgeEstado(pedido.estadoPedido)

        fila.addView(fecha)
        fila.addView(estado)
        return fila
    }

    private fun crearFilaTotalDetalle(pedido: Pedido): LinearLayout {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }
        }

        val total = crearTexto("Total: ${formatearPrecio(pedido.total)}", 13f, true, "#111111").apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val detalle = crearBotonDetalle(pedido.idPedido)

        fila.addView(total)
        if (pedido.estadoPedido.equals(AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO, ignoreCase = true) ||
            pedido.estadoPedido.equals(AppConstants.ESTADO_PEDIDO_PENDIENTE, ignoreCase = true)) {

            val botonPagar = TextView(this).apply {
                text = "Pagar"
                textSize = 12f
                setTextColor(getColor(android.R.color.white))
                setBackgroundResource(R.drawable.bg_badge_orange) // Tu fondo naranja
                setPadding(dpToPx(14), dpToPx(5), dpToPx(14), dpToPx(5))

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    rightMargin = dpToPx(8) // Espacio para que no se pegue al botón detalle
                }

                setOnClickListener {
                    // Creamos el Intent para abrir PagoActivity
                    val intent = Intent(this@MisPedidosActivity, PagoActivity::class.java).apply {
                        // 1. Usamos la constante exacta y lo convertimos a Long (.toLong())
                        putExtra(AppConstants.EXTRA_ID_PEDIDO, pedido.idPedido.toLong())

                        // 2. Pasamos el total
                        putExtra("EXTRA_TOTAL_PAGAR", pedido.total)

                        putExtra("VIENE_DE_MIS_PEDIDOS", true)
                    }
                    startActivity(intent)
                }
            }
            fila.addView(botonPagar) // Lo agregamos a la fila
        }

        fila.addView(detalle)
        return fila
    }

    private fun crearBotonDetalle(idPedido: Int): TextView {
        return TextView(this).apply {
            text = "Ver detalle"
            textSize = 12f
            setTextColor(getColor(android.R.color.black))
            setBackgroundResource(R.drawable.bg_btn_outline)
            setPadding(dpToPx(14), dpToPx(5), dpToPx(14), dpToPx(5))
            setOnClickListener {
                val intent = Intent(this@MisPedidosActivity, DetallePedidoActivity::class.java).apply {
                    putExtra(AppConstants.EXTRA_ID_PEDIDO, idPedido.toLong())
                }
                startActivity(intent)
            }
        }
    }

    private fun crearBadgeEstado(estadoPedido: String): TextView {
        return TextView(this).apply {
            text = estadoPedido
            textSize = 11f
            setTextColor(getColor(android.R.color.white))
            setBackgroundResource(obtenerFondoEstado(estadoPedido))
            setPadding(dpToPx(10), dpToPx(3), dpToPx(10), dpToPx(3))
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
