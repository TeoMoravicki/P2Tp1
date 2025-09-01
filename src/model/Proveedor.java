package model;
import java.util.ArrayList;
import java.util.List;

public class Proveedor extends Persona {
    private String razonSocial;
    private String nif;
    private List<Object> productosSuministrados; // Puede contener Producto o Servicio

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
}