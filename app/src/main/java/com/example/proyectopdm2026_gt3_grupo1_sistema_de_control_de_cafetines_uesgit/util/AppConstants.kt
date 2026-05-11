package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

object AppConstants {
    const val ROL_USUARIO = "Usuario"
    const val ROL_ADMINISTRADOR = "Administrador"
    const val ROL_ENCARGADO = "Encargado"

    const val ESTADO_ACTIVO = "Activo"
    const val ESTADO_INACTIVO = "Inactivo"

    const val TIPO_PRODUCTO_NORMAL = "Normal"
    const val TIPO_PRODUCTO_ANTOJITO = "Antojito"

    const val DISPONIBILIDAD_DISPONIBLE = "Disponible"
    const val DISPONIBILIDAD_NO_DISPONIBLE = "No disponible"

    const val TIPO_PEDIDO_RESERVA = "Reserva"
    const val TIPO_PEDIDO_ENTREGA = "Entrega"

    const val ESTADO_PEDIDO_PENDIENTE = "Pendiente"
    const val ESTADO_PEDIDO_PENDIENTE_PAGO = "Pendiente de pago"
    const val ESTADO_PEDIDO_PAGADO = "Pagado"
    const val ESTADO_PEDIDO_EN_PREPARACION = "En preparación"
    const val ESTADO_PEDIDO_LISTO = "Listo para entregar"
    const val ESTADO_PEDIDO_ENTREGADO = "Entregado"
    const val ESTADO_PEDIDO_CANCELADO = "Cancelado"

    const val METODO_PAGO_EFECTIVO = "Efectivo"
    const val METODO_PAGO_TARJETA = "Tarjeta"
    const val METODO_PAGO_BITCOIN = "Bitcoin"

    const val ESTADO_PAGO_REGISTRADO = "Registrado"
    const val ESTADO_PAGO_CONFIRMADO = "Confirmado"
    const val ESTADO_PAGO_RECHAZADO = "Rechazado"

    const val MAX_ANTOJITOS_POR_PEDIDO = 3
    const val HORA_INICIO_ANTOJITOS = 14
    const val HORA_FIN_ANTOJITOS = 16

    const val EXTRA_ID_LOCAL = "extra_id_local"
    const val EXTRA_NOMBRE_LOCAL = "extra_nombre_local"
    const val EXTRA_ID_PEDIDO = "extra_id_pedido"
    const val EXTRA_MODO_EDICION_LOCAL = "extra_modo_edicion_local"
}
