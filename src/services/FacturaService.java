package services;

import enums.EstadoPago;
import enums.FormaPago;
import model.*;

import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FacturaService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public FacturaService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE FACTURAS ---");
            System.out.println("1. Crear factura");
            System.out.println("2. Listar facturas");
            System.out.println("3. Mostrar detalle de factura");
            System.out.println("4. Mostrar todas las facturas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 3 -> detalle();
                    case 4 -> mostrarTodas();
                    case 0 -> { }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVA FACTURA ---");

        if (sistema.getClientes().isEmpty() || sistema.getEmpleados().isEmpty() ||
                (sistema.getProductos().isEmpty() && sistema.getServicios().isEmpty())) {
            System.out.println("No hay suficientes datos para crear una factura.");
            System.out.println("Necesita al menos un cliente, un empleado y un producto o servicio.");
            return;
        }
        try {
            System.out.print("Número de factura: ");
            String numero = scanner.nextLine();

            System.out.println("Seleccione el cliente:");
            for (int i = 0; i < sistema.getClientes().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getClientes().get(i).getNombre());
            }
            System.out.print("Seleccione el número del cliente: ");
            int clienteIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            Cliente cliente = sistema.getClientes().get(clienteIndex);

            System.out.println("Seleccione el empleado:");
            for (int i = 0; i < sistema.getEmpleados().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getEmpleados().get(i).getNombre());
            }
            System.out.print("Seleccione el número del empleado: ");
            int empleadoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            Empleado empleado = sistema.getEmpleados().get(empleadoIndex);

            System.out.println("Seleccione la forma de pago:");
            FormaPago[] formasPago = FormaPago.values();
            for (int i = 0; i < formasPago.length; i++) {
                System.out.println((i + 1) + ". " + formasPago[i]);
            }
            System.out.print("Seleccione el número de la forma de pago: ");
            int pagoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            FormaPago formaPago = formasPago[pagoIndex];

            Factura nuevaFactura = new Factura(numero, new Date(), cliente, empleado, formaPago);

            int opcionItem = -1;
            while (opcionItem != 0) {
                System.out.println("\n¿Qué desea agregar?");
                System.out.println("1. Producto");
                System.out.println("2. Servicio");
                System.out.println("0. Finalizar");
                System.out.print("Seleccione una opción: ");

                opcionItem = scanner.nextInt();
                scanner.nextLine();

                if (opcionItem == 1 && !sistema.getProductos().isEmpty()) {
                    System.out.println("Seleccione el producto:");
                    for (int i = 0; i < sistema.getProductos().size(); i++) {
                        Producto p = sistema.getProductos().get(i);
                        System.out.println((i + 1) + ". " + p.getNombre() + " - $" + p.getPrecio());
                    }
                    System.out.print("Seleccione el número del producto: ");
                    int productoIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    nuevaFactura.agregarItem(sistema.getProductos().get(productoIndex));
                    System.out.println("Producto agregado.");
                } else if (opcionItem == 2 && !sistema.getServicios().isEmpty()) {
                    System.out.println("Seleccione el servicio:");
                    for (int i = 0; i < sistema.getServicios().size(); i++) {
                        Servicio s = sistema.getServicios().get(i);
                        System.out.println((i + 1) + ". " + s.getNombre() + " - $" + s.getPrecio());
                    }
                    System.out.print("Seleccione el número del servicio: ");
                    int servicioIndex = scanner.nextInt() - 1;
                    scanner.nextLine();
                    nuevaFactura.agregarItem(sistema.getServicios().get(servicioIndex));
                    System.out.println("Servicio agregado.");
                } else if (opcionItem != 0) {
                    System.out.println("Opción no válida o no hay elementos disponibles.");
                }
            }

            System.out.println("Total a pagar: $" + nuevaFactura.calcularTotal());
            System.out.println("Seleccione el estado del pago:");
            EstadoPago[] estadosPago = EstadoPago.values();
            for (int i = 0; i < estadosPago.length; i++) {
                System.out.println((i + 1) + ". " + estadosPago[i]);
            }
            System.out.print("Seleccione el número del estado: ");
            int estadoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            EstadoPago estado = estadosPago[estadoIndex];

            Pago pago = new Pago(nuevaFactura.calcularTotal(), new Date(), formaPago, estado);
            nuevaFactura.setPago(pago);

            sistema.agregarFactura(nuevaFactura);
            System.out.println("Factura creada exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE FACTURAS ---");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            System.out.println((i + 1) + ". Factura #" + f.getNumero() +
                    " - Cliente: " + f.getCliente().getNombre() +
                    " - Total: $" + f.calcularTotal() +
                    " - Estado: " + (f.getPago()!=null ? f.getPago().getEstado() : "SIN PAGO"));
        }
    }

    public void detalle() {
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }

        System.out.println("Seleccione la factura a mostrar:");
        for (int i = 0; i < sistema.getFacturas().size(); i++) {
            Factura f = sistema.getFacturas().get(i);
            System.out.println((i + 1) + ". Factura #" + f.getNumero() +
                    " - Cliente: " + f.getCliente().getNombre());
        }
        System.out.print("Seleccione el número de la factura: ");
        int facturaIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (facturaIndex >= 0 && facturaIndex < sistema.getFacturas().size()) {
            sistema.getFacturas().get(facturaIndex).mostrarDetalle();
        } else {
            System.out.println("Número de factura no válido.");
        }
    }

    public void mostrarTodas() {
        System.out.println("\n=== TODAS LAS FACTURAS ===");
        if (sistema.getFacturas().isEmpty()) {
            System.out.println("No hay facturas registradas.");
            return;
        }
        for (Factura factura : sistema.getFacturas()) {
            factura.mostrarDetalle();
        }
    }
}
