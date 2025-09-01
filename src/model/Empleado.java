package model;
import enums.PuestoEmpleado;

import java.util.Date;

public class Empleado extends Persona {
    private double salario;
    private PuestoEmpleado puesto;
    private Date fechaIngreso;
    private Departamento departamento;

    public Empleado(String nombre, String domicilio, String dni, String telefono,
                    double salario, PuestoEmpleado puesto, Date fechaIngreso, Departamento departamento) {
        super(nombre, domicilio, dni, telefono);
        this.salario = salario;
        this.puesto = puesto;
        this.fechaIngreso = fechaIngreso;
        this.departamento = departamento;
    }


    public double getSalario() { return salario; }
    public PuestoEmpleado getPuesto() { return puesto; }
    public Date getFechaIngreso() { return fechaIngreso; }
    public Departamento getDepartamento() { return departamento; }
}