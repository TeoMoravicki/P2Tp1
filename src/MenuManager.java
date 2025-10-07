import java.util.InputMismatchException;
import java.util.Scanner;

import model.SistemaGestion;
import services.DepartamentoService;
import services.EmpleadoService;
import services.ProveedorService;
import services.ClienteService;
import services.ProductoService;
import services.ServicioService;
import services.FacturaService;

public class MenuManager {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    private final DepartamentoService departamentoService;
    private final EmpleadoService    empleadoService;
    private final ProveedorService   proveedorService;
    private final ClienteService     clienteService;
    private final ProductoService    productoService;
    private final ServicioService    servicioService;
    private final FacturaService     facturaService;

    public MenuManager(SistemaGestion sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);

        this.departamentoService = new DepartamentoService(sistema, scanner);
        this.empleadoService    = new EmpleadoService(sistema, scanner);
        this.proveedorService   = new ProveedorService(sistema, scanner);
        this.clienteService     = new ClienteService(sistema, scanner);
        this.productoService    = new ProductoService(sistema, scanner);
        this.servicioService    = new ServicioService(sistema, scanner);
        this.facturaService     = new FacturaService(sistema, scanner);
    }

    public void mostrarMenuPrincipal() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n=== SISTEMA DE GESTIÓN EMPRESARIAL ===");
            System.out.println("1. Gestionar Departamentos");
            System.out.println("2. Gestionar Empleados");
            System.out.println("3. Gestionar Proveedores");
            System.out.println("4. Gestionar Clientes");
            System.out.println("5. Gestionar Productos");
            System.out.println("6. Gestionar Servicios");
            System.out.println("7. Gestionar Facturas");
            System.out.println("8. Imprimir TODAS las facturas (PDF)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> departamentoService.gestionar();
                    case 2 -> empleadoService.gestionar();
                    case 3 -> proveedorService.gestionar();
                    case 4 -> clienteService.gestionar();
                    case 5 -> productoService.gestionar();
                    case 6 -> servicioService.gestionar();
                    case 7 -> facturaService.gestionar();
                    case 8 -> facturaService.imprimirTodasPdf(); // << cambio
                    case 0 -> System.out.println("Saliendo del sistema...");
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }
}
