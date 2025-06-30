package Batch1_POSG4.util;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public final class PdfReceiptGenerator {

    private PdfReceiptGenerator() {}
    public static void write(Path out,
                            long   saleId,
                            String cashier,
                            List<String> bodyLines,
                            double gross, double discount,
                            double grand, double paid, double change)
                            throws IOException {

        final float W    = 80f * 2.835f;      // 80-mm roll  ➜  points
        final float TOP  = 10f, LEFT = 4f;
        final float FONT = 9f,  LEAD = FONT * 1.15f;
        
        
        PDRectangle size = new PDRectangle(W, 500);

        try (PDDocument doc = new PDDocument()) {
            PDType0Font mono = PDType0Font.load(
                doc,
                PdfReceiptGenerator.class.getResourceAsStream(
                    "/Batch1_POSG4/resources/fonts/Consolas.ttf"),   // <── here
                true);
            /* mutable holders so the lambda can change them */
            final PDPage[]             page = { new PDPage(size) };
            doc.addPage(page[0]);
            final PDPageContentStream[] cs   = { new PDPageContentStream(doc, page[0]) };
            final float[]               y    = { size.getHeight() - TOP };

            java.util.function.Consumer<String> ln = txt -> {
                try {
                    if (y[0] < LEAD) {                    // need a new page?
                        cs[0].close();
                        page[0] = new PDPage(size);
                        doc.addPage(page[0]);
                        cs[0] = new PDPageContentStream(doc, page[0]);
                        y[0]  = size.getHeight() - TOP;
                    }

                    cs[0].beginText();
                    cs[0].setFont(mono, FONT);            // use the mono var, not a constant
                    cs[0].newLineAtOffset(LEFT, y[0]);
                    cs[0].showText(txt);
                    cs[0].endText();
                    y[0] -= LEAD;

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            };

            /* ------------- build the receipt ------------- */
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ln.accept("===== SALES RECEIPT =====");
            ln.accept("Sale ID:   " + saleId);
            ln.accept("Date/time: " + LocalDateTime.now().format(fmt));
            ln.accept("Cashier:   " + cashier);
            ln.accept("-------------------------------");

            bodyLines.forEach(ln);

            ln.accept("-------------------------------");
            ln.accept(String.format("Gross Total:  PHP %.2f", gross));
            ln.accept(String.format("Discount:     PHP %.2f", discount));
            ln.accept(String.format("Grand Total:  PHP %.2f", grand));
            ln.accept(String.format("Paid:         PHP %.2f", paid));
            ln.accept(String.format("Change:       PHP %.2f", change));
            ln.accept("===============================");

            cs[0].close();
            doc.save(out.toFile());
        }

    }
}

