package model;

import java.util.*;
import enums.*;
import service.EmailService;

public class SistemaGestion {
    private List<Departamento> departamentos;
    private List<Empleado> empleados;
    private List<Proveedor> proveedores;
    private List<Cliente> clientes;
    private List<Producto> productos;
    private List<Servicio> servicios;
    private List<Factura> facturas;
    private EmailService emailService;

    public SistemaGestion() {
        this.departamentos = new ArrayList<>();
        this.empleados = new ArrayList<>();
        this.proveedores = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.servicios = new ArrayList<>();
        this.facturas = new ArrayList<>();

        // Inicializar el servicio de email (configuración para desarrollo)
        this.emailService = new EmailService(
                "smtp.gmail.com",          // host
                "587",                     // port
                "teobenjaminmoravicki@gmail.com",      // user
                "buii ukwi xrom xvls",             // pass
                true                      // prod-test
        );

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

        // Crear cliente CON EMAIL
        Cliente cliente1 = new Cliente("María López", "Calle Secundaria 789", "11223344", "555-1122",
                10000, CategoriaCliente.PREMIUM, "teobenjaminmoravicki@gmail.com");
        clientes.add(cliente1);

        // Crear producto
        Producto producto1 = new Producto("P001", "Laptop HP", 1200,
                TipoProductoServicio.VENTA_INSUMOS, proveedor1);
        productos.add(producto1);

        // Crear servicio
        Servicio servicio1 = new Servicio("S001", "Mantenimiento anual", 500,
                TipoProductoServicio.SERVICIO_TECNICO, proveedor1);
        servicios.add(servicio1);

        // Crear una factura de ejemplo (sin pago inicial)
        Factura facturaEjemplo = new Factura(
                "F-001",
                new Date(),
                cliente1,
                empleado1,
                FormaPago.TARJETA
        );
        facturaEjemplo.agregarItem(producto1);
        facturaEjemplo.agregarItem(servicio1);
        facturas.add(facturaEjemplo);
    }

    // Getters para las listas
    public List<Departamento> getDepartamentos() {
        return new ArrayList<>(departamentos);
    }

    public List<Empleado> getEmpleados() {
        return new ArrayList<>(empleados);
    }

    public List<Proveedor> getProveedores() {
        return new ArrayList<>(proveedores);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public List<Servicio> getServicios() {
        return new ArrayList<>(servicios);
    }

    public List<Factura> getFacturas() {
        return new ArrayList<>(facturas);
    }

    // Getter para el servicio de email
    public EmailService getEmailService() {
        return emailService;
    }

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

    public boolean asignarResponsableDepartamento(Departamento departamento, Empleado nuevoResponsable) {
        if (departamento == null) return false;

        Empleado anterior = departamento.getResponsable();
        departamento.setResponsable(nuevoResponsable);

        // Actualizamos referencia del empleado responsable
        if (nuevoResponsable != null) {
            nuevoResponsable.setDepartamento(departamento);
            if (!empleados.contains(nuevoResponsable)) {
                empleados.add(nuevoResponsable);
            }
        }

        return true;
    }

    // Métodos de búsqueda útiles
    public Cliente buscarClientePorDNI(String dni) {
        return clientes.stream()
                .filter(cliente -> cliente.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }

    public Empleado buscarEmpleadoPorDNI(String dni) {
        return empleados.stream()
                .filter(empleado -> empleado.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }

    public Proveedor buscarProveedorPorDNI(String dni) {
        return proveedores.stream()
                .filter(proveedor -> proveedor.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }

    public Producto buscarProductoPorCodigo(String codigo) {
        return productos.stream()
                .filter(producto -> producto.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public Servicio buscarServicioPorCodigo(String codigo) {
        return servicios.stream()
                .filter(servicio -> servicio.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public Factura buscarFacturaPorNumero(String numero) {
        return facturas.stream()
                .filter(factura -> factura.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    // Método para configurar el servicio de email en producción
    public void configurarEmailService(String host, String port, String username, String password, boolean enabled) {
        this.emailService = new EmailService(host, port, username, password, enabled);
    }

    // Método para verificar si hay datos mínimos para crear facturas
    public boolean puedeCrearFacturas() {
        return !clientes.isEmpty() &&
                !empleados.isEmpty() &&
                (!productos.isEmpty() || !servicios.isEmpty());
    }

    // Método para obtener estadísticas del sistema
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("departamentos", departamentos.size());
        stats.put("empleados", empleados.size());
        stats.put("proveedores", proveedores.size());
        stats.put("clientes", clientes.size());
        stats.put("productos", productos.size());
        stats.put("servicios", servicios.size());
        stats.put("facturas", facturas.size());

        // Calcular facturas pagadas vs pendientes
        long facturasPagadas = facturas.stream()
                .filter(f -> f.getPago() != null && f.getPago().getEstado() == EstadoPago.PAGADO)
                .count();
        stats.put("facturasPagadas", (int) facturasPagadas);
        stats.put("facturasPendientes", facturas.size() - (int) facturasPagadas);

        return stats;
    }
}