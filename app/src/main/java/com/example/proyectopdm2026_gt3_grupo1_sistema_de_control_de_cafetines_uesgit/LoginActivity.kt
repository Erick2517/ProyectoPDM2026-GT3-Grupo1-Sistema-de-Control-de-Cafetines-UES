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
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    lateinit var txtCorreo: EditText
    lateinit var txtPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val lblRegistrarse = findViewById<TextView>(R.id.lblRegistrarse)

        txtCorreo = findViewById(R.id.txtCorreo)
        txtPassword = findViewById(R.id.txtPassword)

        val dbHelper = DatabaseHelper(this)

        btnLogin.setOnClickListener {

            val correo = txtCorreo.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = dbHelper.login(correo, password)

            if (usuario != null) {

                //  GUARDAR SESIÓN
                val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putInt("idUsuario", usuario.idUsuario)
                editor.putInt("idRol", usuario.idRol)
                editor.putString("nombre", usuario.nombre)
                editor.putString("email", usuario.email)
                editor.apply()

                Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                // REDIRECCIÓN POR ROL
                when (usuario.idRol) {

                    2 -> {
                        startActivity(Intent(this, PanelAdminActivity::class.java))
                    }

                    3 -> {
                        startActivity(Intent(this, PanelEncargadoActivity::class.java))
                    }

                    1 -> {
                        startActivity(Intent(this, BienvenidaActivity::class.java))
                    }

                    else -> {
                        Toast.makeText(this, "Rol no válido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                finish()

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        // IR A REGISTRO
        lblRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}