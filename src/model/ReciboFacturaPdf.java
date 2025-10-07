package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class ReciboFacturaPdf implements DocumentoImprimible {

    private final Factura factura;

    public ReciboFacturaPdf(Factura factura) {
        this.factura = factura;
    }

    @Override
    public void imprimir() {
        Path dir = Paths.get("recibos");
        try {
            Files.createDirectories(dir);
            String nombreArchivo = "factura_" + factura.getNumero() + ".pdf";
            Path destino = dir.resolve(nombreArchivo);

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);


                PDFont FONT_TEXT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    float x = 50;
                    float y = page.getMediaBox().getUpperRightY() - 50;
                    float leading = 16;


                    write(cs, x, y, FONT_BOLD, 18, "FACTURA #" + factura.getNumero());
                    y -= leading * 2;

                    write(cs, x, y, FONT_TEXT, 12, "Fecha: " + factura.getFechaEmision()); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12,
                            "Cliente: " + factura.getCliente().getNombre() + " (" + factura.getCliente().getCategoria() + ")"); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12,
                            "Empleado: " + factura.getEmpleado().getNombre() + " (" + factura.getEmpleado().getPuesto() + ")"); y -= leading;
                    if (factura.getEmpleado().getDepartamento() != null) {
                        write(cs, x, y, FONT_TEXT, 12,
                                "Departamento: " + factura.getEmpleado().getDepartamento().getNombre()); y -= leading;
                    }

                    y -= leading / 2;
                    write(cs, x, y, FONT_BOLD, 12, "ITEMS:"); y -= leading;

                    for (ItemComercial it : factura.getItems()) {
                        String nombre = (it instanceof Producto) ? ((Producto) it).getNombre()
                                : ((Servicio) it).getNombre();
                        write(cs, x + 10, y, FONT_TEXT, 12,
                                "- " + nombre + ": $" + money(it.getPrecio()));
                        y -= leading;
                    }

                    y -= leading / 2;
                    write(cs, x, y, FONT_TEXT, 12, "Total (items): $" + money(factura.calcularSubtotal())); y -= leading;

                    String formaPago = (factura.getPago() != null)
                            ? factura.getPago().getFormaPago().toString()
                            : (factura.getFormaPago() != null ? factura.getFormaPago().toString() : "N/D");
                    write(cs, x, y, FONT_TEXT, 12, "Forma de pago: " + formaPago); y -= leading;

                    String estado = (factura.getPago() != null) ? factura.getPago().getEstado().toString() : "PENDIENTE";
                    double total = factura.calcularTotal();
                    double pagado = (factura.getPago() != null) ? factura.getPago().getMonto() : 0.0;
                    double saldo  = Math.round((total - pagado) * 100.0) / 100.0;

                    write(cs, x, y, FONT_TEXT, 12, "Estado de pago: " + estado); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12, "Monto pagado: $" + money(pagado)); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12, "Saldo pendiente: $" + money(Math.max(0, saldo))); y -= leading;

                    y -= leading / 2;
                    write(cs, x, y, FONT_TEXT, 12, "============================="); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12, "Subtotal: $" + money(factura.calcularSubtotal())); y -= leading;
                    write(cs, x, y, FONT_TEXT, 12, "IVA (21%): $" + money(factura.calcularSubtotal() * 0.21)); y -= leading;
                    if (factura.getImpuestoExtra() > 0) {
                        double extraAmt = factura.calcularSubtotal() * (factura.getImpuestoExtra() / 100.0);
                        write(cs, x, y, FONT_TEXT, 12, "Impuesto extra ("
                                + String.format(java.util.Locale.US,"%.2f", factura.getImpuestoExtra()) + "%): $" + money(extraAmt));
                        y -= leading;
                    }
                    write(cs, x, y, FONT_TEXT, 12, "Descuento aplicado: " + String.format(Locale.US, "%.2f", factura.getDescuento()) + "%"); y -= leading;
                    write(cs, x, y, FONT_BOLD, 12, "TOTAL: $" + money(factura.calcularTotal()));
                }

                doc.save(destino.toFile());
            }

            System.out.println("PDF generado en: " + destino.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error generando PDF: " + e.getMessage());
        }
    }

    private static void write(PDPageContentStream cs, float x, float y, PDFont font, float size, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text.replace("\n", " "));
        cs.endText();
    }

    private static String money(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}
