package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LocalesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_locales)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val cardLocal = findViewById<LinearLayout>(R.id.local1)

        btnBack.setOnClickListener {
            finish()
        }

        // Configuración para la tarjeta del local:
        // Al tocar el cafetín, nos lleva a la pantalla de "ProductosActivity"
        cardLocal.setOnClickListener {
            val intent = Intent(
                this,
                ProductosActivity::class.java
            )
            startActivity(intent)// Ejecuta el salto a la otra pantalla
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}