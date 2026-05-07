package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.sync

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import org.json.JSONObject

class SyncManager(private val context: Context) {

    private val URL_SYNC = "http://192.168.1.3/cafetines_api/auth/register.php"

    fun sincronizar() {

        val db = DatabaseHelper(context)
        val pendientes = db.obtenerPendientesSync()

        if (pendientes.isEmpty()) return

        for (dato in pendientes) {

            val json = JSONObject(dato)

            val request = object : StringRequest(
                Request.Method.POST, URL_SYNC,
                {
                    db.marcarSincronizado()
                },
                {
                    it.printStackTrace()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "nombre" to json.getString("nombre"),
                        "email" to json.getString("email"),
                        "password" to json.getString("password"),
                        "carnet" to json.getString("carnet"),
                        "id_rol" to json.getInt("id_rol").toString()
                    )
                }
            }

            Volley.newRequestQueue(context).add(request)
        }
    }
}