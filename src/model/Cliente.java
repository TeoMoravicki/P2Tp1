package model;
import enums.CategoriaCliente;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {
    private double limiteCredito;
    private List<Factura> historialCompras;
    private CategoriaCliente categoria;
    private String email;

    public Cliente(String nombre, String domicilio, String dni, String telefono,
                   double limiteCredito, CategoriaCliente categoria, String email) {
        super(nombre, domicilio, dni, telefono);
        this.limiteCredito = limiteCredito;
        this.categoria = categoria;
        this.historialCompras = new ArrayList<>();
        this.email = email;
    }

    public void agregarFactura(Factura factura) {
        historialCompras.add(factura);
    }

    // Getter
    public String getEmail() {return email;}
    public double getLimiteCredito() { return limiteCredito; }
    public CategoriaCliente getCategoria() { return categoria; }
    public List<Factura> getHistorialCompras() { return historialCompras; }

    // Setters para edición en sitio
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }
    public void setCategoria(CategoriaCliente categoria) { this.categoria = categoria; }
    public void setEmail(String email) {this.email = email;}

    @Override
    public String toString() {
        return nombre;
    }
}
