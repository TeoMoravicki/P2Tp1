package display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.SistemaGestion;
import model.Departamento;
import model.Empleado;

public class DepartamentoPanel extends JPanel {
    private SistemaGestion sistema;
    private JTable departamentosTable;
    private DefaultTableModel tableModel;

    public DepartamentoPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarDepartamentos();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Departamentos"));

        JButton btnAgregar = crearBoton("Agregar Departamento", Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar Departamento", Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar Departamento", Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));

        btnAgregar.addActionListener(e -> agregarDepartamento());
        btnEditar.addActionListener(e -> editarDepartamento());
        btnEliminar.addActionListener(e -> eliminarDepartamento());
        btnActualizar.addActionListener(e -> cargarDepartamentos());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Departamentos"));

        String[] columnNames = {"Nombre", "Presupuesto", "Responsable"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        departamentosTable = new JTable(tableModel);
        departamentosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departamentosTable.setRowHeight(30);
        departamentosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(departamentosTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void agregarDepartamento() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.DepartamentoDialog dialog = new display.dialog.DepartamentoDialog(owner, sistema, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDepartamentos();
            mostrarExito("Departamento creado exitosamente");
        }
    }


    private boolean validarYGuardarDepartamento(String nombre, String presupuesto, Empleado responsable) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        try {
            double presupuestoVal = Double.parseDouble(presupuesto);
            if (presupuestoVal < 0) {
                mostrarError("El presupuesto no puede ser negativo");
                return false;
            }

            Departamento nuevoDepartamento = new Departamento(nombre.trim(), presupuestoVal, responsable);
            sistema.agregarDepartamento(nuevoDepartamento);
            cargarDepartamentos();
            mostrarExito("Departamento creado exitosamente");
            return true;

        } catch (NumberFormatException e) {
            mostrarError("El presupuesto debe ser un número válido");
            return false;
        }
    }

    private void editarDepartamento() {
        int selectedRow = departamentosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un departamento para editar");
            return;
        }

        String nombre = (String) tableModel.getValueAt(selectedRow, 0);
        Departamento departamento = buscarDepartamentoPorNombre(nombre);

        if (departamento != null) {
            mostrarDialogoEdicion(departamento);
        }
    }

    private void eliminarDepartamento() {
        int selectedRow = departamentosTable.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor seleccione un departamento para eliminar");
            return;
        }

        String nombre = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el departamento?\n\n" +
                        "Nombre: " + nombre,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getDepartamentos().removeIf(departamento -> departamento.getNombre().equals(nombre));
            cargarDepartamentos();
            mostrarExito("Departamento eliminado exitosamente");
        }
    }

    private void cargarDepartamentos() {
        tableModel.setRowCount(0);

        for (Departamento departamento : sistema.getDepartamentos()) {
            Object[] rowData = {
                    departamento.getNombre(),
                    String.format("$%,.2f", departamento.getPresupuesto()),
                    departamento.getResponsable() != null ? departamento.getResponsable().getNombre() : "Sin asignar"
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() > 0) {
            departamentosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            departamentosTable.getColumnModel().getColumn(0).setPreferredWidth(200);
            departamentosTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            departamentosTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        }
    }

    private Departamento buscarDepartamentoPorNombre(String nombre) {
        for (Departamento departamento : sistema.getDepartamentos()) {
            if (departamento.getNombre().equals(nombre)) {
                return departamento;
            }
        }
        return null;
    }

    private void mostrarDialogoEdicion(Departamento departamento) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        display.dialog.DepartamentoDialog dialog = new display.dialog.DepartamentoDialog(owner, sistema, departamento);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDepartamentos();
            mostrarExito("Departamento actualizado exitosamente");
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