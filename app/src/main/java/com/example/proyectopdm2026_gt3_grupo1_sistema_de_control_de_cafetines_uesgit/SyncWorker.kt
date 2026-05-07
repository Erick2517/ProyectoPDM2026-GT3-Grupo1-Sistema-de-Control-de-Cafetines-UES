package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.sync

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.RequestFuture
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.DatabaseHelper
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SyncWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val URL_SYNC = "http://192.168.1.3/cafetines_api/auth/register.php"

    override fun doWork(): Result {

        val db = DatabaseHelper(applicationContext)
        val pendientes = db.obtenerPendientesSync()

        if (pendientes.isEmpty()) return Result.success()

        val queue = Volley.newRequestQueue(applicationContext)

        for (dato in pendientes) {

            try {
                val json = JSONObject(dato)

                val future = RequestFuture.newFuture<String>()

                val request = object : StringRequest(
                    Request.Method.POST,
                    URL_SYNC,
                    future,
                    future
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

                queue.add(request)

                val response = future.get(10, TimeUnit.SECONDS)

                val res = JSONObject(response)

                if (res.getBoolean("success")) {
                    db.marcarSincronizado()
                }

            } catch (e: Exception) {
                return Result.retry()
            }
        }

        return Result.success()
    }
}