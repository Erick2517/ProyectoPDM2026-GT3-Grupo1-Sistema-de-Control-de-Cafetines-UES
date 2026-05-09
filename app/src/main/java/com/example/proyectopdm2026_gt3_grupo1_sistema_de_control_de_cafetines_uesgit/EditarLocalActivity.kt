package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarLocalActivity : AppCompatActivity() {

    // =========================
    // VARIABLES
    // =========================
    private lateinit var txtNombre: EditText
    private lateinit var txtUbicacion: EditText
    private lateinit var txtDescripcion: EditText

    private lateinit var spEstado: Spinner

    private lateinit var btnGuardar: Button

    private lateinit var imgPreview: ImageView

    private lateinit var cardSubirImagen: CardView

    // =========================
    // IMAGENES
    // =========================
    private var imagenActual = ""

    private var imageUri: Uri? = null

    // =========================
    // ID LOCAL
    // =========================
    private var idLocal = ""

    companion object {
        private const val PICK_IMAGE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_local)

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }


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
        // =========================
        // INICIALIZAR VISTAS
        // =========================
        txtNombre =
            findViewById(R.id.txtNombreLocal)

        txtUbicacion =
            findViewById(R.id.txtUbicacion)

        txtDescripcion =
            findViewById(R.id.txtDescripcion)

        spEstado =
            findViewById(R.id.spEstadoLocal)

        btnGuardar =
            findViewById(R.id.btnGuardarLocal)

        imgPreview =
            findViewById(R.id.imgPreview)

        cardSubirImagen =
            findViewById(R.id.cardSubirImagen)

        // =========================
        // SPINNER
        // =========================
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.estados_local,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spEstado.adapter = adapter

        // =========================
        // RECIBIR DATOS
        // =========================
        idLocal =
            intent.getStringExtra("id_local").toString()

        txtNombre.setText(
            intent.getStringExtra("nombre")
        )

        txtUbicacion.setText(
            intent.getStringExtra("ubicacion")
        )

        txtDescripcion.setText(
            intent.getStringExtra("descripcion")
        )

        val estado =
            intent.getStringExtra("estado")

        imagenActual =
            intent.getStringExtra("imagen") ?: ""

        // =========================
        // SELECCIONAR ESTADO
        // =========================
        if (estado == "Activo") {

            spEstado.setSelection(0)

        } else {

            spEstado.setSelection(1)
        }

        // =========================
        // MOSTRAR IMAGEN ACTUAL
        // =========================
        // =========================
// MOSTRAR IMAGEN ACTUAL
// =========================


        if (
            imagenActual.isNotEmpty() &&
            imagenActual.startsWith("http")
        ) {

            imgPreview.visibility = View.VISIBLE

            Glide.with(this)
                .load(imagenActual.trim())
                .error(R.drawable.logo_ues)
                .into(imgPreview)

        }

        // =========================
        // CAMBIAR IMAGEN
        // =========================
        cardSubirImagen.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(
                intent,
                PICK_IMAGE
            )
        }

        // =========================
        // GUARDAR
        // =========================
        btnGuardar.setOnClickListener {

            actualizarLocal()
        }
    }

    // =========================
    // RECIBIR NUEVA IMAGEN
    // =========================
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == PICK_IMAGE &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            imageUri = data.data

            imgPreview.visibility = View.VISIBLE

            imgPreview.setImageURI(imageUri)

            Toast.makeText(
                this,
                "Imagen seleccionada",
                Toast.LENGTH_SHORT
            ).show()
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
    // ACTUALIZAR
    // =========================
    private fun actualizarLocal() {

        val url =
            "http://192.168.1.3/cafetines_api/locales/update_local.php"

        // =========================
        // IMAGEN FINAL
        // =========================
        val imagenFinal =

            if (imageUri != null) {

                convertirImagenBase64(imageUri!!)

            } else {

                imagenActual
            }

        val request = object : StringRequest(
            Request.Method.POST,
            url,

            {
                Toast.makeText(
                    this,
                    "Local actualizado",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            {
                Toast.makeText(
                    this,
                    "Error al actualizar",
                    Toast.LENGTH_SHORT
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf(

                    "id_local" to idLocal,

                    "nombre_local" to
                            txtNombre.text.toString(),

                    "ubicacion" to
                            txtUbicacion.text.toString(),

                    "descripcion" to
                            txtDescripcion.text.toString(),

                    "estado" to
                            spEstado.selectedItem.toString(),

                    "imagen" to imagenFinal
                )
            }
        }

        Volley.newRequestQueue(this)
            .add(request)
    }
}