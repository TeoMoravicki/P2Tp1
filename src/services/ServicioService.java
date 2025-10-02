package services;

import enums.TipoProductoServicio;
import model.Proveedor;
import model.Servicio;
import model.SistemaGestion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ServicioService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public ServicioService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE SERVICIOS ---");
            System.out.println("1. Crear servicio");
            System.out.println("2. Listar servicios");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
                    case 0 -> { /* volver */ }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVO SERVICIO ---");

        if (sistema.getProveedores().isEmpty()) {
            System.out.println("No hay proveedores registrados. Debe crear un proveedor primero.");
            return;
        }
        try {
            System.out.print("Código: ");
            String codigo = scanner.nextLine();

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Precio: ");
            double precio = scanner.nextDouble();
            scanner.nextLine();

            TipoProductoServicio[] tipos = TipoProductoServicio.values();
            System.out.println("Seleccione el tipo:");
            for (int i = 0; i < tipos.length; i++) {
                System.out.println((i + 1) + ". " + tipos[i]);
            }
            System.out.print("Seleccione el número del tipo: ");
            int tipoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            TipoProductoServicio tipo = tipos[tipoIndex];

            System.out.println("Seleccione el proveedor:");
            for (int i = 0; i < sistema.getProveedores().size(); i++) {
                System.out.println((i + 1) + ". " + sistema.getProveedores().get(i).getNombre());
            }
            System.out.print("Seleccione el número del proveedor: ");
            int proveedorIndex = scanner.nextInt() - 1; // ahora lo paso a 0-based
            scanner.nextLine();
            Proveedor proveedor = sistema.getProveedores().get(proveedorIndex);

            Servicio nuevoServicio = new Servicio(codigo, nombre, precio, tipo, proveedor);
            sistema.agregarServicio(nuevoServicio);
            System.out.println("Servicio creado exitosamente.");
        } catch (InputMismatchException e) {
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine(); // Limpiar buffer
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Opción fuera de rango.");
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE SERVICIOS ---");
        if (sistema.getServicios().isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }

        for (int i = 0; i < sistema.getServicios().size(); i++) {
            Servicio s = sistema.getServicios().get(i);
            System.out.println((i + 1) + ". " + s.getNombre() +
                    " - Código: " + s.getCodigo() +
                    " - Precio: $" + s.getPrecio() +
                    " - Proveedor: " + s.getProveedor().getNombre());
        }
    }
}

