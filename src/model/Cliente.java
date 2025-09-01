package model;
import enums.CategoriaCliente;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {
    private double limiteCredito;
    private List<Factura> historialCompras;
    private CategoriaCliente categoria;

    public Cliente(String nombre, String domicilio, String dni, String telefono,
                   double limiteCredito, CategoriaCliente categoria) {
        super(nombre, domicilio, dni, telefono);
        this.limiteCredito = limiteCredito;
        this.categoria = categoria;
        this.historialCompras = new ArrayList<>();
    }


    public void agregarFactura(Factura factura) {
        historialCompras.add(factura);
    }

    public double getLimiteCredito() { return limiteCredito; }
    public CategoriaCliente getCategoria() { return categoria; }
    public List<Factura> getHistorialCompras() { return historialCompras; }
}
