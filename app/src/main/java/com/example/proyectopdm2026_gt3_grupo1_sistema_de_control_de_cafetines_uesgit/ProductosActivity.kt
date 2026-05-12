package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray

class ProductosActivity : AppCompatActivity() {

    private lateinit var containerProductos: LinearLayout
    private lateinit var lblNombreLocal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)

        val btnBack =
            findViewById<ImageView>(R.id.imgBack)

        val btnVerCarrito =
            findViewById<Button>(R.id.btnVerCarrito)

        val imgCarrito =
            findViewById<ImageView>(R.id.imgCarrito)

        containerProductos =
            findViewById(R.id.lytContainerProductos)

        lblNombreLocal =
            findViewById(R.id.lblNombreLocal)

        val idLocal =
            intent.getIntExtra("id_local", 0)

        val nombreLocal =
            intent.getStringExtra("nombre_local")

        lblNombreLocal.text = nombreLocal

        btnBack.setOnClickListener {
            finish()
        }

        btnVerCarrito.setOnClickListener {

            val intent = Intent(
                this,
                CarritoActivity::class.java
            )

            startActivity(intent)
        }

        imgCarrito.setOnClickListener {

            val intent = Intent(
                this,
                CarritoActivity::class.java
            )

            startActivity(intent)
        }

        cargarProductos(idLocal)

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
    // CARGAR PRODUCTOS
    // =========================

    private fun cargarProductos(idLocal: Int) {

        val url =
            "http://192.168.1.3/cafetines_api/productos/get_productos.php?id_local=$idLocal"

        val request = StringRequest(
            Request.Method.GET,
            url,

            { response ->

                containerProductos.removeAllViews()

                val jsonArray = JSONArray(response)

                val inflater =
                    LayoutInflater.from(this)

                for (i in 0 until jsonArray.length()) {

                    val obj =
                        jsonArray.getJSONObject(i)

                    val view = inflater.inflate(
                        R.layout.item_producto_cliente,
                        containerProductos,
                        false
                    )

                    // =========================
                    // REFERENCIAS
                    // =========================

                    val lblNombreProducto =
                        view.findViewById<TextView>(
                            R.id.lblNombreProducto
                        )

                    val lblPrecio =
                        view.findViewById<TextView>(
                            R.id.lblPrecio
                        )

                    val lblEstado =
                        view.findViewById<TextView>(
                            R.id.lblEstado
                        )

                    val lblStock =
                        view.findViewById<TextView>(
                            R.id.lblStock
                        )

                    val imgProducto =
                        view.findViewById<ImageView>(
                            R.id.imgProducto
                        )

                    val btnAgregar =
                        view.findViewById<Button>(
                            R.id.btnAgregar
                        )

                    // =========================
                    // DATOS
                    // =========================

                    val nombre =
                        obj.getString("nombre_producto")

                    val precio =
                        obj.getString("precio")

                    val estado =
                        obj.getString("estado")

                    val stock =
                        obj.getString("stock")

                    val imagen =
                        obj.getString("imagen")

                    lblNombreProducto.text = nombre

                    lblPrecio.text = "$$precio"

                    lblEstado.text = estado

                    lblStock.text = "Stock: $stock"

                    // =========================
                    // IMAGEN
                    // =========================

                    val urlImagen =
                        "http://192.168.1.3/cafetines_api/" + imagen

                    Glide.with(this)
                        .load(urlImagen)
                        .placeholder(R.drawable.logo_ues)
                        .error(R.drawable.logo_ues)
                        .into(imgProducto)

                    // =========================
                    // BOTON AGREGAR
                    // =========================

                    btnAgregar.setOnClickListener {

                        val url = "http://192.168.1.3/cafetines_api/carrito/agregar.php"

                        val request = object : StringRequest(
                            Request.Method.POST,
                            url,
                            { response ->

                                Toast.makeText(
                                    this,
                                    "Agregado al carrito",
                                    Toast.LENGTH_SHORT
                                ).show()

                            },
                            { error ->
                                Toast.makeText(
                                    this,
                                    "Error al agregar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            override fun getParams(): Map<String, String> {
                                return mapOf(

                                    "id_usuario" to "1", // luego lo haces dinámico
                                    "id_producto" to obj.getInt("id_producto").toString(),
                                    "cantidad" to "1"

                                )
                            }
                        }

                        Volley.newRequestQueue(this).add(request)
                    }

                    containerProductos.addView(view)
                }
            },

            {
                Toast.makeText(
                    this,
                    "Error al cargar productos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Volley.newRequestQueue(this)
            .add(request)
    }
}