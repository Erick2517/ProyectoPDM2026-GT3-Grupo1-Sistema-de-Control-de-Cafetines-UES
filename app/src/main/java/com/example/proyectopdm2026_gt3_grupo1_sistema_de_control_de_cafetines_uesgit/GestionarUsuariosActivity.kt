package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.adapter.UsuarioAdapter
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario
import org.json.JSONObject

class GestionarUsuariosActivity : AppCompatActivity() {

    // URL de tu API en PHP
    private val URL_USERS = "http://192.168.1.3/cafetines_api/users/get_usuarios.php"

    private lateinit var rvUsuarios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Habilitar el diseño de borde a borde
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_usuarios)

        // 2. CORRECCIÓN VISUAL: Ajusta el padding para que el TopBar respete la barra de estado
        // IMPORTANTE: Asegúrate de que el ID en tu XML sea android:id="@+id/main"
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        rvUsuarios = findViewById(R.id.rvUsuarios)

        // Configuración del RecyclerView
        rvUsuarios.layoutManager = LinearLayoutManager(this)

        // Evento para regresar a la pantalla anterior
        btnBack.setOnClickListener {
            finish()
        }

        // Iniciar la carga de datos
        cargarUsuariosOnline()
    }

    /**
     * Intenta obtener la lista de usuarios desde el servidor MySQL mediante Volley.
     */
    private fun cargarUsuariosOnline() {
        val request = StringRequest(
            Request.Method.GET, URL_USERS,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getBoolean("success")) {
                        val data = json.getJSONArray("data")
                        val lista = mutableListOf<Usuario>()

                        for (i in 0 until data.length()) {
                            val obj = data.getJSONObject(i)
                            lista.add(
                                Usuario(
                                    idUsuario = obj.getInt("id_usuario"),
                                    nombre = obj.getString("nombre"),
                                    email = obj.getString("email"),
                                    password = "", // No descargamos passwords por seguridad
                                    carnet = obj.getString("carnet"),
                                    idRol = obj.getInt("id_rol")
                                )
                            )
                        }
                        // Asignar el adaptador con la lista obtenida de la API
                        rvUsuarios.adapter = UsuarioAdapter(lista)
                    } else {
                        Toast.makeText(this, "Error: ${json.getString("message")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Si el JSON falla, intentamos cargar localmente
                    cargarUsuariosOffline()
                }
            },
            {
                // Si falla la red (servidor apagado), cargamos desde SQLite
                cargarUsuariosOffline()
            }
        )
        // Ejecutar petición
        Volley.newRequestQueue(this).add(request)
    }

    /**
     * Carga los usuarios almacenados en la base de datos interna SQLite (Modo Offline).
     */
    private fun cargarUsuariosOffline() {
        val db = DatabaseHelper(this)
        val lista = mutableListOf<Usuario>()

        // Consulta directa a la tabla local de usuarios
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM usuarios", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Usuario(
                        idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        password = "",
                        carnet = cursor.getString(cursor.getColumnIndexOrThrow("carnet")),
                        idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los datos locales en la lista
        rvUsuarios.adapter = UsuarioAdapter(lista)

        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay datos locales guardados", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Mostrando datos offline", Toast.LENGTH_SHORT).show()
        }
    }
}
