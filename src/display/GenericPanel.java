package display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.SistemaGestion;

public abstract class GenericPanel<T> extends JPanel {
    protected SistemaGestion sistema;
    protected JTable table;
    protected DefaultTableModel tableModel;

    public GenericPanel(SistemaGestion sistema) {
        this.sistema = sistema;
        initializePanel();
        cargarDatos();
    }

    // MÉTODOS COMUNES IMPLEMENTADOS
    protected void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createTopPanel(getTituloGestion()), BorderLayout.NORTH);
        add(createCenterPanel(getTituloLista()), BorderLayout.CENTER);
    }

    protected JPanel createTopPanel(String titulo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        JButton btnAgregar = crearBoton("Agregar " + getNombreEntidad(), Color.decode("#2E8B57"));
        JButton btnEditar = crearBoton("Editar " + getNombreEntidad(), Color.decode("#1E90FF"));
        JButton btnEliminar = crearBoton("Eliminar " + getNombreEntidad(), Color.decode("#DC143C"));
        JButton btnActualizar = crearBoton("Actualizar", Color.decode("#696969"));

        btnAgregar.addActionListener(e -> agregar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> cargarDatos());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    protected JPanel createCenterPanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        tableModel = new DefaultTableModel(getColumnNames(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // MÉTODOS AUXILIARES COMUNES
    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    protected JButton crearBoton(String texto, Color color) {
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

    // MÉTODOS ABSTRACTOS (cada panel implementa su lógica específica)
    protected abstract String[] getColumnNames();
    protected abstract String getTituloGestion();
    protected abstract String getTituloLista();
    protected abstract String getNombreEntidad();
    protected abstract void agregar();
    protected abstract void editar();
    protected abstract void eliminar();
    protected abstract void cargarDatos();
}
