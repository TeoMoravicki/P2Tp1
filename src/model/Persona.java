package model;

abstract class Persona {
    protected String nombre;
    protected String domicilio;
    protected String dni;
    protected String telefono;

    public Persona(String nombre, String domicilio, String dni, String telefono) {
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.dni = dni;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public String getDomicilio() { return domicilio; }
    public String getDni() { return dni; }
    public String getTelefono() { return telefono; }

    // Setters añadidos para permitir editar nombre, domicilio y teléfono desde diálogos
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
