package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import android.net.Uri
import android.widget.ImageView

object ImageViewLoader {
    fun cargarImagen(imageView: ImageView, imagenUri: String?, fallbackResId: Int) {
        if (imagenUri.isNullOrBlank()) {
            imageView.setImageResource(fallbackResId)
            return
        }

        try {
            imageView.setImageURI(Uri.parse(imagenUri))
        } catch (_: SecurityException) {
            imageView.setImageResource(fallbackResId)
        } catch (_: RuntimeException) {
            imageView.setImageResource(fallbackResId)
        }
    }
}
