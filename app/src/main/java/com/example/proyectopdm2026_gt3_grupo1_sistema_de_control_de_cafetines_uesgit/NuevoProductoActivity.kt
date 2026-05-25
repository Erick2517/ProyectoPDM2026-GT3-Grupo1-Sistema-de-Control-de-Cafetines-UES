package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.LocalLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.ProductoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.LocalRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.ProductoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Local
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.validation.ProductoValidator
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.ImageViewLoader
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult

class NuevoProductoActivity : AppCompatActivity() {
    private lateinit var productoRepository: ProductoRepository
    private lateinit var localRepository: LocalRepository
    private var locales: List<Local> = emptyList()
    private var productoEnEdicion: Producto? = null
    private var imagenUriSeleccionada: String? = null

    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            conservarPermisoLectura(uri)
            imagenUriSeleccionada = uri.toString()
            mostrarImagenSeleccionada()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevo_producto)
        configurarDependencias()
        configurarSpinnersBasicos()
        cargarLocales()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAddLocal = findViewById<Button>(R.id.btnGuardar)
        btnAddLocal.setOnClickListener {
            guardarProducto()
        }

        findViewById<LinearLayout>(R.id.layoutSubirImagen).setOnClickListener {
            seleccionarImagen.launch(arrayOf("image/*"))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        productoRepository = ProductoRepository(ProductoLocalDataSource(databaseHelper))
        localRepository = LocalRepository(LocalLocalDataSource(databaseHelper))
    }

    private fun configurarSpinnersBasicos() {
        configurarSpinner(R.id.spTipo, listOf("Normal", "Antojito", "Desayuno", "Almuerzo", "Bebida", "Refrigerio"))
        configurarSpinner(
            R.id.spDisponibilidadProducto,
            listOf(AppConstants.DISPONIBILIDAD_DISPONIBLE, AppConstants.DISPONIBILIDAD_NO_DISPONIBLE)
        )
    }

    private fun configurarSpinner(spinnerId: Int, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(spinnerId).adapter = adapter
    }

    private fun cargarLocales() {
        when (val resultado = localRepository.obtenerLocalesActivos()) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                locales = resultado.data
                configurarSpinner(R.id.spLocalProducto, locales.map { it.nombreLocal })
                cargarProductoSiEsEdicion()
            }
        }
    }

    private fun cargarProductoSiEsEdicion() {
        val esEdicion = intent.getBooleanExtra(AppConstants.EXTRA_MODO_EDICION_PRODUCTO, false)
        val idProducto = intent.getIntExtra(AppConstants.EXTRA_ID_PRODUCTO, 0)
        if (!esEdicion || idProducto <= 0) return

        when (val resultado = productoRepository.obtenerProductoPorId(idProducto)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> mostrarProducto(resultado.data)
        }
    }

    private fun mostrarProducto(producto: Producto) {
        productoEnEdicion = producto
        findViewById<TextView>(R.id.tvTituloFormularioProducto).text = "Editar producto"
        findViewById<Button>(R.id.btnGuardar).text = "Actualizar Producto"
        findViewById<EditText>(R.id.etNombreProductoAdmin).setText(producto.nombreProducto)
        findViewById<EditText>(R.id.etPrecioProductoAdmin).setText(producto.precio.toString())
        findViewById<EditText>(R.id.etStockProductoAdmin).setText(producto.stock.toString())
        seleccionarValor(R.id.spTipo, producto.tipo)
        seleccionarValor(R.id.spDisponibilidadProducto, producto.disponibilidad)
        imagenUriSeleccionada = producto.imagenUri
        mostrarImagenSeleccionada()
        val posicionLocal = locales.indexOfFirst { it.idLocal == producto.idLocal }
        if (posicionLocal >= 0) findViewById<Spinner>(R.id.spLocalProducto).setSelection(posicionLocal)
    }

    private fun guardarProducto() {
        if (locales.isEmpty()) {
            mostrarMensaje("Debe existir al menos un local activo para registrar productos.")
            return
        }

        val nombre = findViewById<EditText>(R.id.etNombreProductoAdmin).text.toString().trim()
        val precio = findViewById<EditText>(R.id.etPrecioProductoAdmin).text.toString().trim().toDoubleOrNull()
        val tipo = findViewById<Spinner>(R.id.spTipo).selectedItem?.toString().orEmpty()
        val disponibilidad = findViewById<Spinner>(R.id.spDisponibilidadProducto).selectedItem?.toString().orEmpty()
        val stock = findViewById<EditText>(R.id.etStockProductoAdmin).text.toString().trim().toIntOrNull()
        val local = locales.getOrNull(findViewById<Spinner>(R.id.spLocalProducto).selectedItemPosition)

        val error = ProductoValidator.validarProducto(
            nombre = nombre,
            precio = precio,
            tipo = tipo,
            disponibilidad = disponibilidad,
            stock = stock,
            idLocal = local?.idLocal
        )
        if (error != null) {
            mostrarMensaje(error)
            return
        }

        val producto = Producto(
            idProducto = productoEnEdicion?.idProducto ?: 0,
            nombreProducto = nombre,
            precio = precio ?: 0.0,
            disponibilidad = disponibilidad,
            tipo = tipo,
            stock = stock ?: 0,
            idLocal = local?.idLocal ?: 0,
            imagenUri = imagenUriSeleccionada
        )

        if (productoEnEdicion == null) registrarProducto(producto) else actualizarProducto(producto)
    }

    private fun registrarProducto(producto: Producto) {
        when (val resultado = productoRepository.registrarProducto(producto)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                mostrarMensaje("Producto registrado correctamente.")
                finish()
            }
        }
    }

    private fun actualizarProducto(producto: Producto) {
        when (val resultado = productoRepository.actualizarProducto(producto)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                if (resultado.data) {
                    mostrarMensaje("Producto actualizado correctamente.")
                    finish()
                } else {
                    mostrarMensaje("No se pudo actualizar el producto.")
                }
            }
        }
    }

    private fun seleccionarValor(spinnerId: Int, valor: String) {
        val spinner = findViewById<Spinner>(spinnerId)
        val posicion = (0 until spinner.count).firstOrNull { spinner.getItemAtPosition(it).toString() == valor }
        if (posicion != null) spinner.setSelection(posicion)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun conservarPermisoLectura(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: RuntimeException) {
            mostrarMensaje("No se pudo conservar el acceso permanente a la imagen.")
        }
    }

    private fun mostrarImagenSeleccionada() {
        ImageViewLoader.cargarImagen(
            findViewById(R.id.imgPreviewProducto),
            imagenUriSeleccionada,
            R.drawable.ic_camera
        )
    }
}
