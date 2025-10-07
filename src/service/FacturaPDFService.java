package service;

import model.Factura;
import model.ReciboFacturaPdf;

public class FacturaPDFService {

    public static void imprimirFactura(Factura factura) throws Exception {
        ReciboFacturaPdf generadorPdf = new ReciboFacturaPdf(factura);
        generadorPdf.imprimir();
    }

    public static String getMensajeExito() {
        return "Factura impresa exitosamente.\nEl archivo PDF se guardó en la carpeta 'recibos'";
    }
}