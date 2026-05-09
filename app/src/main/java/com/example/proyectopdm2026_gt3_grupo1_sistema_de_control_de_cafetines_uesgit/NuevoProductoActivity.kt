package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.GestionarLocalesActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Local
class NuevoProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText

    private lateinit var spTipo: Spinner
    private lateinit var spLocal: Spinner

    private lateinit var imgPreview: ImageView
    private lateinit var layoutImagen: LinearLayout

    private var imageUri: Uri? = null
    private var imagenBase64: String = ""

    private var listaLocales = mutableListOf<Local>()

    companion object {
        private const val PICK_IMAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevo_producto)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        etNombre = findViewById(R.id.etNombre)
        etPrecio = findViewById(R.id.etPrecio)
        etStock = findViewById(R.id.etStock)

        spTipo = findViewById(R.id.spTipo)
        spLocal = findViewById(R.id.spLocal)

        imgPreview = findViewById(R.id.imgPreview)
        layoutImagen = findViewById(R.id.layoutSubirImagen)

        btnBack.setOnClickListener {
            finish()
        }

        layoutImagen.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, PICK_IMAGE)
        }

        btnGuardar.setOnClickListener {
            insertarProducto()
        }

        cargarLocales()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // =========================
    // IMAGEN
    // =========================
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {

            imageUri = data.data
            imgPreview.setImageURI(imageUri)

            imagenBase64 = convertirImagenBase64(imageUri!!)
        }
    }

    private fun convertirImagenBase64(uri: Uri): String {

        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()

        return android.util.Base64.encodeToString(
            bytes,
            android.util.Base64.DEFAULT
        )
    }

    // =========================
    // INSERTAR PRODUCTO
    // =========================
    private fun insertarProducto() {

        val url = "http://192.168.1.3/cafetines_api/productos/upload_producto.php"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            {
                Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf(

                    "nombre_producto" to etNombre.text.toString(),
                    "precio" to etPrecio.text.toString(),
                    "stock" to etStock.text.toString(),
                    "estado" to spTipo.selectedItem.toString(),
                    "id_local" to listaLocales[spLocal.selectedItemPosition].idLocal.toString(),
                    "imagen" to imagenBase64
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    // =========================
    // CARGAR LOCALES
    // =========================
    private fun cargarLocales() {

        val url = "http://192.168.1.3/cafetines_api/locales/get_locales.php"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                val json = org.json.JSONArray(response)

                listaLocales.clear()
                val nombres = mutableListOf<String>()

                for (i in 0 until json.length()) {

                    val obj = json.getJSONObject(i)

                    val local = Local(
                        obj.getInt("id_local"),
                        obj.getString("nombre_local"),
                        "",
                        "",
                        "",
                        0,
                        ""
                    )

                    listaLocales.add(local)
                    nombres.add(local.nombreLocal)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombres
                )

                spLocal.adapter = adapter
            },
            {
                it.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}