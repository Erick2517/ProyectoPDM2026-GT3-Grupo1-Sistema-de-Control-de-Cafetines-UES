package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    fun hayInternet(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val actNetwork = cm.getNetworkCapabilities(network) ?: return false

        return actNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}