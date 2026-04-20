package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)

        val btnLogOut = findViewById<Button>(R.id.btnCerrarSesion)
        val btnVerLocales = findViewById<Button>(R.id.btnVerLocales)
        val btnMisPedidos = findViewById<Button>(R.id.btnMisPedidos)
        val btnPedidosEsp = findViewById<Button>(R.id.btnPedidoEspecial)

        val btnLocalCentral = findViewById<Button>(R.id.btnLocalCentral)
        val btnLocalIng = findViewById<Button>(R.id.btnLocalIngenieria)
        val local1 = findViewById<LinearLayout>(R.id.local1)
        val local2 = findViewById<LinearLayout>(R.id.local2)

        btnLogOut.setOnClickListener {
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
}