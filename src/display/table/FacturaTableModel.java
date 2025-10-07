package display.table;

import javax.swing.table.DefaultTableModel;

public class FacturaTableModel extends DefaultTableModel {
    private static final String[] COLUMN_NAMES = {
            "Número", "Fecha", "Cliente", "Empleado", "Forma Pago",
            "Subtotal", "IVA", "Descuento", "Impuesto Extra", "Total", "Estado"
    };

    public FacturaTableModel() {
        super(COLUMN_NAMES, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}