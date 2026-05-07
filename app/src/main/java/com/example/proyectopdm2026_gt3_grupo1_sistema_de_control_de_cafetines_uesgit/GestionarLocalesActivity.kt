package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit
import android.view.View
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Local
import org.json.JSONArray
import com.bumptech.glide.Glide
class GestionarLocalesActivity : AppCompatActivity() {

    private lateinit var containerLocales: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_locales)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnAgregarLocal = findViewById<Button>(R.id.btnAgregarLocal)
        containerLocales = findViewById(R.id.containerLocales)

        btnBack.setOnClickListener {
            finish()
        }

        btnAgregarLocal.setOnClickListener {
            val intent = Intent(this, NuevoLocalActivity::class.java)
            startActivity(intent)
        }

        cargarLocales()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // =========================
    // CARGAR LOCALES DESDE API
    // =========================
    private fun cargarLocales() {

        val url = "http://192.168.1.3/cafetines_api/locales/get_locales.php"

        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->

                val jsonArray = JSONArray(response)

                containerLocales.removeAllViews()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val nombre = obj.getString("nombre_local")
                    val ubicacion = obj.getString("ubicacion")
                    val descripcion = obj.getString("descripcion")
                    val estado = obj.getString("estado")
                    val imagen = obj.getString("imagen")

                    val view = LayoutInflater.from(this)
                        .inflate(R.layout.item_local, containerLocales, false)

                    val tvNombre = view.findViewById<TextView>(R.id.tvNombreLocal)
                    val tvUbicacion = view.findViewById<TextView>(R.id.tvUbicacion)
                    val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcion)
                    val tvEstado = view.findViewById<TextView>(R.id.tvEstado)
                    val estadoColor = view.findViewById<View>(R.id.estadoColor)
                    val img = view.findViewById<ImageView>(R.id.imgLocal)

                    // ===== ASIGNAR TEXTOS =====
                    tvNombre.text = nombre
                    tvUbicacion.text = ubicacion
                    tvDescripcion.text = descripcion
                    tvEstado.text = estado

                    // ===== COLOR ESTADO =====
                    if (estado.equals("Activo", true)) {
                        estadoColor.setBackgroundColor(android.graphics.Color.GREEN)
                    } else {
                        estadoColor.setBackgroundColor(android.graphics.Color.RED)
                    }

                    // ===== IMAGEN =====
                    if (imagen.startsWith("http")) {
                        com.bumptech.glide.Glide.with(this)
                            .load(imagen)
                            .into(img)
                    }

                    containerLocales.addView(view)
                }
            },
            {
                Toast.makeText(
                    this,
                    "Error al cargar locales",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}