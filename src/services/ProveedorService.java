package services;

import model.Proveedor;
import model.SistemaGestion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ProveedorService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public ProveedorService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE PROVEEDORES ---");
            System.out.println("1. Crear proveedor");
            System.out.println("2. Listar proveedores");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crear();
                    case 2 -> listar();
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
        System.out.println("\n--- CREAR NUEVO PROVEEDOR ---");
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Domicilio: ");
            String domicilio = scanner.nextLine();

            System.out.print("DNI: ");
            String dni = scanner.nextLine();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine();

            System.out.print("Razón Social: ");
            String razonSocial = scanner.nextLine();

            System.out.print("NIF: ");
            String nif = scanner.nextLine();

            Proveedor nuevoProveedor = new Proveedor(nombre, domicilio, dni, telefono, razonSocial, nif);
            sistema.agregarProveedor(nuevoProveedor);
            System.out.println("Proveedor creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE PROVEEDORES ---");
        if (sistema.getProveedores().isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }
        for (int i = 0; i < sistema.getProveedores().size(); i++) {
            Proveedor p = sistema.getProveedores().get(i);
            System.out.println((i + 1) + ". " + p.getNombre() +
                    " - Razón Social: " + p.getRazonSocial() +
                    " - NIF: " + p.getNif());
        }
    }
}
