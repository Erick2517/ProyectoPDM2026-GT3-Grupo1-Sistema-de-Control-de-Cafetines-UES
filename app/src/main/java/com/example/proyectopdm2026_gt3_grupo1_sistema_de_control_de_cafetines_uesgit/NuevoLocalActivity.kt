package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Local
class NuevoLocalActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var btnBack: ImageView


    private lateinit var txtNombreLocal: EditText
    private lateinit var txtUbicacion: EditText
    private lateinit var txtDescripcion: EditText

    private lateinit var swEntrega: Switch

    private lateinit var spEstadoLocal: Spinner

    private lateinit var btnGuardarLocal: Button

    // CARD IMAGEN
    private lateinit var cardSubirImagen: CardView

    // PREVIEW
    private lateinit var imgPreview: ImageView

    // URI
    private var imageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_nuevo_local)

        // INICIALIZAR VISTAS

        inicializarVistas()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.estados_local,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spEstadoLocal.adapter = adapter


        // BOTÓN VOLVER

        btnBack.setOnClickListener {
            finish()
        }


        // SUBIR IMAGEN
        // Lógica para abrir la galería del teléfono
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

        // GUARDAR
//  Al presionar Guardar, se intenta el registro en la nube (API)
        // =========================
// GUARDAR
// =========================
        btnGuardarLocal.setOnClickListener {

            val nombre =
                txtNombreLocal.text.toString().trim()

            val ubicacion =
                txtUbicacion.text.toString().trim()

            val descripcion =
                txtDescripcion.text.toString().trim()

            if (nombre.isEmpty()) {

                txtNombreLocal.error =
                    "Ingresa el nombre"

                txtNombreLocal.requestFocus()

                return@setOnClickListener
            }

            if (ubicacion.isEmpty()) {

                txtUbicacion.error =
                    "Ingresa la ubicación"

                txtUbicacion.requestFocus()

                return@setOnClickListener
            }

            if (descripcion.isEmpty()) {

                txtDescripcion.error =
                    "Ingresa la descripción"

                txtDescripcion.requestFocus()

                return@setOnClickListener
            }

            if (imageUri == null) {

                Toast.makeText(
                    this,
                    "Selecciona una imagen",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            registrarLocalOnline()
        }

        // EDGE TO EDGE

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
    // INICIALIZAR VISTAS
    // =========================
    private fun inicializarVistas() {

        btnBack =
            findViewById(R.id.btnBack)

        txtNombreLocal =
            findViewById(R.id.txtNombreLocal)

        txtUbicacion =
            findViewById(R.id.txtUbicacion)

        txtDescripcion =
            findViewById(R.id.txtDescripcion)

        swEntrega =
            findViewById(R.id.swEntrega)

        spEstadoLocal =
            findViewById(R.id.spEstadoLocal)

        btnGuardarLocal =
            findViewById(R.id.btnGuardarLocal)

        cardSubirImagen =
            findViewById(R.id.cardSubirImagen)

        imgPreview =
            findViewById(R.id.imgPreview)
    }

    // =========================
    // RECIBIR IMAGEN
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

            // MOSTRAR PREVIEW
            imgPreview.visibility = View.VISIBLE

            imgPreview.setImageURI(imageUri)

            Toast.makeText(
                this,
                "Imagen seleccionada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * PROCESO ONLINE: Envía los datos y la IMAGEN al servidor PHP
     */
    private fun registrarLocalOnline() {

        // Validación: No podemos enviar un local sin foto a la API
        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.3/cafetines_api/locales/create_local.php"

        val inputStream = contentResolver.openInputStream(imageUri!!)
        val bytes = inputStream!!.readBytes()

        val imagenBase64 = android.util.Base64.encodeToString(
            bytes,
            android.util.Base64.DEFAULT
        )

        val request = object : com.android.volley.toolbox.StringRequest(
            Method.POST,
            url,

            { response ->

                Toast.makeText(
                    this,
                    "Local registrado",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            },

            { error ->

                Toast.makeText(
                    this,
                    "Error de conexión",
                    Toast.LENGTH_SHORT
                ).show()

                error.printStackTrace()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf(

                    "nombre_local" to txtNombreLocal.text.toString(),

                    "ubicacion" to txtUbicacion.text.toString(),

                    "descripcion" to txtDescripcion.text.toString(),

                    "imagen" to imagenBase64,

                    "entrega_campus" to
                            if (swEntrega.isChecked) "1" else "0",

                    "estado" to
                            spEstadoLocal.selectedItem.toString()
                )
            }
        }
        com.android.volley.toolbox.Volley
            .newRequestQueue(this)
            .add(request)
    }

    // GUARDAR LOCAL
    /**
     * PROCESO LOCAL: Guarda la información en la base de datos interna SQLite
     */

    private fun ejecutarGuardado() {

        val nombre =
            txtNombreLocal.text
                .toString()
                .trim()

        val ubicacion =
            txtUbicacion.text
                .toString()
                .trim()

        val descripcion =
            txtDescripcion.text
                .toString()
                .trim()

        val ofreceEntrega =
            swEntrega.isChecked

        val estado =
            spEstadoLocal.selectedItem.toString()

        // VALIDACIONES

        if (nombre.isEmpty()) {

            txtNombreLocal.error =
                "Ingresa el nombre"

            txtNombreLocal.requestFocus()

            return
        }

        if (ubicacion.isEmpty()) {

            txtUbicacion.error =
                "Ingresa la ubicación"

            txtUbicacion.requestFocus()

            return
        }

        if (descripcion.isEmpty()) {

            txtDescripcion.error =
                "Ingresa la descripción"

            txtDescripcion.requestFocus()

            return
        }

        if (imageUri == null) {

            Toast.makeText(
                this,
                "Selecciona una imagen",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // CREAR OBJETO

        val local = Local(

            nombreLocal = nombre,

            ubicacion = ubicacion,

            descripcion = descripcion,

            imagen = imageUri.toString(),

            entregaCampus = if (ofreceEntrega) 1 else 0,

            estado = estado
        )

        // GUARDAR EN SQLITE

        val db = DatabaseHelper(this)

        val resultado =
            db.insertarLocal(local)


        // RESULTADO

        if (resultado) {

            Toast.makeText(
                this,
                "Local guardado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "Error al guardar local o nombre duplicado",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}