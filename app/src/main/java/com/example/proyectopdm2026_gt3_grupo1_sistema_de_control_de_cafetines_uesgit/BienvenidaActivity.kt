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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)

        // =========================
        // SESIÓN
        // =========================
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)

        val nombre = prefs.getString("nombre", "Usuario")
        val idUsuario = prefs.getInt("idUsuario", 0)
        val idRol = prefs.getInt("idRol", 0)

        // =========================
        // VISTAS
        // =========================
        val tvSaludo = findViewById<TextView>(R.id.tv_saludo)

        val btnLogOut = findViewById<Button>(R.id.btnCerrarSesion)
        val btnVerLocales = findViewById<Button>(R.id.btnVerLocales)
        val btnMisPedidos = findViewById<Button>(R.id.btnMisPedidos)
        val btnPedidosEsp = findViewById<Button>(R.id.btnPedidoEspecial)

        val btnLocalCentral = findViewById<Button>(R.id.btnLocalCentral)
        val btnLocalIng = findViewById<Button>(R.id.btnLocalIngenieria)

        val local1 = findViewById<LinearLayout>(R.id.local1)
        val local2 = findViewById<LinearLayout>(R.id.local2)

        // =========================
        // SALUDO
        // =========================
        val db = DatabaseHelper(this)
        val usuario = db.obtenerUsuarioPorId(idUsuario)

        if (usuario != null) {
            tvSaludo.text = "Hola, ${usuario.nombre}"
        } else {
            tvSaludo.text = "Hola, $nombre"
        }

        // =========================
        // LOGOUT
        // =========================
        btnLogOut.setOnClickListener {

            val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // =========================
        // NAVEGACIÓN
        // =========================
        btnVerLocales.setOnClickListener {
            startActivity(Intent(this, LocalesActivity::class.java))
        }

        btnMisPedidos.setOnClickListener {
            startActivity(Intent(this, MisPedidosActivity::class.java))
        }

        btnPedidosEsp.setOnClickListener {
            startActivity(Intent(this, PedidoEspecialActivity::class.java))
        }

        btnLocalCentral.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        btnLocalIng.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        local1.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        local2.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        // =========================
        // AJUSTE DE PANTALLA
        // =========================
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}