import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    public void generarPDF(File archivoPDF, File archivoLogo, ContenidoCSV contenidoCSV) throws IOException {
        if (!archivoPDF.getName().toLowerCase().endsWith(".pdf")) {
            archivoPDF = new File(archivoPDF.getAbsolutePath() + ".pdf");
        }

        System.out.println("Guardando PDF en: " + archivoPDF.getAbsolutePath());

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
            document.open();

            // 1. Agregar el logo si existe
            if (archivoLogo != null && archivoLogo.exists()) {
                Image logo = Image.getInstance(archivoLogo.getAbsolutePath());
                logo.scaleToFit(100, 100); 
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
                document.add(new Paragraph("\n")); // Espacio debajo del logo
            }

            // 2. Título del PDF
            Paragraph titulo = new Paragraph("Factura de Compra", 
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n")); // Espacio después del título

            // 3. Recorrer los datos del CSV y agregarlos al PDF
            List<String[]> filas = contenidoCSV.obtenerFilas();
            for (String[] fila : filas) {
                // Extraer los datos del CSV
                String nombre = fila[0];
                String apellido = fila[1];
                String correo = fila[2];
                double totalCompra = Double.parseDouble(fila[3]);
                double iva = Double.parseDouble(fila[4]);
                double descuento = Double.parseDouble(fila[5]);

                // Calcular el precio total
                double precioConIVA = totalCompra + (totalCompra * iva);
                double precioTotal = precioConIVA - (precioConIVA * descuento);

                // 4. Agregar los datos al PDF con formato
                document.add(new Paragraph("Cliente: " + nombre + " " + apellido));
                document.add(new Paragraph("Correo: " + correo));
                document.add(new Paragraph(String.format("Total de Compra: $%.2f", totalCompra)));
                document.add(new Paragraph(String.format("IVA: %.2f%%", iva * 100)));
                document.add(new Paragraph(String.format("Descuento: %.2f%%", descuento * 100)));
                document.add(new Paragraph(String.format("Precio Total: $%.2f", precioTotal)));
                document.add(new Paragraph("----------------------------------------------------"));

                document.add(new Paragraph("\n")); // Espacio entre facturas
            }

            document.close();
            System.out.println("PDF generado exitosamente.");
        } catch (DocumentException e) {
            throw new IOException("Error al crear el PDF: " + e.getMessage());
        }
    }
}
