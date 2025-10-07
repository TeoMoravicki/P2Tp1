package display.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import enums.TipoProductoServicio;
import model.Producto;
import model.Proveedor;
import model.SistemaGestion;

public class ProductoDialog extends JDialog {
    private SistemaGestion sistema;
    private Producto producto; // null = creación, no-null = edición
    private boolean guardado = false;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<TipoProductoServicio> cmbTipo;
    private JComboBox<Proveedor> cmbProveedor;

    public ProductoDialog(Frame owner, SistemaGestion sistema, Producto productoExistente) {
        super(owner, productoExistente == null ? "Agregar Producto" : "Editar Producto", true);
        this.sistema = sistema;
        this.producto = productoExistente;
        initDialog();
    }

    private void initDialog() {
        setLayout(new BorderLayout());
        setSize(480, 380);
        setLocationRelativeTo(getOwner());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtCodigo = new JTextField(20);
        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(20);
        cmbTipo = new JComboBox<>(TipoProductoServicio.values());
        Proveedor[] proveedores = sistema.getProveedores().toArray(new Proveedor[0]);
        cmbProveedor = new JComboBox<>(proveedores);
        cmbProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proveedor) {
                    setText(((Proveedor) value).getNombre());
                } else {
                    setText("");
                }
                return this;
            }
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Código:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCodigo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Precio:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPrecio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tipo:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbTipo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Proveedor:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbProveedor, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = crearBoton("Guardar", Color.decode("#2E8B57"));
        JButton btnCancelar = crearBoton("Cancelar", Color.decode("#DC143C"));

        btnGuardar.addActionListener(e -> {
            if (validarFormulario()) {
                guardarProducto();
            }
        });
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (producto != null) {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        txtCodigo.setText(producto.getCodigo());
        txtCodigo.setEditable(false); // no permitimos cambiar el código en edición
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        cmbTipo.setSelectedItem(producto.getTipo());

        // seleccionar proveedor por referencia / dni
        Proveedor prov = producto.getProveedor();
        if (prov != null) {
            for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
                Proveedor p = cmbProveedor.getItemAt(i);
                if (p != null && Objects.equals(p.getDni(), prov.getDni())) {
                    cmbProveedor.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean validarFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        Proveedor proveedor = (Proveedor) cmbProveedor.getSelectedItem();

        if (codigo.isEmpty()) {
            mostrarError("El código es obligatorio");
            return false;
        }
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }
        if (proveedor == null) {
            mostrarError("Debe seleccionar un proveedor");
            return false;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            if (precio < 0) {
                mostrarError("El precio no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarError("El precio debe ser un número válido");
            return false;
        }

        // Verificar unicidad de código solo en creación (o si se permitiera cambiarlo)
        if (producto == null) {
            for (Producto p : sistema.getProductos()) {
                if (p.getCodigo().equalsIgnoreCase(codigo)) {
                    mostrarError("Ya existe un producto con el código: " + codigo);
                    return false;
                }
            }
        }

        return true;
    }

    private void guardarProducto() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        double precio = Double.parseDouble(txtPrecio.getText().trim());
        TipoProductoServicio tipo = (TipoProductoServicio) cmbTipo.getSelectedItem();
        Proveedor proveedor = (Proveedor) cmbProveedor.getSelectedItem();

        if (producto == null) {
            // Creación
            Producto nuevo = new Producto(codigo, nombre, precio, tipo, proveedor);
            sistema.agregarProducto(nuevo);
        } else {
            // Edición in-place (usando setters)
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setTipo(tipo);
            producto.setProveedor(proveedor);
            // si quisieras permitir cambiar código en edición: producto.setCodigo(codigo)
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

    public boolean isGuardado() { return guardado; }
}
