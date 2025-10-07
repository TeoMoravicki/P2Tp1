package display.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import model.Departamento;
import model.Empleado;
import model.SistemaGestion;

public class DepartamentoDialog extends JDialog {
    private SistemaGestion sistema;
    private Departamento departamento;
    private String nombreOriginal; // para identificar al editar
    private boolean guardado = false;

    private JTextField txtNombre;
    private JTextField txtPresupuesto;
    private JComboBox<Empleado> cmbResponsable;

    public DepartamentoDialog(Frame owner, SistemaGestion sistema, Departamento departamentoExistente) {
        super(owner, departamentoExistente == null ? "Agregar Departamento" : "Editar Departamento", true);
        this.sistema = sistema;
        this.departamento = departamentoExistente;
        this.nombreOriginal = departamentoExistente != null ? departamentoExistente.getNombre() : null;
        initDialog();
    }

    private void initDialog() {
        setLayout(new BorderLayout());
        setSize(420, 300);
        setLocationRelativeTo(getOwner());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtNombre = new JTextField(20);
        txtPresupuesto = new JTextField(20);

        // Preparar combo de responsables: permitimos "Sin asignar" (valor null)
        Empleado[] empleados = sistema.getEmpleados().toArray(new Empleado[0]);
        Empleado[] empleadosConNull = new Empleado[empleados.length + 1];
        empleadosConNull[0] = null;
        System.arraycopy(empleados, 0, empleadosConNull, 1, empleados.length);
        cmbResponsable = new JComboBox<>(empleadosConNull);
        cmbResponsable.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Sin asignar");
                } else if (value instanceof Empleado) {
                    setText(((Empleado) value).getNombre());
                }
                return this;
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Presupuesto:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPresupuesto, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbResponsable, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = crearBoton("💾 Guardar", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("❌ Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> {
            if (validarFormulario()) {
                guardarDepartamento();
            }
        });
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (departamento != null) {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        txtNombre.setText(departamento.getNombre());
        txtPresupuesto.setText(String.valueOf(departamento.getPresupuesto()));
        Empleado resp = departamento.getResponsable();
        if (resp == null) {
            cmbResponsable.setSelectedIndex(0);
        } else {
            // buscar el empleado en el combo por referencia/dni
            for (int i = 0; i < cmbResponsable.getItemCount(); i++) {
                Empleado it = cmbResponsable.getItemAt(i);
                if (it != null && Objects.equals(it.getDni(), resp.getDni())) {
                    cmbResponsable.setSelectedIndex(i);
                    break;
                }
            }
        }
        // opcional: bloquear edición del nombre si querés (descomentar)
        // txtNombre.setEditable(false);
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String pres = txtPresupuesto.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        // Validar nombre único (si es creación o si cambia el nombre)
        if (departamento == null || !nombre.equals(nombreOriginal)) {
            for (Departamento d : sistema.getDepartamentos()) {
                if (d.getNombre().equalsIgnoreCase(nombre)) {
                    mostrarError("Ya existe un departamento con el nombre: " + nombre);
                    return false;
                }
            }
        }

        try {
            double presupuesto = Double.parseDouble(pres);
            if (presupuesto < 0) {
                mostrarError("El presupuesto no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarError("El presupuesto debe ser un número válido");
            return false;
        }

        return true;
    }

    private void guardarDepartamento() {
        String nombre = txtNombre.getText().trim();
        double presupuesto = Double.parseDouble(txtPresupuesto.getText().trim());
        Empleado responsable = (Empleado) cmbResponsable.getSelectedItem();

        Departamento nuevo = new Departamento(nombre, presupuesto, responsable);

        if (departamento == null) {
            // creación
            sistema.agregarDepartamento(nuevo);
        } else {
            // edición: reemplazamos el objeto en la lista buscando por nombreOriginal
            for (int i = 0; i < sistema.getDepartamentos().size(); i++) {
                Departamento d = sistema.getDepartamentos().get(i);
                if (d.getNombre().equals(nombreOriginal)) {
                    sistema.getDepartamentos().set(i, nuevo);
                    break;
                }
            }
        }

        guardado = true;
        dispose();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { boton.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { boton.setBackground(color); }
        });
        return boton;
    }

    public boolean isGuardado() {
        return guardado;
    }
}
