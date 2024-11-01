# ComponenteBoton Funcionamiento
Boton generador de pdf a traves de un archivo csv
-Método crearGenerarPDFAction()de la claseComponentePDF
Este método crea y devuelve una acción ( Action) que permite seleccionar un archivo CSV, una imagen para un logo y un archivo PDF de destino, generando el PDF con los archivos seleccionados.

JFileChooserPara seleccionar archivos :

Se utilizan tres instancias de JFileChooserpara:
Seleccionar el archivo CSV:
JFileChooser fileChooser = new JFileChooser();
fileChooser.setDialogTitle("Seleccione el archivo CSV");
int seleccionCSV = fileChooser.showOpenDialog(null);

if (seleccionCSV != JFileChooser.APPROVE_OPTION) {
    System.out.println("No se seleccionó ningún archivo CSV.");
    return;
}

File archivoCSV = fileChooser.getSelectedFile();
Seleccione una imagen opcional para el logo:
fileChooser.setDialogTitle("Seleccione una imagen para el logo (opcional)");
int seleccionLogo = fileChooser.showOpenDialog(null);
File archivoLogo = (seleccionLogo == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;

Elegir el lugar y nombre del archivo PDF de destino:
fileChooser.setDialogTitle("Seleccione dónde guardar el archivo PDF");
fileChooser.setSelectedFile(new File("factura.pdf"));
int seleccionPDF = fileChooser.showSaveDialog(null);

if (seleccionPDF != JFileChooser.APPROVE_OPTION) {
    System.out.println("No se seleccionó ninguna ubicación de guardado.");
    return;
}
File archivoPDF = fileChooser.getSelectedFile();

Carga del CSV : Si el usuario selecciona un archivo CSV, se crea una instancia de la clase ContenidoCSVy se carga el contenido usando cargarDesdeArchivo:
ContenidoCSV contenidoCSV = new ContenidoCSV();
contenidoCSV.cargarDesdeArchivo(archivoCSV);

Generación del PDF : Se instancia PDFGenerator se llama al método generarPDF, proporcionando el archivo de destino, el logo (si se seleccionó) y los datos del CSV:
PDFGenerator pdfGenerator = new PDFGenerator();
pdfGenerator.generarPDF(archivoPDF, archivoLogo, contenidoCSV);

Manejo de errores : Cualquier excepción es capturada e informada al usuario mediante una ventana de diálogo:
catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

-ClaseContenidoCSV
La clase ContenidoCSVadministra los datos de un archivo CSV, permitiendo cargar y acceder a ellos en forma de lista de filas.

ConstructorContenidoCSV : Inicializa filascomo una lista vacía, donde se guardarán las filas leídas desde el archivo CSV.
public ContenidoCSV() {
    this.filas = new ArrayList<>();
}
MétodocargarDesdeArchivo(File archivo) : Lee el archivo CSV línea por línea.

Saltado de la primera línea : Ignora la primera línea, asumiendo que es de encabezados.
boolean esPrimeraLinea = true;
while ((linea = br.readLine()) != null) {
    if (esPrimeraLinea) {
        esPrimeraLinea = false;
        continue;
    }

Separación por comas : Cada línea se divide en valores separados por comas y se añade a filas.
String[] valores = linea.split(","); // Dividimos por comas
filas.add(valores);

MétodoobtenerFilas() : Retorna la lista de filas leídas, que es una lista de arreglos de cadenas.
public List<String[]> obtenerFilas() {
    return this.filas;
}

-ClasePDFGenerator
La clase PDFGeneratores responsable de generar el PDF a partir de los datos en el archivo CSV y un logotipo opcional.

Validación de la extensión PDF : Agrega .pdfal archivo de destino si no está presente:
if (!archivoPDF.getName().toLowerCase().endsWith(".pdf")) {
    archivoPDF = new File(archivoPDF.getAbsolutePath() + ".pdf");
}

Configuración del documento : Crea una instancia Documenty lo abre para escritura.
Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
document.open();

Agregar logo : Si archivoLogoestá seleccionado y existe, se agrega al documento centrado y redimensionado:
if (archivoLogo != null && archivoLogo.exists()) {
    Image logo = Image.getInstance(archivoLogo.getAbsolutePath());
    logo.scaleToFit(100, 100); 
    logo.setAlignment(Element.ALIGN_CENTER);
    document.add(logo);
    document.add(new Paragraph("\n")); // Espacio debajo del logo
}

Título : Agrega un título centrado, "Factura de Compra", al inicio del PDF.
Paragraph titulo = new Paragraph("Factura de Compra", 
        new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
titulo.setAlignment(Element.ALIGN_CENTER);
document.add(titulo);
document.add(new Paragraph("\n")); // Espacio después del título

*Datos de CSV : Registre las filas del CSV y agregue los datos de cada fila con formato específico:
Cálculos : Realiza cálculos de IVA y descuento.
double precioConIVA = totalCompra + (totalCompra * iva);
double precioTotal = precioConIVA - (precioConIVA * descuento);

Formato : Agregue cada dato en líneas separadas, y una línea separadora entre facturas.
document.add(new Paragraph("Cliente: " + nombre + " " + apellido));
document.add(new Paragraph("Correo: " + correo));
document.add(new Paragraph(String.format("Total de Compra: $%.2f", totalCompra)));
document.add(new Paragraph(String.format("IVA: %.2f%%", iva * 100)));
document.add(new Paragraph(String.format("Descuento: %.2f%%", descuento * 100)));
document.add(new Paragraph(String.format("Precio Total: $%.2f", precioTotal)));
document.add(new Paragraph("----------------------------------------------------"));
document.add(new Paragraph("\n")); // Espacio entre facturas

Cierre del documento : Finaliza la creación del PDF y maneja cualquier excepción lanzada.
document.close();
System.out.println("PDF generado exitosamente.");
