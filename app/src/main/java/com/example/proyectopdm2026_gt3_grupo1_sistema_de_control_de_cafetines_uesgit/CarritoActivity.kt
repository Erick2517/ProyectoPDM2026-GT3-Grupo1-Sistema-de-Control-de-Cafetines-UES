package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
class CarritoActivity : AppCompatActivity() {
    private lateinit var containerCarrito: LinearLayout
    private lateinit var lblTotalPagar: TextView
    private val BASE_URL = "http://192.168.1.3/cafetines_api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPedido)

        containerCarrito = findViewById(R.id.containerCarrito)
        lblTotalPagar = findViewById(R.id.lblTotalPagar)

        btnBack.setOnClickListener { finish() }

        btnConfirmar.setOnClickListener {
            startActivity(Intent(this, PagoActivity::class.java))
        }

        cargarCarrito()
    }

    private fun cargarCarrito() {

        containerCarrito.removeAllViews()

        val url = BASE_URL + "carrito/listar.php?id_usuario=1"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                val array = JSONArray(response)
                var total = 0.0

                for (i in 0 until array.length()) {

                    val obj = array.getJSONObject(i)

                    val view = LayoutInflater.from(this)
                        .inflate(R.layout.item_carrito, containerCarrito, false)

                    val img = view.findViewById<ImageView>(R.id.imgProductoCarrito)
                    val nombre = view.findViewById<TextView>(R.id.lblNombreProductoCarrito)
                    val precio = view.findViewById<TextView>(R.id.lblPrecioCarrito)
                    val cantidad = view.findViewById<TextView>(R.id.lblCantidad)
                    val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

                    nombre.text = obj.getString("nombre_producto")
                    precio.text = "$${obj.getDouble("precio")}"
                    cantidad.text = "Cantidad: ${obj.getInt("cantidad")}"


                    val imagen = obj.optString("imagen", "").trim()

                    val urlImg = when {
                        imagen.startsWith("http") -> imagen
                        imagen.isNotEmpty() -> BASE_URL + imagen.replace("\\", "").removePrefix("/")
                        else -> ""
                    }

                    if (urlImg.isNotEmpty()) {
                        Glide.with(this)
                            .load(urlImg)
                            .placeholder(R.drawable.logo_ues)
                            .error(R.drawable.logo_ues)
                            .into(img)
                    } else {
                        img.setImageResource(R.drawable.logo_ues)
                    }

                    total += obj.optDouble("subtotal", 0.0)


                    val id = obj.optInt("id", -1)

                    btnEliminar.setOnClickListener {
                        if (id != -1) eliminarProducto(id)
                    }

                    containerCarrito.addView(view)
                }

                lblTotalPagar.text = "$${"%.2f".format(total)}"
            },
            { it.printStackTrace() }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun eliminarProducto(id: Int) {

        val url = BASE_URL + "carrito/eliminar.php"

        val request = object : StringRequest(
            Method.POST,
            url,
            {
                cargarCarrito()
            },
            { it.printStackTrace() }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf("id" to id.toString())
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarCarrito()
    }
}