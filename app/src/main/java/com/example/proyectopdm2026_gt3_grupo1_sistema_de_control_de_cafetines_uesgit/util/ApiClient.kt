package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080"

    private val executor = Executors.newSingleThreadExecutor()

    interface ApiCallback {
        fun onSuccess(response: String)
        fun onError(error: String)
    }

    fun get(endpoint: String, callback: ApiCallback) {
        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                val url = URL(BASE_URL + endpoint)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                val reader = BufferedReader(
                    InputStreamReader(
                        if (responseCode in 200..299) {
                            connection.inputStream
                        } else {
                            connection.errorStream
                        }
                    )
                )

                val response = reader.readText()
                reader.close()

                if (responseCode in 200..299) {
                    callback.onSuccess(response)
                } else {
                    callback.onError("Error HTTP $responseCode: $response")
                }

            } catch (e: Exception) {
                callback.onError("Error de conexión: ${e.message}")
            } finally {
                connection?.disconnect()
            }
        }
    }
}