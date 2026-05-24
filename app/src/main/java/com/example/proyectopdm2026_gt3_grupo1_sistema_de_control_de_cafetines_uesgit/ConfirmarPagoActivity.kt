package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PagoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.PedidoLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PagoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PedidoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Pago
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ConfirmarPagoActivity : AppCompatActivity() {
    private lateinit var pagoRepository: PagoRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_pago)
        configurarDependencias()
        cargarPago()
        val btnVerPedidos = findViewById<Button>(R.id.btnVerPedidos)
        val lblRegresarInicio = findViewById<TextView>(R.id.lblVolverInicio);

        btnVerPedidos.setOnClickListener {
            val intent = Intent(
                this,
                MisPedidosActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        lblRegresarInicio.setOnClickListener {
            val intent = Intent(
                this,
                BienvenidaActivity::class.java
            )
            startActivity(intent)
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
        pagoRepository = PagoRepository(PagoLocalDataSource(databaseHelper))
    }
    private fun cargarPago() {
        val idPago = intent.getLongExtra(AppConstants.ID_PAGO, 0L).toInt()
        if (idPago <= 0) {
            Toast.makeText(this, "No se recibió un pago válido.", Toast.LENGTH_LONG).show()
            return
        }

        when (val resultado = pagoRepository.obtenerPagoPorId(idPago)) {
            is OperationResult.Error ->   Toast.makeText(this, resultado.message, Toast.LENGTH_LONG).show()
            is OperationResult.Success -> mostrarPedido(resultado.data)
        }
    }

    private fun mostrarPedido(pago: Pago) {
        findViewById<TextView>(R.id.txtNumPeido).text = "Pedido #${pago.idPedido}"
        findViewById<TextView>(R.id.txtMetodoPago).text = pago.metodoPago
        findViewById<TextView>(R.id.txtTotalPagado).text = pago.monto.toString()
        findViewById<TextView>(R.id.tvEstado).text = pago.estadoPago
        if (pago.estadoPago != AppConstants.ESTADO_PEDIDO_PENDIENTE_PAGO) {
            Toast.makeText(this, "El pedido no se encuentra pendiente de pago.", Toast.LENGTH_LONG).show()
        }
    }
}