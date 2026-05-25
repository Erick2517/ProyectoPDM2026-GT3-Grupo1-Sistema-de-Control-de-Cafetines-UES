package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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
                val response = leerRespuesta(connection, responseCode)

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

    fun postForm(endpoint: String, params: Map<String, String>, callback: ApiCallback) {
        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                val url = URL(BASE_URL + endpoint)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                val body = crearCuerpoFormulario(params)

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(body)
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                val response = leerRespuesta(connection, responseCode)

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

    fun patchForm(endpoint: String, params: Map<String, String>, callback: ApiCallback) {
        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                val url = URL(BASE_URL + endpoint)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PATCH"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                val body = crearCuerpoFormulario(params)

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(body)
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                val response = leerRespuesta(connection, responseCode)

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

    private fun crearCuerpoFormulario(params: Map<String, String>): String {
        return params.entries.joinToString("&") { entry ->
            "${URLEncoder.encode(entry.key, "UTF-8")}=${
                URLEncoder.encode(entry.value, "UTF-8")
            }"
        }
    }

    private fun leerRespuesta(connection: HttpURLConnection, responseCode: Int): String {
        val inputStream = if (responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        if (inputStream == null) {
            return ""
        }

        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = reader.readText()
        reader.close()

        return response
    }
}