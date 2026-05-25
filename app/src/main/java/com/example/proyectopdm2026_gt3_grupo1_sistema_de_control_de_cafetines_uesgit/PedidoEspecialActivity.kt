package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit // Tu package real

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoEspecialLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoEspecialRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pedido
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.PedidoEspecial
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.SessionManager
import java.util.Calendar
import kotlin.math.round

class PedidoEspecialActivity : AppCompatActivity() {

    // 1. DECLARACIÓN DE COMPONENTES DE LA VISTA
    private lateinit var spCafetin: Spinner
    private lateinit var etTipoEvento: Spinner
    private lateinit var etFechaEvento: EditText
    private lateinit var etHoraEvento: EditText
    private lateinit var etCantidadPersonas: EditText
    private lateinit var etPresupuesto: EditText
    private lateinit var etProductos: EditText
    private lateinit var btnSolicitar: Button

    // 2. DECLARACIÓN DE REPOSITORIOS ARQUITECTÓNICOS
    private lateinit var pedidoRepository: PedidoRepository
    private lateinit var pedidoEspecialRepository: PedidoEspecialRepository
    private lateinit var productoRepository: ProductoRepository // El que integramos del grupo
    private lateinit var sessionManager: SessionManager
    private var productosElegidosObjetos = ArrayList<Producto>()

    // Variable global para controlar qué cafetín seleccionó el usuario
    private var idLocalSeleccionado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_especial)

        val databaseHelper = AppDatabaseHelper(this)

        // 3. INICIALIZACIÓN DE REPOSITORIOS (Capa de datos oculta)
        pedidoRepository = PedidoRepository(PedidoLocalDataSource(databaseHelper))
        pedidoEspecialRepository = PedidoEspecialRepository(PedidoEspecialLocalDataSource(databaseHelper))
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        sessionManager = SessionManager(this)

        // 4. ENLACE DE VISTAS CON EL XML (findViewById)
        spCafetin = findViewById(R.id.spCafetin) // Recuerda agregarlo a tu XML
        etTipoEvento = findViewById(R.id.spTipoEvento)
        etFechaEvento = findViewById(R.id.etFechaEvento)
        etHoraEvento = findViewById(R.id.etHoraEvento)
        etCantidadPersonas = findViewById(R.id.etCantidadPersonas)
        etPresupuesto = findViewById(R.id.etPresupuesto)
        etProductos = findViewById(R.id.etProductos)
        btnSolicitar = findViewById(R.id.btnSolicitar)

        // 5. CARGA DE COMBOS ESTÁTICOS Y DINÁMICOS
        val opcionesEventos = arrayOf(
            "Reunión de Facultad", "Defensa de Tesis", "Seminario / Taller",
            "Graduación", "Evento Deportivo", "Otro"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesEventos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etTipoEvento.adapter = adapter

        // Ejecutamos la carga autónoma de cafetines desde SQLite
        cargarComboCafetines()

        // 6. COMPORTAMIENTO INTERACTIVO (Listeners)

        // Calendario Nativo
        etFechaEvento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                etFechaEvento.setText(fechaSeleccionada)
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        // Reloj Nativo
        etHoraEvento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                val horaFormateada = if (hourOfDay == 0 || hourOfDay == 12) 12 else hourOfDay % 12
                val horaFinal = String.format("%02d:%02d %s", horaFormateada, minute, amPm)
                etHoraEvento.setText(horaFinal)
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), false)
            timePickerDialog.show()
        }

        // Selector de Productos filtrado dinámicamente por el Cafetín elegido
        etProductos.setOnClickListener {
            if (idLocalSeleccionado <= 0) {
                Toast.makeText(this, "Por favor, selecciona primero un cafetín.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (val resultado = productoRepository.obtenerProductosDisponiblesPorLocal(idLocalSeleccionado)) {
                is OperationResult.Success -> {
                    val productosDb = resultado.data

                    if (productosDb.isEmpty()) {
                        Toast.makeText(this, "Este cafetín no tiene productos disponibles.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (productosElegidosObjetos.isNotEmpty() && productosElegidosObjetos.first().idLocal != idLocalSeleccionado) {
                        productosElegidosObjetos.clear() // Vaciamos la lista global
                        etProductos.setText("")          // Limpiamos el EditText visualmente
                    }
                    val listaNombres = productosDb.map { "${it.nombreProducto} - $${it.precio}" }.toTypedArray()
                    val seleccionados = productosDb.map { productoDb ->
                        productosElegidosObjetos.any { seleccionado -> seleccionado.idProducto == productoDb.idProducto }
                    }.toBooleanArray()

                    AlertDialog.Builder(this)
                        .setTitle("Productos de este Cafetín")
                        .setMultiChoiceItems(listaNombres, seleccionados) { _, index, isChecked ->
                            val productoActual = productosDb[index]
                            if (isChecked) {
                                productosElegidosObjetos.add(productoActual)
                            } else {
                                productosElegidosObjetos.remove(productoActual)
                            }
                        }
                        .setPositiveButton("Aceptar") { _, _ ->
                            val nombresParaMostrar = productosElegidosObjetos.joinToString(", ") { it.nombreProducto }
                            etProductos.setText(nombresParaMostrar)

                            //calcular el total de oproductos seleccionados
                            val numPersona=etCantidadPersonas.text.toString().toInt()
                            val totalTemp = productosElegidosObjetos.sumOf { it.precio }*numPersona
                            val total: Double = round(totalTemp * 100) / 100.0
                            findViewById<TextView>(R.id.etTotal).text = total.toString()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
                is OperationResult.Error -> {
                    Toast.makeText(this, "Error: ${resultado.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botones de acción final
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnSolicitar.setOnClickListener { procesarPedidoEspecial() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 7. FUNCIÓN PARA CARGAR EL SPINNER DE CAFETINES DESDE SQLITE
    private fun cargarComboCafetines() {
        val listaLocales = ArrayList<Pair<Int, String>>()
        val db = AppDatabaseHelper(this).readableDatabase

        // Consultamos los locales reales de tu grupo
        val cursor = db.rawQuery("SELECT id_local, nombre_local FROM Locales", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_local"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_local"))
                listaLocales.add(Pair(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (listaLocales.isEmpty()) return

        val nombresCafetines = listaLocales.map { it.second }.toTypedArray()
        val adapterCafetines = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresCafetines)
        adapterCafetines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCafetin.adapter = adapterCafetines

        spCafetin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                idLocalSeleccionado = listaLocales[position].first
                etProductos.setText("") // Reseteamos productos para evitar cruce de locales
                if (productosElegidosObjetos.isNotEmpty() && productosElegidosObjetos.first().idLocal != idLocalSeleccionado) {
                    productosElegidosObjetos.clear() // Vaciamos la lista global
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // 8. LOGICA DE VALIDACIÓN Y PERSISTENCIA
    private fun procesarPedidoEspecial() {
        val tipo = etTipoEvento.selectedItem.toString().trim()
        val fecha = etFechaEvento.text.toString().trim()
        val hora = etHoraEvento.text.toString().trim()
        val cantidadStr = etCantidadPersonas.text.toString().trim()
        val presupuestoStr = etPresupuesto.text.toString().trim()
        val productosSeleccionados = etProductos.text.toString().trim()

        if (tipo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || cantidadStr.isEmpty() || presupuestoStr.isEmpty() || productosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = cantidadStr.toIntOrNull() ?: 0
        val presupuesto = presupuestoStr.toDoubleOrNull() ?: 0.0


        val idUsuario = sessionManager.obtenerIdUsuario()
        val idUbicacion = sessionManager.obtenerIdUbicacion()

        //concatenación en la descripción
        val descripcionCompleta = "$tipo - Productos: $productosSeleccionados"
        var totalTemp = productosElegidosObjetos.sumOf { it.precio }*cantidad
        val total: Double = round(totalTemp * 100) / 100.0
        if (total < 25.00 || total > 200.00 || presupuesto < total) {
            etPresupuesto.error = "El total debe estar entre $25.00 y $200.00 y menor al presupuesto."
            return
        }
        val pedidoBase = Pedido(
            idPedido = 0,
            tipoPedido = "Especial: $tipo",
            fechaPedido = "$fecha $hora",
            estadoPedido = AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO,
            total = total,
            idUsuario = idUsuario,
            idUbicacion = idUbicacion
        )

        val listaDetalles = productosElegidosObjetos.map { producto ->
            val subtotalTemp = producto.precio * cantidad
            val subtotal: Double = round(subtotalTemp * 100) / 100.0
            com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.DetallePedido(
                idDetallePedido = 0,
                idPedido = 0,
                idProducto = producto.idProducto,
                cantidad = cantidad,
                precioUnitario = producto.precio,
                subtotal = subtotal
            )
        }

        // 2. Pasamos la lista estructurada con los productos
        when (val resultadoLocal = pedidoRepository.crearPedido(pedidoBase, listaDetalles)) {
            is OperationResult.Success<Long> -> {
                val idInsertadoLocal = resultadoLocal.data
                val anticipo: Double = round((total*0.5) * 100) / 100.0
                val detalleEspecial = PedidoEspecial(
                    idPedidoEspecial = 0,
                    idPedido = idInsertadoLocal.toInt(),
                    descripcionEvento = descripcionCompleta,
                    fechaEvento = fecha,
                    horaEvento = hora,
                    numeroPersonas = cantidad,
                    montoMinimo = 25.00,
                    montoMaximo = 200.00,
                    anticipo = anticipo,
                    referenciaPago = null
                )

                when (val resultadoEspecial = pedidoEspecialRepository.registrarPedidoEspecial(detalleEspecial)) {
                    is OperationResult.Success<*> -> {
                        Toast.makeText(this, "Guardado localmente en SQLite.", Toast.LENGTH_SHORT).show()
                        enviarPedidoA_ApiRemota(pedidoBase, idInsertadoLocal.toInt())
                    }
                    is OperationResult.Error -> {
                        Toast.makeText(this, "Error al guardar detalle: ${resultadoEspecial.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is OperationResult.Error -> {
                Toast.makeText(this, "Error local base: ${resultadoLocal.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enviarPedidoA_ApiRemota(pedido: Pedido, idLocal: Int) {

        Toast.makeText(this, "Pedido registrado. Abriendo pantalla de pago...", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, PagoActivity::class.java).apply {

            putExtra(AppConstants.EXTRA_ID_PEDIDO, idLocal.toLong())
            putExtra(AppConstants.EXTRA_TOTAL_PAGAR, pedido.total)
            putExtra(AppConstants.EXTRA_VIENE_DE_MIS_PEDIDOS, true)
        }
        startActivity(intent)
        finish()
    }
}