package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {

    lateinit var txtNombre: EditText
    lateinit var txtCorreo: EditText
    lateinit var txtCarnet: EditText
    lateinit var txtPassword: EditText
    lateinit var txtConfirmPassword: EditText

    // Dirección IP del servidor local (XAMPP).
    // Nota: 192.168.1.3 debe ser la IP de su PC en la red local.
    private val URL_REGISTER = "http://192.168.1.3/cafetines_api/auth/register.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // Vinculación de objetos Kotlin con los IDs del XML
        val lblLogin = findViewById<TextView>(R.id.lblLogin)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtCarnet = findViewById(R.id.txtCarnet)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword)

        // Evento para ir al Login si ya se tiene cuenta
        lblLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // Lógica principal al presionar el botón de registro
        btnRegistrar.setOnClickListener {

            val nombre = txtNombre.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val carnet = txtCarnet.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val confirmPassword = txtConfirmPassword.text.toString().trim()

            // --- VALIDACIONES DE FORMULARIO ---
            if (nombre.isEmpty() || correo.isEmpty() || carnet.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si pasa todas las validaciones, intenta registrar en la nube (API)
            registerAPI(nombre, correo, carnet, password)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Intenta enviar los datos al servidor mediante una petición POST (Volley)
     */
    private fun registerAPI(nombre: String, email: String, carnet: String, password: String) {

        val request = object : StringRequest(
            Request.Method.POST, URL_REGISTER,
            { response -> // --- BLOQUE DE ÉXITO EN LA CONEXIÓN ---

                try {
                    val json = JSONObject(response)

                    // Si el servidor confirma éxito
                    if (json.getBoolean("success")) {

                        val data = json.getJSONObject("data")

                        // Mapeamos la respuesta JSON al modelo de datos Usuario
                        val usuario = Usuario(
                            idUsuario = data.getInt("id_usuario"),
                            nombre = data.getString("nombre"),
                            email = data.getString("email"),
                            password = data.getString("password"),
                            carnet = data.getString("carnet"),
                            idRol = data.getInt("id_rol")
                        )

                        // Sincronizamos con la base de datos interna (SQLite) para inicio de sesión rápido
                        val db = DatabaseHelper(this)
                        db.insertarOActualizarUsuario(usuario)

                        Toast.makeText(this, "Registro exitoso (online)", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {

                    e.printStackTrace()

                    Toast.makeText(
                        this,
                        "Error servidor, guardando offline",
                        Toast.LENGTH_SHORT
                    ).show()

                    registroLocal(nombre, email, carnet, password)
                }

            },
            {
                // --- BLOQUE DE FALLO (Offline) ---
                // Si el servidor está apagado o no hay internet, guarda los datos localmente
                registroLocal(nombre, email, carnet, password)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password,
                    "carnet" to carnet,
                    "id_rol" to "1"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun registroLocal(nombre: String, email: String, carnet: String, password: String) {

        val usuario = Usuario(
            nombre = nombre,
            email = email,
            password = password,
            carnet = carnet,
            idRol = 1
        )

        val db = DatabaseHelper(this)
        val resultado = db.insertarUsuario(usuario)

        if (resultado) {
            Toast.makeText(this, "Guardado offline", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error local (duplicado)", Toast.LENGTH_SHORT).show()
        }
    }
}