package model;

import enums.TipoProductoServicio;

public class Servicio extends ItemComercial {
    public Servicio(String codigo, String nombre, double precio,
                    TipoProductoServicio tipo, Proveedor proveedor) {
        super(codigo, nombre, precio, tipo, proveedor);
    }
}
