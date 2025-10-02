package services;

import enums.CategoriaCliente;
import model.Cliente;
import model.SistemaGestion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClienteService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public ClienteService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Crear cliente");
            System.out.println("2. Listar clientes");
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
        System.out.println("\n--- CREAR NUEVO CLIENTE ---");
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Domicilio: ");
            String domicilio = scanner.nextLine();

            System.out.print("DNI: ");
            String dni = scanner.nextLine();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine();

            System.out.print("Límite de Crédito: ");
            double limiteCredito = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Seleccione la categoría:");
            CategoriaCliente[] categorias = CategoriaCliente.values();
            for (int i = 0; i < categorias.length; i++) {
                System.out.println((i + 1) + ". " + categorias[i]);
            }
            System.out.print("Seleccione el número de la categoría: ");
            int categoriaIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            CategoriaCliente categoria = categorias[categoriaIndex];

            Cliente nuevoCliente = new Cliente(nombre, domicilio, dni, telefono, limiteCredito, categoria);
            sistema.agregarCliente(nuevoCliente);
            System.out.println("Cliente creado exitosamente.");
        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        if (sistema.getClientes().isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (int i = 0; i < sistema.getClientes().size(); i++) {
            Cliente c = sistema.getClientes().get(i);
            System.out.println((i + 1) + ". " + c.getNombre() +
                    " - DNI: " + c.getDni() +
                    " - Categoría: " + c.getCategoria() +
                    " - Límite de Crédito: $" + c.getLimiteCredito());
        }
    }
}
