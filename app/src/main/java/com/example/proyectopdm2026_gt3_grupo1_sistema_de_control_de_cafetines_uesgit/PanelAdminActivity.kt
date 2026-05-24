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

class PanelAdminActivity : AppCompatActivity() {
    private lateinit var permisoRepository: PermisoRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_admin)
        configurarDependencias()
        aplicarPermisosMenu()

        val btnLogOut = findViewById<Button>(R.id.btnCerrarSesion)
        btnLogOut.setOnClickListener {
            sessionManager.cerrarSesion()
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        val crdGestionarProductos = findViewById<LinearLayout>(R.id.crdGestionarProductos)
        val crdGestionarLocales = findViewById<LinearLayout>(R.id.crdGestionarLocales)
        val crdGestionarUsuarios = findViewById<LinearLayout>(R.id.crdGestionarUsuarios)

        crdGestionarProductos.setOnClickListener {
            val intent = Intent(
                this,
                GestionarProductos::class.java
            )
            startActivity(intent)
        }
        crdGestionarLocales.setOnClickListener {
            val intent = Intent(
                this,
                GestionarLocalesActivity::class.java
            )
            startActivity(intent)
        }
        crdGestionarUsuarios.setOnClickListener {
            val intent = Intent(
                this,
                GestionarUsuariosActivity::class.java
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
                configurarVisibilidad(R.id.cardLocales, opciones.contains(AppConstants.OPCION_GESTIONAR_LOCALES))
                configurarVisibilidad(R.id.cardProductos, opciones.contains(AppConstants.OPCION_GESTIONAR_PRODUCTOS))
                configurarVisibilidad(R.id.cardUsuarios, opciones.contains(AppConstants.OPCION_GESTIONAR_USUARIOS))
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
