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
    private FormaPago formaPago;
    private List<ItemComercial> items;
    private double descuento;         // en porcentaje (0 a 100)
    private double impuestoExtra;     // en porcentaje (0 a 100)
    private Pago pago;

    public Factura(String numero, Date fechaEmision, Cliente cliente, Empleado empleado, FormaPago formaPago) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empleado = empleado;
        this.formaPago = formaPago;
        this.items = new ArrayList<>();
        this.descuento = 0.0;
        this.impuestoExtra = 0.0;
    }

    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public FormaPago getFormaPago() { return formaPago; }
    public List<ItemComercial> getItems() { return items; }
    public Pago getPago() { return pago; }

    public void setPago(Pago pago) { this.pago = pago; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public double getImpuestoExtra() { return impuestoExtra; }
    public void setImpuestoExtra(double impuestoExtra) { this.impuestoExtra = impuestoExtra; }

    public double calcularSubtotal() {
        double sub = 0.0;
        for (ItemComercial item : items) {
            sub += item.getPrecio();
        }
        return sub;
    }

    public double calcularImpuestos() {
        return calcularSubtotal() * 0.21;
    }

    public double calcularImpuestoExtra() {
        return calcularSubtotal() * (impuestoExtra / 100.0);
    }

    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        double iva = calcularImpuestos();
        double extra = calcularImpuestoExtra();
        double bruto = subtotal + iva + extra;
        double descuentoAplicado = bruto * (descuento / 100.0);
        return bruto - descuentoAplicado;
    }


    public void agregarItem(ItemComercial item) {
        items.add(item);
    }
}
