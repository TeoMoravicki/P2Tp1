import java.util.*;
import enums.*;
import model.*;

public class SistemaGestion {
    private List<Departamento> departamentos;
    private List<Empleado> empleados;
    private List<Proveedor> proveedores;
    private List<Cliente> clientes;
    private List<Producto> productos;
    private List<Servicio> servicios;
    private List<Factura> facturas;

    public SistemaGestion() {
        this.departamentos = new ArrayList<>();
        this.empleados = new ArrayList<>();
        this.proveedores = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.servicios = new ArrayList<>();
        this.facturas = new ArrayList<>();
        cargarDatosEjemplo();
    }

    private void cargarDatosEjemplo() {
        // Crear departamento
        Departamento departamentoVentas = new Departamento("Ventas", 50000, null);
        departamentos.add(departamentoVentas);

        // Crear empleado
        Empleado empleado1 = new Empleado("Carlos Gómez", "Calle 123", "12345678", "555-1234",
                3000, PuestoEmpleado.GERENTE, new Date(), departamentoVentas);
        empleados.add(empleado1);

        // Actualizar el responsable del departamento
        departamentoVentas.setResponsable(empleado1);

        // Crear proveedor
        Proveedor proveedor1 = new Proveedor("TecnoSuministros", "Av. Principal 456", "87654321", "555-4321",
                "TecnoSuministros S.A.", "A12345678");
        proveedores.add(proveedor1);

        // Crear cliente
        Cliente cliente1 = new Cliente("María López", "Calle Secundaria 789", "11223344", "555-1122",
                10000, CategoriaCliente.PREMIUM);
        clientes.add(cliente1);

        // Crear producto
        Producto producto1 = new Producto("P001", "Laptop HP", 1200,
                TipoProductoServicio.VENTA_INSUMOS, proveedor1);
        productos.add(producto1);

        // Crear servicio
        Servicio servicio1 = new Servicio("S001", "Mantenimiento anual", 500,
                TipoProductoServicio.SERVICIO_TECNICO, proveedor1);
        servicios.add(servicio1);
    }

    // Getters para las listas
    public List<Departamento> getDepartamentos() { return departamentos; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Proveedor> getProveedores() { return proveedores; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Producto> getProductos() { return productos; }
    public List<Servicio> getServicios() { return servicios; }
    public List<Factura> getFacturas() { return facturas; }

    // Métodos para agregar elementos
    public void agregarDepartamento(Departamento departamento) {
        departamentos.add(departamento);
    }

    public void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
    }

    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
    }

    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void agregarServicio(Servicio servicio) {
        servicios.add(servicio);
    }

    public void agregarFactura(Factura factura) {
        facturas.add(factura);
    }
}