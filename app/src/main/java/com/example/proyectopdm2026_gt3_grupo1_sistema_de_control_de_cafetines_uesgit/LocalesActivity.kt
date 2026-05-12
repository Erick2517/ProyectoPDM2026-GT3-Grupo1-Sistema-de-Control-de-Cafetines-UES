package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray

class LocalesActivity : AppCompatActivity() {

    private lateinit var containerLocales: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_locales)

        val btnBack =
            findViewById<ImageView>(R.id.imgBack)

        containerLocales =
            findViewById(R.id.lytContainerLocales)

        btnBack.setOnClickListener {
            finish()
        }

        cargarLocales()

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    // =========================
    // CARGAR LOCALES
    // =========================
    private fun cargarLocales() {

        val url =
            "http://192.168.1.3/cafetines_api/locales/get_locales.php"

        val request = StringRequest(
            Request.Method.GET,
            url,

            { response ->

                containerLocales.removeAllViews()

                val jsonArray = JSONArray(response)

                val inflater =
                    LayoutInflater.from(this)

                for (i in 0 until jsonArray.length()) {

                    val obj =
                        jsonArray.getJSONObject(i)

                    val view = inflater.inflate(
                        R.layout.item_local,
                        containerLocales,
                        false
                    )

                    // =========================
                    // REFERENCIAS
                    // =========================

                    val tvNombre =
                        view.findViewById<TextView>(
                            R.id.tvNombreLocal
                        )

                    val tvUbicacion =
                        view.findViewById<TextView>(
                            R.id.tvUbicacion
                        )

                    val tvDescripcion =
                        view.findViewById<TextView>(
                            R.id.tvDescripcion
                        )

                    val tvEstado =
                        view.findViewById<TextView>(
                            R.id.tvEstado
                        )

                    val imgLocal =
                        view.findViewById<ImageView>(
                            R.id.imgLocal
                        )

                    val btnEditar =
                        view.findViewById<Button>(
                            R.id.btnEditar
                        )

                    val estadoColor =
                        view.findViewById<View>(
                            R.id.estadoColor
                        )

                    // =========================
                    // DATOS
                    // =========================

                    tvNombre.text =
                        obj.getString("nombre_local")

                    tvUbicacion.text =
                        obj.getString("ubicacion")

                    tvDescripcion.text =
                        obj.getString("descripcion")

                    tvEstado.text =
                        obj.getString("estado")

                    // =========================
                    // COLOR ESTADO
                    // =========================

                    if (
                        obj.getString("estado")
                        == "Activo"
                    ) {

                        estadoColor.setBackgroundColor(
                            getColor(android.R.color.holo_green_dark)
                        )

                    } else {

                        estadoColor.setBackgroundColor(
                            getColor(android.R.color.holo_red_dark)
                        )
                    }

                    // =========================
                    // IMAGEN
                    // =========================

                    val imagen =
                        obj.getString("imagen")

                    Glide.with(this)
                        .load(imagen)
                        .placeholder(R.drawable.logo_ues)
                        .error(R.drawable.logo_ues)
                        .into(imgLocal)
                    // =========================
                    // OCULTAR BOTON EDITAR
                    // =========================

                    btnEditar.visibility = View.GONE

                    // =========================
                    // CLICK LOCAL
                    // =========================

                    view.setOnClickListener {

                        val intent = Intent(
                            this,
                            ProductosActivity::class.java
                        )

                        intent.putExtra(
                            "id_local",
                            obj.getInt("id_local")
                        )

                        startActivity(intent)
                    }

                    containerLocales.addView(view)
                }
            },

            {
                it.printStackTrace()
            }
        )

        Volley.newRequestQueue(this)
            .add(request)
    }
}