import enums.*;
import model.*;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Crear departamentos
        Departamento departamentoVentas = new Departamento("Ventas", 50000, null);

        // Crear empleados
        Empleado empleado1 = new Empleado("Carlos Gómez", "Calle 123", "12345678", "555-1234",
                3000, PuestoEmpleado.GERENTE, new Date(), departamentoVentas);

        // Actualizar el responsable del departamento
        departamentoVentas = new Departamento("Ventas", 50000, empleado1);

        // Crear proveedores
        Proveedor proveedor1 = new Proveedor("TecnoSuministros", "Av. Principal 456", "87654321", "555-4321",
                "TecnoSuministros S.A.", "A12345678");

        // Crear clientes
        Cliente cliente1 = new Cliente("María López", "Calle Secundaria 789", "11223344", "555-1122",
                10000, CategoriaCliente.PREMIUM);

        // Crear productos y servicios
        Producto producto1 = new Producto("P001", "Laptop HP", 1200,
                TipoProductoServicio.VENTA_INSUMOS, proveedor1);

        Servicio servicio1 = new Servicio("S001", "Mantenimiento anual", 500,
                TipoProductoServicio.SERVICIO_TECNICO, proveedor1);

        // Crear factura
        Factura factura1 = new Factura("F-2023-001", new Date(), cliente1, empleado1, FormaPago.TARJETA);
        factura1.agregarItem(producto1);
        factura1.agregarItem(servicio1);

        // Crear pago
        Pago pago1 = new Pago(factura1.calcularTotal(), new Date(), FormaPago.TARJETA, EstadoPago.CANCELADO);
        factura1.setPago(pago1);

        // Mostrar detalle de la factura
        factura1.mostrarDetalle();
    }
}