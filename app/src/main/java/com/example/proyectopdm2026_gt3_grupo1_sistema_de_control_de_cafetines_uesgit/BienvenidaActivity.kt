package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database.AppDatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.datasource.OpcionMenuLocalDataSource
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.repository.PermisoRepository
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.AppConstants
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.OperationResult
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util.SessionManager

class BienvenidaActivity : AppCompatActivity() {
    private lateinit var permisoRepository: PermisoRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)
        configurarDependencias()
        aplicarPermisosMenu()

        val btnLogOut = findViewById<Button>(R.id.btnCerrarSesion)
        val btnVerLocales = findViewById<Button>(R.id.btnVerLocales)
        val btnMisPedidos = findViewById<Button>(R.id.btnMisPedidos)
        val btnPedidosEsp = findViewById<Button>(R.id.btnPedidoEspecial)

        val btnLocalCentral = findViewById<Button>(R.id.btnLocalCentral)
        val btnLocalIng = findViewById<Button>(R.id.btnLocalIngenieria)
        val local1 = findViewById<LinearLayout>(R.id.local1)
        val local2 = findViewById<LinearLayout>(R.id.local2)

        btnLogOut.setOnClickListener {
            sessionManager.cerrarSesion()
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnVerLocales.setOnClickListener {
            val intent = Intent(
                this,
                LocalesActivity::class.java
            )
            startActivity(intent)
        }

        btnMisPedidos.setOnClickListener {
            val intent = Intent(
                this,
                MisPedidosActivity::class.java
            )
            startActivity(intent)
        }

        btnPedidosEsp.setOnClickListener {
            val intent = Intent(
                this,
                PedidoEspecialActivity::class.java
            )
            startActivity(intent)
        }

        btnLocalCentral.setOnClickListener {
            val intent = Intent(
                this,
                ProductosActivity::class.java
            )
            startActivity(intent)
        }
        btnLocalIng.setOnClickListener {
            val intent = Intent(
                this,
                ProductosActivity::class.java
            )
            startActivity(intent)
        }

        local1.setOnClickListener {
            val intent = Intent(
                this,
                ProductosActivity::class.java
            )
            startActivity(intent)
        }
        local2.setOnClickListener {
            val intent = Intent(
                this,
                ProductosActivity::class.java
            )
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarDependencias() {
        val databaseHelper = AppDatabaseHelper(this)
        permisoRepository = PermisoRepository(OpcionMenuLocalDataSource(databaseHelper))
        sessionManager = SessionManager(this)
    }

    private fun aplicarPermisosMenu() {
        val idRol = sessionManager.obtenerIdRol()
        when (val resultado = permisoRepository.obtenerOpcionesPorRol(idRol)) {
            is OperationResult.Error -> mostrarMensaje(resultado.message)
            is OperationResult.Success -> {
                val opciones = resultado.data.map { it.nombreOpcion }.toSet()
                val puedeVerLocales = opciones.contains(AppConstants.OPCION_VER_LOCALES)
                configurarVisibilidad(R.id.btnVerLocales, puedeVerLocales)
                configurarVisibilidad(R.id.tvDestacados, puedeVerLocales)
                configurarVisibilidad(R.id.local1, puedeVerLocales)
                configurarVisibilidad(R.id.local2, puedeVerLocales)
                configurarVisibilidad(R.id.tvLocales, puedeVerLocales)
                configurarVisibilidad(R.id.btnLocalCentral, puedeVerLocales)
                configurarVisibilidad(R.id.btnLocalIngenieria, puedeVerLocales)
                configurarVisibilidad(R.id.btnMisPedidos, opciones.contains(AppConstants.OPCION_MIS_PEDIDOS))
                configurarVisibilidad(R.id.btnPedidoEspecial, opciones.contains(AppConstants.OPCION_PEDIDO_ESPECIAL))
            }
        }
    }

    private fun configurarVisibilidad(viewId: Int, visible: Boolean) {
        findViewById<View>(viewId).visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
