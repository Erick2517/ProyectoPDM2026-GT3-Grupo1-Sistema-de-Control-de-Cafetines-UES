package com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.util

import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.CarritoItem
import com.example.proyectopdm2026_gt3_grupo1_sistema_de_control_de_cafetines_uesgit.domain.model.Producto

object CarritoManager {
    private val items = mutableListOf<CarritoItem>()
    private var idLocal: Int? = null
    private var nombreLocal: String? = null

    fun agregarProducto(producto: Producto, nombreLocalProducto: String): OperationResult<CarritoItem> {
        if (producto.disponibilidad != AppConstants.DISPONIBILIDAD_DISPONIBLE || producto.stock <= 0) {
            return OperationResult.Error("El producto no está disponible.")
        }

        if (idLocal != null && idLocal != producto.idLocal) {
            return OperationResult.Error("El carrito contiene productos de otro local. Vacíelo para iniciar un pedido nuevo.")
        }

        if (items.isEmpty()) {
            idLocal = producto.idLocal
            nombreLocal = nombreLocalProducto
        }

        val index = items.indexOfFirst { it.producto.idProducto == producto.idProducto }
        if (index == -1) {
            val item = CarritoItem(producto = producto, cantidad = 1)
            items.add(item)
            return OperationResult.Success(item)
        }

        val itemActual = items[index]
        if (itemActual.cantidad >= producto.stock) {
            return OperationResult.Error("No hay stock suficiente para agregar más unidades.")
        }

        val itemActualizado = itemActual.copy(cantidad = itemActual.cantidad + 1)
        items[index] = itemActualizado
        return OperationResult.Success(itemActualizado)
    }

    fun aumentarCantidad(idProducto: Int): OperationResult<CarritoItem> {
        val index = items.indexOfFirst { it.producto.idProducto == idProducto }
        if (index == -1) return OperationResult.Error("El producto no está en el carrito.")

        val itemActual = items[index]
        if (itemActual.cantidad >= itemActual.producto.stock) {
            return OperationResult.Error("No hay stock suficiente para aumentar la cantidad.")
        }

        val itemActualizado = itemActual.copy(cantidad = itemActual.cantidad + 1)
        items[index] = itemActualizado
        return OperationResult.Success(itemActualizado)
    }

    fun disminuirCantidad(idProducto: Int): OperationResult<CarritoItem?> {
        val index = items.indexOfFirst { it.producto.idProducto == idProducto }
        if (index == -1) return OperationResult.Error("El producto no está en el carrito.")

        val itemActual = items[index]
        if (itemActual.cantidad <= 1) {
            items.removeAt(index)
            limpiarLocalSiCarritoVacio()
            return OperationResult.Success(null)
        }

        val itemActualizado = itemActual.copy(cantidad = itemActual.cantidad - 1)
        items[index] = itemActualizado
        return OperationResult.Success(itemActualizado)
    }

    fun quitarProducto(idProducto: Int) {
        items.removeAll { it.producto.idProducto == idProducto }
        limpiarLocalSiCarritoVacio()
    }

    fun vaciarCarrito() {
        items.clear()
        idLocal = null
        nombreLocal = null
    }

    fun obtenerItems(): List<CarritoItem> {
        return items.toList()
    }

    fun obtenerNombreLocal(): String? {
        return nombreLocal
    }

    fun calcularTotal(): Double {
        return items.sumOf { it.subtotal }
    }

    fun estaVacio(): Boolean {
        return items.isEmpty()
    }

    private fun limpiarLocalSiCarritoVacio() {
        if (items.isEmpty()) {
            idLocal = null
            nombreLocal = null
        }
    }
}
