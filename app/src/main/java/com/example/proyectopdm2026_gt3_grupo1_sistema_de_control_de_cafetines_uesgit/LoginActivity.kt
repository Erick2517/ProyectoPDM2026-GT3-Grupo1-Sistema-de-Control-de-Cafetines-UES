package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
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

        btnLogin.setOnClickListener {

            val correo = txtCorreo.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginAPI(correo, password)
        }

        lblRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // LOGIN API (ONLINE)
    private fun loginAPI(email: String, password: String) {

        val url = "http://192.168.1.3/cafetines_api/auth/login.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                try {

                    println("RESPUESTA SERVER: $response")
                    val json = JSONObject(response)

                    if (json.getBoolean("success")) {

                        val data = json.getJSONObject("data")

                        val usuario = Usuario(
                            idUsuario = data.getInt("id_usuario"),
                            nombre = data.getString("nombre"),
                            email = data.getString("email"),
                            password = data.getString("password"),
                            carnet = data.getString("carnet"),
                            idRol = data.getInt("id_rol")
                        )

                        guardarSesion(usuario)

                        val db = DatabaseHelper(this)

                        // limpiar cache anterior
                        db.eliminarUsuarios()

                        // guardar como cache ligera
                        db.insertarOActualizarUsuario(usuario)

                        irSegunRol(usuario.idRol)

                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {

                    e.printStackTrace()

                    // ENTRA A SQLITE SI FALLA JSON
                    loginLocal(email, password)
                }

            },
            {

                // OFFLINE
                loginLocal(email, password)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "email" to email,
                    "password" to password
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    // LOGIN LOCAL (OFFLINE)

    private fun loginLocal(email: String, password: String) {

        val db = DatabaseHelper(this)
        val usuario = db.login(email, password)

        if (usuario != null) {
            guardarSesion(usuario)
            irSegunRol(usuario.idRol)
            Toast.makeText(this, "Modo offline", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sin conexión y sin datos locales", Toast.LENGTH_SHORT).show()
        }
    }

    // SESIÓN

    private fun guardarSesion(usuario: Usuario) {

        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)

        prefs.edit()
            .putInt("idUsuario", usuario.idUsuario)
            .putInt("idRol", usuario.idRol)
            .putString("nombre", usuario.nombre)
            .putString("email", usuario.email)
            .apply()
    }

    // REDIRECCIÓN

    private fun irSegunRol(idRol: Int) {

        when (idRol) {
            2 -> startActivity(Intent(this, PanelAdminActivity::class.java))
            3 -> startActivity(Intent(this, PanelEncargadoActivity::class.java))
            1 -> startActivity(Intent(this, BienvenidaActivity::class.java))
            else -> Toast.makeText(this, "Rol no válido", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}