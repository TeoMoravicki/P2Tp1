package model;
import java.util.ArrayList;
import java.util.List;

public class Proveedor extends Persona {
    private String razonSocial;
    private String nif;
    private final List<Object> productosSuministrados; // Puede contener Producto o Servicio

    public Proveedor(String nombre, String domicilio, String dni, String telefono,
                     String razonSocial, String nif) {
        super(nombre, domicilio, dni, telefono);
        this.razonSocial = razonSocial;
        this.nif = nif;
        this.productosSuministrados = new ArrayList<>();
    }

    public void agregarProductoSuministrado(Object producto) {
        productosSuministrados.add(producto);
    }

    // Getters
    public String getRazonSocial() { return razonSocial; }
    public String getNif() { return nif; }
    public List<Object> getProductosSuministrados() { return productosSuministrados; }

    // Setters para permitir edición en sitio
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public void setNif(String nif) { this.nif = nif; }

    @Override
    public String toString() {
        return nombre;
    }
}
