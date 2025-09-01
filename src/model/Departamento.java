package model;
public class Departamento {
    private String nombre;
    private double presupuesto;
    private Empleado responsable;

    public Departamento(String nombre, double presupuesto, Empleado responsable) {
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.responsable = responsable;
    }

    public String getNombre() { return nombre; }
    public double getPresupuesto() { return presupuesto; }
    public Empleado getResponsable() { return responsable; }
}