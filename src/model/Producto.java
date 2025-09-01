package model;

import enums.TipoProductoServicio;

public class Producto extends ItemComercial {
    public Producto(String codigo, String nombre, double precio,
                    TipoProductoServicio tipo, Proveedor proveedor) {
        super(codigo, nombre, precio, tipo, proveedor);
    }
}
