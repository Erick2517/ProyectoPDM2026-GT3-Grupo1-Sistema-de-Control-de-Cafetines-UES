package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

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
import org.json.JSONObject

class EditarUsuarioActivity : AppCompatActivity() {
    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCarnet: EditText
    private lateinit var etPassword: EditText
    private lateinit var spRol: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageView

    private var idUsuario: Int = -1

    private var listaRoles = mutableListOf<Pair<Int, String>>()
    private var idRolSeleccionado = -1
    private var idRolActual = -1

    private val URL_UPDATE =
        "http://192.168.1.3/cafetines_api/users/update_usuario.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_usuario)

        val mainView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etCarnet = findViewById(R.id.etCarnet)
        etPassword = findViewById(R.id.etPassword)
        spRol = findViewById(R.id.spRol)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)

        //  RECUPERAR DATOS: Los datos vienen de la pantalla anterior (Lista de Usuarios)
        idUsuario = intent.getIntExtra("id", -1)
        val nombre = intent.getStringExtra("nombre")
        val email = intent.getStringExtra("email")
        val carnet = intent.getStringExtra("carnet")
        idRolActual = intent.getIntExtra("rol", -1)

        //  MOSTRAR DATOS: Llenamos el formulario con la info actual
        etNombre.setText(nombre)
        etEmail.setText(email)
        etCarnet.setText(carnet)

        btnBack.setOnClickListener { finish() }

        //  CARGAR ROLES: Trae los roles desde la base de datos para llenar el Spinner
        cargarRoles()
        // EVENTO SPINNER: Detecta cuál rol selecciona el usuario

        spRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                idRolSeleccionado = listaRoles[position].first
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnGuardar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val carnet = etCarnet.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || carnet.isEmpty()) {
                Toast.makeText(this, "Campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Inicia el proceso de actualización en la nube

            actualizarUsuario(idUsuario, nombre, email, carnet, password, idRolSeleccionado)
        }
    }


    // CARGAR ROLES

    private fun cargarRoles() {

        val url = "http://192.168.1.3/cafetines_api/roles/get_roles.php"

        val request = StringRequest(Request.Method.GET, url,
            { response ->

                val json = JSONObject(response)
                val data = json.getJSONArray("data")

                val nombres = mutableListOf<String>()
                listaRoles.clear()

                for (i in 0 until data.length()) {
                    val obj = data.getJSONObject(i)

                    val id = obj.getInt("id_rol")
                    val nombre = obj.getString("nombre_rol")

                    listaRoles.add(Pair(id, nombre))
                    nombres.add(nombre)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombres
                )

                spRol.adapter = adapter

                val pos = listaRoles.indexOfFirst { it.first == idRolActual }
                if (pos != -1) spRol.setSelection(pos)

            },
            {
                Toast.makeText(this, "Error cargando roles", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }


    // ONLINE UPDATE

    private fun actualizarUsuario(
        id: Int,
        nombre: String,
        email: String,
        carnet: String,
        password: String,
        rol: Int
    ) {

        val request = object : StringRequest(
            Request.Method.POST,
            URL_UPDATE,
            { response ->

                try {
                    val json = JSONObject(response)

                    if (json.getBoolean("success")) {
                        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        actualizarUsuarioOffline(id, nombre, email, carnet, password, rol)
                    }
                } catch (e: Exception) {
                    actualizarUsuarioOffline(id, nombre, email, carnet, password, rol)
                }
            },
            {
                actualizarUsuarioOffline(id, nombre, email, carnet, password, rol)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {

                val params = hashMapOf(
                    "id_usuario" to id.toString(),
                    "nombre" to nombre,
                    "email" to email,
                    "carnet" to carnet,
                    "id_rol" to rol.toString()
                )

                //  SOLO SI SE CAMBIA PASSWORD
                if (password.isNotEmpty()) {
                    params["password"] = password
                }

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }


    // OFFLINE UPDATE

    private fun actualizarUsuarioOffline(
        id: Int,
        nombre: String,
        email: String,
        carnet: String,
        password: String,
        rol: Int
    ) {

        val db = DatabaseHelper(this).writableDatabase

        val sql = if (password.isNotEmpty()) {
            """
            UPDATE usuarios 
            SET nombre=?, email=?, carnet=?, password=?, id_rol=? 
            WHERE id_usuario=?
            """
        } else {
            """
            UPDATE usuarios 
            SET nombre=?, email=?, carnet=?, id_rol=? 
            WHERE id_usuario=?
            """
        }

        val stmt = db.compileStatement(sql)

        stmt.bindString(1, nombre)
        stmt.bindString(2, email)
        stmt.bindString(3, carnet)

        if (password.isNotEmpty()) {
            stmt.bindString(4, password)
            stmt.bindLong(5, rol.toLong())
            stmt.bindLong(6, id.toLong())
        } else {
            stmt.bindLong(4, rol.toLong())
            stmt.bindLong(5, id.toLong())
        }

        stmt.executeUpdateDelete()

        Toast.makeText(this, "Actualizado offline", Toast.LENGTH_SHORT).show()
        finish()
    }
}