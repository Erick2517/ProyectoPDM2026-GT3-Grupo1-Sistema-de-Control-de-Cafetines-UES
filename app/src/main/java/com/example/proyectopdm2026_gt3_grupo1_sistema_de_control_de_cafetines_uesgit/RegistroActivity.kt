package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        val lblLogin = findViewById<TextView>(R.id.lbl_login);
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar);

        lblLogin.setOnClickListener {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnRegistrar.setOnClickListener {
            val intent = Intent(
                this,
                BienvenidaActivity::class.java
            )
            startActivity(intent);
            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}