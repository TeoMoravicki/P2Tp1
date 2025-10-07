package model;

import enums.TipoProductoServicio;

public abstract class ItemComercial {
    protected String codigo;
    protected String nombre;
    protected double precio;
    protected TipoProductoServicio tipo;
    protected Proveedor proveedor;

    public ItemComercial(String codigo, String nombre, double precio,
                         TipoProductoServicio tipo, Proveedor proveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
        this.proveedor = proveedor;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public TipoProductoServicio getTipo() { return tipo; }
    public Proveedor getProveedor() { return proveedor; }

    // Setters (permiten edición in-place)
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setTipo(TipoProductoServicio tipo) { this.tipo = tipo; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    @Override
    public String toString() {
        return nombre;
    }
}
