package model;
import enums.EstadoPago;
import enums.FormaPago;

import java.util.Date;
public class Pago {
    private double monto;
    private Date fecha;
    private FormaPago formaPago;
    private EstadoPago estado;

    public Pago(double monto, Date fecha, FormaPago formaPago, EstadoPago estado) {
        this.monto = monto;
        this.fecha = fecha;
        this.formaPago = formaPago;
        this.estado = estado;
    }


    public double getMonto() { return monto; }
    public Date getFecha() { return fecha; }
    public FormaPago getFormaPago() { return formaPago; }
    public EstadoPago getEstado() { return estado; }
}