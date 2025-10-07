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

    public void setResponsable(Empleado responsable) {
        this.responsable = responsable;
    }

    // Nuevos setters para permitir edición en sitio
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }

    public String getNombre() { return nombre; }
    public double getPresupuesto() { return presupuesto; }
    public Empleado getResponsable() { return responsable; }

    @Override
    public String toString() {
        return nombre;
    }
}
