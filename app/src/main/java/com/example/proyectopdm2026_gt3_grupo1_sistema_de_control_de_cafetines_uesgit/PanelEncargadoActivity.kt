package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PanelEncargadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_encargado)

        val btnLogOut = findViewById<Button>(R.id.btnCerrarSesion)
        btnLogOut.setOnClickListener {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        val crdGestionarProductos = findViewById<LinearLayout>(R.id.crdGestionarProductos)
        val crdGestionarPedidos = findViewById<LinearLayout>(R.id.crdGestionarPedidos)

        crdGestionarProductos.setOnClickListener {
            val intent = Intent(
                this,
                GestionarProductos::class.java
            )
            startActivity(intent)
        }
        crdGestionarPedidos.setOnClickListener {
            val intent = Intent(
                this,
                ControlPedidosActivity::class.java
            )
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}