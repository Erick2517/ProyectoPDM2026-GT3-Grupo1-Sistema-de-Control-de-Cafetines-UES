package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnVerCarrito = findViewById<Button>(R.id.btnVerCarrito)
        val imgCarrito = findViewById<ImageView>(R.id.btnCarrito)

        btnBack.setOnClickListener {
            finish()
        }

        btnVerCarrito.setOnClickListener {
            val intent = Intent(
                this,
                CarritoActivity::class.java
            )
            startActivity(intent)
        }
        imgCarrito.setOnClickListener {
            val intent = Intent(
                this,
                CarritoActivity::class.java
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