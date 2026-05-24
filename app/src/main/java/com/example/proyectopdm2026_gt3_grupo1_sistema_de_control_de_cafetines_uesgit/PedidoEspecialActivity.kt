package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PagoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoEspecialLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PagoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoEspecialRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.PedidoEspecial
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.PedidoEspecialValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.DateUtils
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.SessionManager
import java.util.Calendar
import java.util.Locale

class PedidoEspecialActivity : AppCompatActivity() {
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var pedidoEspecialRepository: PedidoEspecialRepository
    private lateinit var pagoRepository: PagoRepository
    private lateinit var localRepository: LocalRepository
    private lateinit var productoRepository: ProductoRepository
    private lateinit var sessionManager: SessionManager
    private var locales: List<Local> = emptyList()
    private var productos: List<Producto> = emptyList()
    private val productosSeleccionados = mutableListOf<ProductoEventoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedido_especial)
        configurarDependencias()
        configurarSelectoresFechaHora()
        configurarProductos()
        cargarLocales()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnSolicitar).setOnClickListener {
            registrarPedidoEspecial()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarSelectoresFechaHora() {
        findViewById<EditText>(R.id.etFechaEvento).setOnClickListener {
            mostrarSelectorFecha()
        }
        findViewById<EditText>(R.id.etHoraEvento).setOnClickListener {
            mostrarSelectorHora()
        }
    }

    private fun mostrarSelectorFecha() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                findViewById<EditText>(R.id.etFechaEvento).setText(fecha)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarSelectorHora() {
        val calendario = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val hora = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
                findViewById<EditText>(R.id.etHoraEvento).setText(hora)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        pedidoRepository = PedidoRepository(PedidoLocalDataSource(databaseHelper))
        pedidoEspecialRepository = PedidoEspecialRepository(PedidoEspecialLocalDataSource(databaseHelper))
        pagoRepository = PagoRepository(PagoLocalDataSource(databaseHelper))
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        sessionManager = SessionManager(this)
    }

    private fun configurarProductos() {
        findViewById<Spinner>(R.id.spLocalEvento).onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    cargarProductosPorLocal()
                    productosSeleccionados.clear()
                    actualizarResumenProductos()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }

        findViewById<Button>(R.id.btnAgregarProductoEvento).setOnClickListener {
            agregarProductoEvento()
        }
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocalesActivos()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                locales = resultado.data
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locales.map { it.nombreLocal })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spLocalEvento).adapter = adapter
                cargarProductosPorLocal()
            }
        }
    }

    private fun cargarProductosPorLocal() {
        val local = obtenerLocalSeleccionado()
        if (local == null) {
            productos = emptyList()
            configurarSpinnerProductos()
            return
        }

        when (val resultado = productoRepository.obtenerProductosPorLocal(local.idLocal)) {
            is OperationResult.Error -> {
                mostrarMensaje(resultado.message)
                productos = emptyList()
            }
            is OperationResult.Success -> {
                productos = resultado.data.filter {
                    it.disponibilidad == AppConstants.DISPONIBILIDAD_DISPONIBLE && it.stock > 0
                }
            }
        }
        configurarSpinnerProductos()
    }

    private fun configurarSpinnerProductos() {
        val nombres = if (productos.isEmpty()) {
            listOf("No hay productos disponibles")
        } else {
            productos.map { "${it.nombreProducto} - ${formatearPrecio(it.precio)}" }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spProductoEvento).adapter = adapter
    }

    private fun agregarProductoEvento() {
        val producto = productos.getOrNull(findViewById<Spinner>(R.id.spProductoEvento).selectedItemPosition)
        if (producto == null) {
            mostrarMensaje("Debe seleccionar un producto disponible.")
            return
        }

        val cantidad = obtenerTexto(R.id.etCantidadProductoEvento).toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            mostrarMensaje("La cantidad del producto debe ser mayor a cero.")
            return
        }
        if (cantidad > producto.stock) {
            mostrarMensaje("No hay stock suficiente para ${producto.nombreProducto}.")
            return
        }

        val index = productosSeleccionados.indexOfFirst { it.producto.idProducto == producto.idProducto }
        if (index >= 0) {
            val itemActual = productosSeleccionados[index]
            val nuevaCantidad = itemActual.cantidad + cantidad
            if (nuevaCantidad > producto.stock) {
                mostrarMensaje("No hay stock suficiente para ${producto.nombreProducto}.")
                return
            }
            productosSeleccionados[index] = itemActual.copy(cantidad = nuevaCantidad)
        } else {
            productosSeleccionados.add(ProductoEventoItem(producto, cantidad))
        }

        findViewById<EditText>(R.id.etCantidadProductoEvento).text.clear()
        actualizarResumenProductos()
    }

    private fun registrarPedidoEspecial() {
        if (!sessionManager.haySesionActiva()) {
            mostrarMensaje("Debe iniciar sesión para registrar un pedido especial.")
            return
        }

        val descripcionEvento = obtenerTexto(R.id.etTipoEvento)
        val fechaEvento = obtenerTexto(R.id.etFechaEvento)
        val horaEvento = obtenerTexto(R.id.etHoraEvento).ifBlank { null }
        val numeroPersonas = obtenerTexto(R.id.etCantidadPersonas).toIntOrNull()
        val presupuesto = obtenerTexto(R.id.etPresupuesto).toDoubleOrNull()
        val anticipo = obtenerTexto(R.id.etAnticipo).toDoubleOrNull()
        val totalProductos = calcularTotalProductos()

        val descripcionCompleta = "$descripcionEvento. Productos: ${crearResumenProductosTexto()}"

        val error = PedidoEspecialValidator.validarPedidoEspecial(
            descripcionEvento = descripcionCompleta,
            fechaEvento = fechaEvento,
            numeroPersonas = numeroPersonas,
            presupuesto = presupuesto,
            totalProductos = totalProductos,
            montoMinimo = MONTO_MINIMO,
            montoMaximo = MONTO_MAXIMO,
            anticipo = anticipo
        )
        if (error != null) {
            mostrarMensaje(error)
            return
        }

        val pedido = Pedido(
            fechaPedido = DateUtils.obtenerFechaHoraActual(),
            tipoPedido = TIPO_PEDIDO_ESPECIAL,
            estadoPedido = AppConstants.ESTADO_PEDIDO_PAGADO,
            total = totalProductos,
            idUsuario = sessionManager.obtenerIdUsuario(),
            idUbicacion = sessionManager.obtenerIdUbicacion()
        )
        val detalles = crearDetallesPedido()

        when (val resultadoPedido = pedidoRepository.crearPedido(pedido, detalles)) {
            is OperationResult.Error -> mostrarMensaje(resultadoPedido.message)
            is OperationResult.Success -> registrarDetalleEspecial(
                idPedido = resultadoPedido.data.toInt(),
                descripcionEvento = descripcionCompleta,
                fechaEvento = fechaEvento,
                horaEvento = horaEvento,
                numeroPersonas = numeroPersonas ?: 0,
                totalProductos = totalProductos,
                anticipo = anticipo ?: 0.0
            )
        }
    }

    private fun registrarDetalleEspecial(
        idPedido: Int,
        descripcionEvento: String,
        fechaEvento: String,
        horaEvento: String?,
        numeroPersonas: Int,
        totalProductos: Double,
        anticipo: Double
    ) {
        val referenciaPago = "ANTICIPO-ESPECIAL-${System.currentTimeMillis()}"
        val pedidoEspecial = PedidoEspecial(
            idPedido = idPedido,
            descripcionEvento = descripcionEvento,
            fechaEvento = fechaEvento,
            horaEvento = horaEvento,
            numeroPersonas = numeroPersonas,
            montoMinimo = MONTO_MINIMO,
            montoMaximo = MONTO_MAXIMO,
            anticipo = anticipo,
            referenciaPago = referenciaPago
        )

        when (val resultadoEspecial = pedidoEspecialRepository.registrarPedidoEspecial(pedidoEspecial)) {
            is OperationResult.Error -> mostrarMensaje(resultadoEspecial.message)
            is OperationResult.Success -> registrarAnticipo(idPedido, anticipo, referenciaPago)
        }
    }

    private fun registrarAnticipo(idPedido: Int, anticipo: Double, referenciaPago: String) {
        val pago = Pago(
            idPedido = idPedido,
            metodoPago = AppConstants.METODO_PAGO_EFECTIVO,
            monto = anticipo,
            fechaPago = DateUtils.obtenerFechaHoraActual(),
            referencia = referenciaPago,
            estadoPago = AppConstants.ESTADO_PAGO_CONFIRMADO
        )

        when (val resultadoPago = pagoRepository.registrarPago(pago)) {
            is OperationResult.Error -> mostrarMensaje(resultadoPago.message)
            is OperationResult.Success -> {
                mostrarMensaje("Pedido especial registrado con anticipo.")
                limpiarFormulario()
            }
        }
    }

    private fun limpiarFormulario() {
        listOf(
            R.id.etTipoEvento,
            R.id.etFechaEvento,
            R.id.etHoraEvento,
            R.id.etCantidadPersonas,
            R.id.etPresupuesto,
            R.id.etCantidadProductoEvento,
            R.id.etAnticipo
        ).forEach { id -> findViewById<EditText>(id).text.clear() }
        productosSeleccionados.clear()
        actualizarResumenProductos()
    }

    private fun crearDetallesPedido(): List<DetallePedido> {
        return productosSeleccionados.map { item ->
            DetallePedido(
                idPedido = 0,
                idProducto = item.producto.idProducto,
                cantidad = item.cantidad,
                precioUnitario = item.producto.precio,
                subtotal = item.subtotal
            )
        }
    }

    private fun actualizarResumenProductos() {
        val resumen = if (productosSeleccionados.isEmpty()) {
            "No hay productos seleccionados."
        } else {
            productosSeleccionados.joinToString("\n") { item ->
                "${item.producto.nombreProducto} x${item.cantidad} = ${formatearPrecio(item.subtotal)}"
            }
        }
        findViewById<TextView>(R.id.tvProductosSeleccionados).text = resumen
        findViewById<TextView>(R.id.tvTotalProductosEvento).text =
            "Total productos: ${formatearPrecio(calcularTotalProductos())}"
    }

    private fun crearResumenProductosTexto(): String {
        if (productosSeleccionados.isEmpty()) return "Sin productos"
        return productosSeleccionados.joinToString(", ") { item ->
            "${item.producto.nombreProducto} x${item.cantidad}"
        }
    }

    private fun calcularTotalProductos(): Double {
        return productosSeleccionados.sumOf { it.subtotal }
    }

    private fun obtenerLocalSeleccionado(): Local? {
        return locales.getOrNull(findViewById<Spinner>(R.id.spLocalEvento).selectedItemPosition)
    }

    private fun formatearPrecio(precio: Double): String {
        return String.format(Locale.US, "$%.2f", precio)
    }

    private fun obtenerTexto(id: Int): String {
        return findViewById<EditText>(id).text.toString().trim()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private companion object {
        const val MONTO_MINIMO = 25.0
        const val MONTO_MAXIMO = 200.0
        const val TIPO_PEDIDO_ESPECIAL = "Especial"
    }

    private data class ProductoEventoItem(
        val producto: Producto,
        val cantidad: Int
    ) {
        val subtotal: Double
            get() = producto.precio * cantidad
    }
}
