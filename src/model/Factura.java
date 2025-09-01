package model;
import enums.FormaPago;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Factura {
    private String numero;
    private Date fechaEmision;
    private Cliente cliente;
    private Empleado empleado;
    private List<ItemComercial> items;
    private FormaPago formaPago;
    private Pago pago;

    public Factura(String numero, Date fechaEmision, Cliente cliente,
                   Empleado empleado, FormaPago formaPago) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empleado = empleado;
        this.formaPago = formaPago;
        this.items = new ArrayList<>();
        this.cliente.agregarFactura(this);
    }


    public void agregarItem(ItemComercial item) {
        items.add(item);
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemComercial item : items) {
            total += item.getPrecio();
        }
        return total;
    }

    public void mostrarDetalle() {
        System.out.println("=== FACTURA #" + numero + " ===");
        System.out.println("Fecha: " + fechaEmision);
        System.out.println("Cliente: " + cliente.getNombre() + " (" + cliente.getCategoria() + ")");
        System.out.println("Empleado: " + empleado.getNombre() + " (" + empleado.getPuesto() + ")");
        System.out.println("Departamento: " + empleado.getDepartamento().getNombre());
        System.out.println("\nITEMS:");
        for (ItemComercial item : items) {
            System.out.println("- " + item.getNombre() + ": $" + item.getPrecio());
        }
        System.out.println("\nTotal: $" + calcularTotal());
        System.out.println("Forma de pago: " + formaPago);
        if (pago != null) {
            System.out.println("Estado de pago: " + pago.getEstado());
        }
        System.out.println("=============================");
    }

    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public List<ItemComercial> getItems() { return items; }
    public FormaPago getFormaPago() { return formaPago; }
    public Pago getPago() { return pago; }
}
