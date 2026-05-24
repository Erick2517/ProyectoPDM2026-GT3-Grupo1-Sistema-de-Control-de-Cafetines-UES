package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.data.local.database

object DatabaseContract {
    const val DATABASE_NAME = "control_cafetines.db"
    const val DATABASE_VERSION = 6

    object Roles {
        const val TABLE_NAME = "Roles"
        const val ID_ROL = "id_rol"
        const val NOMBRE_ROL = "nombre_rol"
    }

    object Ubicaciones {
        const val TABLE_NAME = "Ubicaciones"
        const val ID_UBICACION = "id_ubicacion"
        const val NOMBRE_UBICACION = "nombre_ubicacion"
        const val DESCRIPCION = "descripcion"
    }

    object Usuarios {
        const val TABLE_NAME = "Usuarios"
        const val ID_USUARIO = "id_usuario"
        const val NOMBRE = "nombre"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CARNET = "carnet"
        const val ID_ROL = "id_rol"
        const val ID_UBICACION = "id_ubicacion"
        const val ID_LOCAL_ASIGNADO = "id_local_asignado"
        const val ACTIVO = "activo"
    }

    object Locales {
        const val TABLE_NAME = "Locales"
        const val ID_LOCAL = "id_local"
        const val NOMBRE_LOCAL = "nombre_local"
        const val UBICACION = "ubicacion"
        const val DESCRIPCION = "descripcion"
        const val ESTADO = "estado"
    }

    object Productos {
        const val TABLE_NAME = "Productos"
        const val ID_PRODUCTO = "id_producto"
        const val NOMBRE_PRODUCTO = "nombre_producto"
        const val PRECIO = "precio"
        const val DISPONIBILIDAD = "disponibilidad"
        const val TIPO = "tipo"
        const val STOCK = "stock"
        const val ID_LOCAL = "id_local"
    }

    object Pedidos {
        const val TABLE_NAME = "Pedidos"
        const val ID_PEDIDO = "id_pedido"
        const val FECHA_PEDIDO = "fecha_pedido"
        const val TIPO_PEDIDO = "tipo_pedido"
        const val ESTADO_PEDIDO = "estado_pedido"
        const val TOTAL = "total"
        const val ID_USUARIO = "id_usuario"
        const val ID_UBICACION = "id_ubicacion"
    }

    object DetallePedido {
        const val TABLE_NAME = "Detalle_Pedido"
        const val ID_DETALLE_PEDIDO = "id_detalle_pedido"
        const val ID_PEDIDO = "id_pedido"
        const val ID_PRODUCTO = "id_producto"
        const val CANTIDAD = "cantidad"
        const val PRECIO_UNITARIO = "precio_unitario"
        const val SUBTOTAL = "subtotal"
    }

    object Pagos {
        const val TABLE_NAME = "Pagos"
        const val ID_PAGO = "id_pago"
        const val ID_PEDIDO = "id_pedido"
        const val METODO_PAGO = "metodo_pago"
        const val MONTO = "monto"
        const val FECHA_PAGO = "fecha_pago"
        const val REFERENCIA = "referencia"
        const val ESTADO_PAGO = "estado_pago"
    }

    object PedidosEspeciales {
        const val TABLE_NAME = "Pedidos_Especiales"
        const val ID_PEDIDO_ESPECIAL = "id_pedido_especial"
        const val ID_PEDIDO = "id_pedido"
        const val DESCRIPCION_EVENTO = "descripcion_evento"
        const val FECHA_EVENTO = "fecha_evento"
        const val HORA_EVENTO = "hora_evento"
        const val NUMERO_PERSONAS = "numero_personas"
        const val MONTO_MINIMO = "monto_minimo"
        const val MONTO_MAXIMO = "monto_maximo"
        const val ANTICIPO = "anticipo"
        const val REFERENCIA_PAGO = "referencia_pago"
    }

    object OpcionesMenu {
        const val TABLE_NAME = "OpcionesMenu"
        const val ID_OPCION = "id_opcion"
        const val NOMBRE_OPCION = "nombre_opcion"
        const val DESCRIPCION_OPCION = "descripcion_opcion"
        const val ESTADO = "estado"
    }

    object RolesOpcionesMenu {
        const val TABLE_NAME = "Roles_OpcionesMenu"
        const val ID_ROL_OPCION = "id_rol_opcion"
        const val ID_ROL = "id_rol"
        const val ID_OPCION = "id_opcion"
    }
}
