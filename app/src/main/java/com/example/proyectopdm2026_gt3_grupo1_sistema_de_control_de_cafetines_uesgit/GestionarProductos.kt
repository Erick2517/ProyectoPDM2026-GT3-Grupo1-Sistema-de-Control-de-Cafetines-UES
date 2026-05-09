package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Local
import org.json.JSONArray

class GestionarProductos : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var spinnerLocales: Spinner

    private var listaLocales = mutableListOf<Local>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_productos)

        val imgBack = findViewById<ImageView>(R.id.imgBack)
        val btnAgregarProducto = findViewById<Button>(R.id.btnAgregarProducto)

        container = findViewById(R.id.containerProductos)
        spinnerLocales = findViewById(R.id.spnLocales)

        imgBack.setOnClickListener {
            finish()
        }

        btnAgregarProducto.setOnClickListener {
            val intent = Intent(this, NuevoProductoActivity::class.java)
            startActivity(intent)
        }

        cargarLocales()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ctlMain)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // =========================
    // CARGAR LOCALES (SPINNER)
    // =========================
    private fun cargarLocales() {

        val url = "http://192.168.1.3/cafetines_api/locales/get_locales.php"

        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                val jsonArray = JSONArray(response)

                listaLocales.clear()
                val nombres = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val local = Local(
                        idLocal = obj.getInt("id_local"),
                        nombreLocal = obj.getString("nombre_local"),
                        ubicacion = "",
                        descripcion = "",
                        imagen = "",
                        entregaCampus = 0,
                        estado = ""
                    )

                    listaLocales.add(local)
                    nombres.add(local.nombreLocal)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombres
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerLocales.adapter = adapter

                spinnerLocales.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val idLocal = listaLocales[position].idLocal
                            cargarProductos(idLocal)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            },
            { error ->
                error.printStackTrace()
            }
        )

        queue.add(request)
    }

    // =========================
    // CARGAR PRODUCTOS POR LOCAL
    // =========================
    private fun cargarProductos(idLocal: Int) {

        val url = "http://192.168.1.3/cafetines_api/productos/get_productos.php?id_local=$idLocal"

        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                container.removeAllViews()

                val jsonArray = JSONArray(response)
                val inflater = LayoutInflater.from(this)

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val view = inflater.inflate(R.layout.item_producto, container, false)

                    val tvNombre = view.findViewById<TextView>(R.id.tvNombreProducto)
                    val tvPrecio = view.findViewById<TextView>(R.id.tvPrecio)
                    val tvEstado = view.findViewById<TextView>(R.id.tvEstado)
                    val imgProducto = view.findViewById<ImageView>(R.id.imgProducto)

                    val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
                    val btnEditar = view.findViewById<Button>(R.id.btnEditar)

                    tvNombre.text = obj.getString("nombre_producto")
                    tvPrecio.text = "$${obj.getDouble("precio")}"
                    tvEstado.text = obj.getString("estado")

                    tvNombre.text = obj.getString("nombre_producto")
                    tvPrecio.text = "$${obj.getDouble("precio")}"
                    tvEstado.text = obj.getString("estado")

                    val imagen = obj.optString("imagen", "")

                    if (imagen.isNotEmpty()) {

                        val urlImagen = "http://192.168.1.3/cafetines_api/" + imagen.replace("\\", "")

                        Glide.with(this)
                            .load(urlImagen)
                            .into(imgProducto)

                    } else {

                        imgProducto.setImageResource(R.drawable.logo_ues)
                    }

                    btnEliminar.setOnClickListener {
                        container.removeView(view)
                    }

                    btnEditar.setOnClickListener {
                        // luego: editar con ID
                    }

                    container.addView(view)
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        queue.add(request)
    }
}