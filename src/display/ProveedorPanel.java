package display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import display.dialog.ProveedorDialog;
import model.SistemaGestion;
import model.Proveedor;

public class ProveedorPanel extends JPanel {
    private SistemaGestion sistema;
    private JTable proveedoresTable;
    private DefaultTableModel tableModel;

    public ProveedorPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarProveedores();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Proveedores"));

        JButton btnAgregar = crearBoton("Agregar Proveedor", Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar Proveedor", Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar Proveedor", Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));

        btnAgregar.addActionListener(e -> agregarProveedor());
        btnEditar.addActionListener(e -> editarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());
        btnActualizar.addActionListener(e -> cargarProveedores());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Proveedores"));

        String[] columnNames = {"Nombre", "DNI", "Teléfono", "Razón Social", "NIF"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        proveedoresTable = new JTable(tableModel);
        proveedoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proveedoresTable.setRowHeight(30);
        proveedoresTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(proveedoresTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void agregarProveedor() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ProveedorDialog dialog = new ProveedorDialog(owner, sistema, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarProveedores();
            mostrarExito("Proveedor creado exitosamente");
        }
    }


    private boolean validarYGuardarProveedor(String nombre, String domicilio, String dni,
                                             String telefono, String razonSocial, String nif) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        if (dni == null || dni.trim().isEmpty()) {
            mostrarError("El DNI es obligatorio");
            return false;
        }

        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            mostrarError("La razón social es obligatoria");
            return false;
        }

        if (nif == null || nif.trim().isEmpty()) {
            mostrarError("El NIF es obligatorio");
            return false;
        }

        for (Proveedor proveedor : sistema.getProveedores()) {
            if (proveedor.getDni().equals(dni)) {
                mostrarError("Ya existe un proveedor con el DNI: " + dni);
                return false;
            }
        }

        Proveedor nuevoProveedor = new Proveedor(nombre.trim(),
                domicilio.trim(),
                dni.trim(),
                telefono.trim(),
                razonSocial.trim(),
                nif.trim());
        sistema.agregarProveedor(nuevoProveedor);
        cargarProveedores();
        mostrarExito("Proveedor creado exitosamente");
        return true;
    }

    private void editarProveedor() {
        int selectedRow = proveedoresTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un proveedor para editar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        Proveedor proveedor = buscarProveedorPorDNI(dni);

        if (proveedor != null) {
            mostrarDialogoEdicion(proveedor);
        }
    }

    private void eliminarProveedor() {
        int selectedRow = proveedoresTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un proveedor para eliminar");
            return;
        }

        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        String nombre = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al proveedor?\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "DNI: " + dni,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getProveedores().removeIf(proveedor -> proveedor.getDni().equals(dni));
            cargarProveedores();
            mostrarExito("Proveedor eliminado exitosamente");
        }
    }

    private void cargarProveedores() {
        tableModel.setRowCount(0);

        for (Proveedor proveedor : sistema.getProveedores()) {
            Object[] rowData = {
                    proveedor.getNombre(),
                    proveedor.getDni(),
                    proveedor.getTelefono(),
                    proveedor.getRazonSocial(),
                    proveedor.getNif()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            proveedoresTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            proveedoresTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            proveedoresTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            proveedoresTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            proveedoresTable.getColumnModel().getColumn(3).setPreferredWidth(200);
            proveedoresTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        }
    }

    private Proveedor buscarProveedorPorDNI(String dni) {
        for (Proveedor proveedor : sistema.getProveedores()) {
            if (proveedor.getDni().equals(dni)) {
                return proveedor;
            }
        }
        return null;
    }

    private void mostrarDialogoEdicion(Proveedor proveedor) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ProveedorDialog dialog = new ProveedorDialog(owner, sistema, proveedor);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarProveedores();
            mostrarExito("Proveedor actualizado exitosamente");
        }
    }

    // Métodos auxiliares (iguales que en ClientePanel)
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setFont(new Font("Arial", Font.BOLD, 12));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }
}