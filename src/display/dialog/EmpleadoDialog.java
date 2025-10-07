package display.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import enums.PuestoEmpleado;
import model.Departamento;
import model.Empleado;
import model.SistemaGestion;

public class EmpleadoDialog extends JDialog {
    private SistemaGestion sistema;
    private Empleado empleado;
    private boolean guardado = false;

    private JTextField txtNombre;
    private JTextField txtDomicilio;
    private JTextField txtDNI;
    private JTextField txtTelefono;
    private JTextField txtSalario;
    private JComboBox<PuestoEmpleado> cmbPuesto;
    private JSpinner spinnerFechaIngreso;
    private JComboBox<Departamento> cmbDepartamento;

    // Constructor que usa EmpleadoPanel: new EmpleadoDialog(sistema, empleado)
    public EmpleadoDialog(SistemaGestion sistema, Empleado empleadoExistente) {
        super((Frame) null, empleadoExistente == null ? "Agregar Empleado" : "Editar Empleado", true);
        this.sistema = sistema;
        this.empleado = empleadoExistente;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(520, 420);
        setLocationRelativeTo(getOwner());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtNombre = new JTextField(20);
        txtDomicilio = new JTextField(20);
        txtDNI = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtSalario = new JTextField(20);

        cmbPuesto = new JComboBox<>(PuestoEmpleado.values());

        spinnerFechaIngreso = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFechaIngreso, "dd/MM/yyyy");
        spinnerFechaIngreso.setEditor(dateEditor);

        Departamento[] departamentosArray = sistema.getDepartamentos().toArray(new Departamento[0]);
        cmbDepartamento = new JComboBox<>(departamentosArray);
        cmbDepartamento.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Departamento) {
                    setText(((Departamento) value).getNombre());
                } else {
                    setText("");
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
        formPanel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDomicilio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("DNI:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDNI, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Puesto:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbPuesto, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Salario:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSalario, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Fecha Ingreso:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(spinnerFechaIngreso, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Departamento:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbDepartamento, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = crearBoton("Guardar", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (empleado != null) {
            cargarDatosEmpleado();
        }
    }

    private void cargarDatosEmpleado() {
        txtNombre.setText(empleado.getNombre());
        txtDomicilio.setText(empleado.getDomicilio());
        txtDNI.setText(empleado.getDni());
        txtDNI.setEditable(false); // No permitir cambiar DNI en edición
        txtTelefono.setText(empleado.getTelefono());
        txtSalario.setText(String.valueOf(empleado.getSalario()));
        cmbPuesto.setSelectedItem(empleado.getPuesto());
        if (empleado.getFechaIngreso() != null) {
            spinnerFechaIngreso.setValue(empleado.getFechaIngreso());
        }
        if (empleado.getDepartamento() != null) {
            cmbDepartamento.setSelectedItem(empleado.getDepartamento());
        }
    }

    private void guardarEmpleado() {
        if (!validarFormulario()) return;

        try {
            String nombre = txtNombre.getText().trim();
            String domicilio = txtDomicilio.getText().trim();
            String dni = txtDNI.getText().trim();
            String telefono = txtTelefono.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            PuestoEmpleado puesto = (PuestoEmpleado) cmbPuesto.getSelectedItem();
            Date fechaIngreso = (Date) spinnerFechaIngreso.getValue();
            Departamento departamento = (Departamento) cmbDepartamento.getSelectedItem();

            if (empleado == null) {
                // crear nuevo
                Empleado nuevoEmpleado = new Empleado(
                        nombre,
                        domicilio,
                        dni,
                        telefono,
                        salario,
                        puesto,
                        fechaIngreso,
                        departamento
                );
                sistema.agregarEmpleado(nuevoEmpleado);
            } else {
                // edición in-place usando setters (mantiene referencias)
                empleado.setNombre(nombre);
                empleado.setDomicilio(domicilio);
                // DNI no se modifica
                empleado.setTelefono(telefono);
                empleado.setSalario(salario);
                empleado.setPuesto(puesto);
                empleado.setFechaIngreso(fechaIngreso);
                empleado.setDepartamento(departamento);
            }

            guardado = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El salario debe ser un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el empleado: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        if (txtDNI.getText().trim().isEmpty()) {
            mostrarError("El DNI es obligatorio");
            return false;
        }

        // Verificar DNI único solo para nuevos empleados
        if (empleado == null) {
            for (Empleado e : sistema.getEmpleados()) {
                if (e.getDni().equals(txtDNI.getText().trim())) {
                    mostrarError("Ya existe un empleado con el DNI: " + txtDNI.getText().trim());
                    return false;
                }
            }
        }

        try {
            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario < 0) {
                mostrarError("El salario no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El salario debe ser un número válido");
            return false;
        }

        if (cmbPuesto.getSelectedItem() == null) {
            mostrarError("El puesto es obligatorio");
            return false;
        }

        if (cmbDepartamento.getSelectedItem() == null) {
            mostrarError("El departamento es obligatorio");
            return false;
        }

        Date fecha = (Date) spinnerFechaIngreso.getValue();
        if (fecha.after(new Date())) {
            mostrarError("La fecha de ingreso no puede ser futura");
            return false;
        }

        return true;
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    public boolean isGuardado() {
        return guardado;
    }
}
