package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.R
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.model.Usuario

class UsuarioAdapter(
    private val listaUsuarios: List<Usuario>
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.tvNombre)
        val email = view.findViewById<TextView>(R.id.tvEmail)
        val rol = view.findViewById<TextView>(R.id.tvRol)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {

        val usuario = listaUsuarios[position]

        holder.nombre.text = usuario.nombre
        holder.email.text = usuario.email

        holder.rol.text = when (usuario.idRol) {
            1 -> "Usuario"
            2 -> "Administrador"
            3 -> "Encargado"
            else -> "Desconocido"
        }

        //  BOTÓN EDITAR
        holder.btnEditar.setOnClickListener {

            val intent = android.content.Intent(
                holder.itemView.context,
                com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.EditarUsuarioActivity::class.java
            )

            intent.putExtra("id", usuario.idUsuario)
            intent.putExtra("nombre", usuario.nombre)
            intent.putExtra("email", usuario.email)
            intent.putExtra("carnet", usuario.carnet)
            intent.putExtra("rol", usuario.idRol)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaUsuarios.size
}