package display;

import javax.swing.*;
import java.awt.*;

import display.dialog.ClienteDialog;
import model.SistemaGestion;
import model.Cliente;
import enums.CategoriaCliente;

public class ClientePanel extends GenericPanel<Cliente> {

    public ClientePanel(SistemaGestion sistema) {
        super(sistema);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Nombre", "DNI", "Teléfono", "Categoría", "Límite Crédito", "Domicilio"};
    }

    @Override
    protected String getTituloGestion() {
        return "Gestión de Clientes";
    }

    @Override
    protected String getTituloLista() {
        return "Lista de Clientes";
    }

    @Override
    protected String getNombreEntidad() {
        return "Cliente";
    }

    @Override
    protected void agregar() {
        ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this), sistema, null);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarDatos();
            mostrarExito("Cliente creado exitosamente");
        }
    }

    @Override
    protected void editar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un cliente para editar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        Cliente cliente = buscarClientePorDNI(dni);

        if (cliente != null) {
            ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this), sistema, cliente);
            dialog.setVisible(true);

            if (dialog.isGuardado()) {
                cargarDatos();
                mostrarExito("Cliente actualizado exitosamente");
            }
        }
    }

    @Override
    protected void eliminar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un cliente para eliminar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        String nombre = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al cliente?\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "DNI: " + dni,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getClientes().removeIf(cliente -> cliente.getDni().equals(dni));
            cargarDatos();
            mostrarExito("Cliente eliminado exitosamente");
        }
    }

    @Override
    protected void cargarDatos() {
        tableModel.setRowCount(0);

        for (Cliente cliente : sistema.getClientes()) {
            Object[] rowData = {
                    cliente.getNombre(),
                    cliente.getDni(),
                    cliente.getTelefono(),
                    cliente.getCategoria().toString(),
                    String.format("$%,.2f", cliente.getLimiteCredito()),
                    cliente.getDomicilio()
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
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
    }

    private Cliente buscarClientePorDNI(String dni) {
        return sistema.getClientes().stream()
                .filter(cliente -> cliente.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }
}