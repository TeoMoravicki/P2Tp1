package display;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import display.dialog.EmpleadoDialog;
import model.*;
import enums.PuestoEmpleado;

public class EmpleadoPanel extends GenericPanel<Empleado> {

    public EmpleadoPanel(SistemaGestion sistema) {
        super(sistema);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Nombre", "DNI", "Teléfono", "Puesto", "Salario", "Departamento"};
    }

    @Override
    protected String getTituloGestion() {
        return "Gestión de Empleados";
    }

    @Override
    protected String getTituloLista() {
        return "Lista de Empleados";
    }

    @Override
    protected String getNombreEntidad() {
        return "Empleado";
    }

    @Override
    protected void agregar() {
        if (sistema.getDepartamentos().isEmpty()) {
            mostrarError("No hay departamentos registrados. Debe crear un departamento primero.");
            return;
        }

        EmpleadoDialog dialog = new EmpleadoDialog(sistema, null);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
            mostrarExito("Empleado creado exitosamente");
        }
    }

    @Override
    protected void editar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un empleado para editar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        Empleado empleado = buscarEmpleadoPorDNI(dni);

        if (empleado != null) {
            EmpleadoDialog dialog = new EmpleadoDialog(sistema, empleado);
            dialog.setVisible(true);

            if (dialog.isGuardado()) {
                cargarDatos();
                mostrarExito("Empleado actualizado exitosamente");
            }
        }
    }

    @Override
    protected void eliminar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un empleado para eliminar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        String nombre = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al empleado?\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "DNI: " + dni,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getEmpleados().removeIf(empleado -> empleado.getDni().equals(dni));
            cargarDatos();
            mostrarExito("Empleado eliminado exitosamente");
        }
    }

    @Override
    protected void cargarDatos() {
        tableModel.setRowCount(0);

        for (Empleado empleado : sistema.getEmpleados()) {
            Object[] rowData = {
                    empleado.getNombre(),
                    empleado.getDni(),
                    empleado.getTelefono(),
                    empleado.getPuesto().toString(),
                    String.format("$%,.2f", empleado.getSalario()),
                    empleado.getDepartamento().getNombre()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            configurarColumnasTabla();
        }
    }

    private void configurarColumnasTabla() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private Empleado buscarEmpleadoPorDNI(String dni) {
        return sistema.getEmpleados().stream()
                .filter(empleado -> empleado.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }
}