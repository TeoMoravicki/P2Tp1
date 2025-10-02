package services;

import model.Departamento;
import model.Empleado;
import model.SistemaGestion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class DepartamentoService {
    private final SistemaGestion sistema;
    private final Scanner scanner;

    public DepartamentoService(SistemaGestion sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void gestionar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE DEPARTAMENTOS ---");
            System.out.println("1. Crear departamento");
            System.out.println("2. Listar departamentos");
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
            } catch(InputMismatchException e){
                System.out.println("Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void crear() {
        System.out.println("\n--- CREAR NUEVO DEPARTAMENTO ---");
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Presupuesto: ");
            double presupuesto = scanner.nextDouble();
            scanner.nextLine();

            Empleado responsable = null;
            if (!sistema.getEmpleados().isEmpty()) {
                System.out.println("Seleccione un responsable:");
                for (int i = 0; i < sistema.getEmpleados().size(); i++) {
                    System.out.println((i + 1) + ". " + sistema.getEmpleados().get(i).getNombre());
                }
                System.out.print("Seleccione el número del responsable (0 para ninguno): ");
                int seleccion = scanner.nextInt();
                scanner.nextLine();

                if (seleccion > 0 && seleccion <= sistema.getEmpleados().size()) {
                    responsable = sistema.getEmpleados().get(seleccion - 1);
                }
            }

            Departamento nuevoDepartamento = new Departamento(nombre, presupuesto, responsable);
            sistema.agregarDepartamento(nuevoDepartamento);
            System.out.println("Departamento creado exitosamente.");

        } catch(InputMismatchException e){
            System.out.println("Por favor, ingrese algo válido.");
            scanner.nextLine();
        }
    }

    public void listar() {
        System.out.println("\n--- LISTA DE DEPARTAMENTOS ---");
        if (sistema.getDepartamentos().isEmpty()) {
            System.out.println("No hay departamentos registrados.");
            return;
        }

        for (int i = 0; i < sistema.getDepartamentos().size(); i++) {
            Departamento d = sistema.getDepartamentos().get(i);
            System.out.println((i + 1) + ". " + d.getNombre() +
                    " - Presupuesto: $" + d.getPresupuesto() +
                    " - Responsable: " +
                    (d.getResponsable() != null ? d.getResponsable().getNombre() : "Sin asignar"));
        }
    }
}
