package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PagoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.PagoValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.DateUtils
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import java.util.Calendar
import java.util.Locale

class PagoActivity : AppCompatActivity() {
    private lateinit var pagoRepository: PagoRepository
    private lateinit var pedidoRepository: PedidoRepository
    private var pedidoActual: Pedido? = null
    private var metodoPagoSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        configurarDependencias()
        configurarMetodosPago()
        cargarPedido()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        btnBack.setOnClickListener {
            finish()
        }

        btnConfirmar.setOnClickListener {
            confirmarPago()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        pagoRepository = PagoRepository(PagoLocalDataSource(databaseHelper))
        pedidoRepository = PedidoRepository(PedidoLocalDataSource(databaseHelper))
    }

    private fun configurarMetodosPago() {
        findViewById<CardView>(R.id.cardEfectivo).setOnClickListener {
            seleccionarMetodoPago(AppConstants.METODO_PAGO_EFECTIVO)
        }
        findViewById<CardView>(R.id.cardTarjeta).setOnClickListener {
            seleccionarMetodoPago(AppConstants.METODO_PAGO_TARJETA)
        }
        findViewById<CardView>(R.id.cardBitcoin).setOnClickListener {
            seleccionarMetodoPago(AppConstants.METODO_PAGO_BITCOIN)
        }
    }

    private fun cargarPedido() {
        val idPedido = intent.getLongExtra(AppConstants.EXTRA_ID_PEDIDO, 0L).toInt()
        if (idPedido <= 0) {
            bloquearPago("No se recibió un pedido válido para pagar.")
            return
        }

        when (val resultado = pedidoRepository.obtenerPedidoPorId(idPedido)) {
            is OperationResult.Error -> bloquearPago(resultado.message)
            is OperationResult.Success -> mostrarPedido(resultado.data)
        }
    }

    private fun mostrarPedido(pedido: Pedido) {
        pedidoActual = pedido

        findViewById<TextView>(R.id.tvNumeroPedidoPago).text = "Pedido #${pedido.idPedido}"
        findViewById<TextView>(R.id.tvTotalPedidoPago).text = "Total a pagar: ${formatearPrecio(pedido.total)}"
        findViewById<TextView>(R.id.tvSubtotalPago).text = formatearPrecio(pedido.total)
        findViewById<TextView>(R.id.tvCostoEntregaPago).text = formatearPrecio(0.0)
        findViewById<TextView>(R.id.tvTotalResumenPago).text = formatearPrecio(pedido.total)
        findViewById<EditText>(R.id.txtMontoPago).setText(String.format(Locale.US, "%.2f", pedido.total))

        if (pedido.estadoPedido != AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO) {
            bloquearPago("El pedido no se encuentra pendiente de pago.")
        }
    }

    private fun seleccionarMetodoPago(metodoPago: String) {
        metodoPagoSeleccionado = metodoPago
        pintarMetodoPago(
            cardId = R.id.cardEfectivo,
            labelId = R.id.tvMetodoEfectivo,
            seleccionado = metodoPago == AppConstants.METODO_PAGO_EFECTIVO
        )
        pintarMetodoPago(
            cardId = R.id.cardTarjeta,
            labelId = R.id.tvMetodoTarjeta,
            seleccionado = metodoPago == AppConstants.METODO_PAGO_TARJETA
        )
        pintarMetodoPago(
            cardId = R.id.cardBitcoin,
            labelId = R.id.tvMetodoBitcoin,
            seleccionado = metodoPago == AppConstants.METODO_PAGO_BITCOIN
        )
    }

    private fun pintarMetodoPago(cardId: Int, labelId: Int, seleccionado: Boolean) {
        val colorFondo = if (seleccionado) getColor(R.color.wine) else getColor(android.R.color.white)
        val colorTexto = if (seleccionado) getColor(android.R.color.white) else getColor(android.R.color.black)

        findViewById<CardView>(cardId).setCardBackgroundColor(colorFondo)
        findViewById<TextView>(labelId).setTextColor(colorTexto)
    }

    private fun confirmarPago() {
        val pedido = pedidoActual
        if (pedido == null) {
            mostrarMensaje("No hay un pedido cargado para registrar el pago.")
            return
        }

        val monto = findViewById<EditText>(R.id.txtMontoPago).text.toString().trim().toDoubleOrNull()

        // 1. VALIDACIÓN DE MONTO MÍNIMO
        if (monto == null || monto <= 0.05) { // Evita montos vacíos, en cero o negativos
            mostrarMensaje("El monto ingresado debe ser mayor a $0.05")
            return
        }

        // 2. VALIDACIÓN DE HORARIO DE ANTOJITOS (2:00 p.m. a 4:00 p.m.)
       /* val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (horaActual < 14 || horaActual >= 16) {
            mostrarMensaje("Los antojitos solo se pueden pagar entre las 2:00 p.m. y las 4:00 p.m.")
            return
        }*/

        // 3. VALIDACIÓN DE CANTIDAD MÁXIMA (Máximo 3 unidades en total)
        val totalUnidades = 1
        if (totalUnidades > 3) {
            mostrarMensaje("Solo puedes pedir un máximo de 3 antojitos.")
            return
        }

        val errorValidacion = PagoValidator.validarPago(
            idPedido = pedido.idPedido,
            metodoPago = metodoPagoSeleccionado,
            monto = monto
        )

        if (errorValidacion != null) {
            mostrarMensaje(errorValidacion)
            return
        }

        when (metodoPagoSeleccionado) {
            AppConstants.METODO_PAGO_EFECTIVO -> {
                ejecutarGuardadoRealEnBase(monto)
            }
            AppConstants.METODO_PAGO_TARJETA -> {
                mostrarDialogoTarjetaSimulado(monto)
            }
            AppConstants.METODO_PAGO_BITCOIN -> {
                mostrarDialogoBitcoinSimulado(monto)
            }
        }
    }

    private fun mostrarDialogoTarjetaSimulado(monto: Double?) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Pago con Tarjeta Crédito/Débito")

        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputTarjeta = EditText(this).apply { hint = "Número de Tarjeta (16 dígitos)" }
        val inputVencimiento = EditText(this).apply { hint = "MM/AA" }
        val inputCVV = EditText(this).apply { hint = "CVV (3 dígitos)" }

        contenedor.addView(inputTarjeta)
        contenedor.addView(inputVencimiento)
        contenedor.addView(inputCVV)
        builder.setView(contenedor)

        builder.setPositiveButton("Proceder Pago") { dialog, _ ->
            if (inputTarjeta.text.toString().length < 16 || inputCVV.text.toString().length < 3) {
                mostrarMensaje("Datos de tarjeta inválidos. Simulación cancelada.")
            } else {
                dialog.dismiss()
                ejecutarGuardadoRealEnBase(monto)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoBitcoinSimulado(monto: Double?) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Pago con Bitcoin (Simulado)")

        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val txtWalletIndicacion = TextView(this).apply {
            text = "Transfiere el equivalente a la siguiente dirección de la Wallet del Cafetín UES:\n\nbc1qxy2kgdygjrsqtzq2n0yrf249"
            textSize = 14f
        }
        val inputTransaccion = EditText(this).apply { hint = "ID de Transacción / Hash simulado" }

        contenedor.addView(txtWalletIndicacion)
        contenedor.addView(inputTransaccion)
        builder.setView(contenedor)

        builder.setPositiveButton("Verificar Transferencia") { dialog, _ ->
            if (inputTransaccion.text.toString().isEmpty()) {
                mostrarMensaje("Debe ingresar el hash de la transacción simulada.")
            } else {
                dialog.dismiss()
                ejecutarGuardadoRealEnBase(monto)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun ejecutarGuardadoRealEnBase(monto: Double?) {
        val pedido = pedidoActual ?: return

        val pago = Pago(
            idPedido = pedido.idPedido,
            metodoPago = metodoPagoSeleccionado,
            monto = monto ?: 0.0,
            fechaPago = DateUtils.obtenerFechaHoraActual(),
            referencia = crearReferenciaSimulada(metodoPagoSeleccionado),
            estadoPago = AppConstants.ESTADO_PAGO_REGISTRADO
        )

        when (val resultado = pagoRepository.registrarPagoConfirmado(pago)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> abrirConfirmacionPago()
        }
    }

    private fun crearReferenciaSimulada(metodoPago: String): String? {
        return when (metodoPago) {
            AppConstants.METODO_PAGO_TARJETA,
            AppConstants.METODO_PAGO_BITCOIN -> "SIM-${metodoPago.uppercase(Locale.ROOT)}-${System.currentTimeMillis()}"
            else -> null
        }
    }

    private fun abrirConfirmacionPago() {
        val intent = Intent(this, ConfirmarPagoActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun bloquearPago(mensaje: String) {
        mostrarMensaje(mensaje)
        findViewById<Button>(R.id.btnConfirmar).isEnabled = false
    }

    private fun formatearPrecio(precio: Double): String {
        return String.format(Locale.US, "$%.2f", precio)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
