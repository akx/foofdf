package ai.foofdf;

import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = Options.parse(args);
        if (options.getInputPdf() == null) {
            System.err.println("usage: -i input_pdf [-f input_fdf-or-xfdf] [-o output-pdf] [-d (dump)]");
            return;
        }
        run(options);
    }

    private static void run(Options options) throws Exception {
        PdfReader reader = new PdfReader(options.getInputPdf());
        if (options.getDump()) {
            dumpFields(reader);
        }
        OutputStream outputStream = (options.getOutputPdf() != null ? new FileOutputStream(options.getOutputPdf()) : new ByteArrayOutputStream());
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        String inputFdf = options.getInputFdf();
        if (inputFdf != null) {
            AcroFields fields = stamper.getAcroFields();
            HashMap<String, String> data = readFDF(inputFdf);
            for (String key : data.keySet()) {
                String value = data.get(key);
                if (!fields.setField(key, value)) {
                    System.err.println("* unable to set '" + key + "' to '" + value + "'");
                }
            }
        }
        stamper.close();
        outputStream.close();
    }

    private static void dumpFields(PdfReader reader) {
        final Map<String, AcroFields.Item> fields = reader.getAcroFields().getFields();
        for (String name : fields.keySet()) {
            System.out.println(name);
        }
    }

    private static HashMap<String, String> readFDF(String inputFdf) throws Exception {
        HashMap<String, String> data = new HashMap<>();
        try {
            FdfReader fr = new FdfReader(inputFdf);
            for (String key : fr.getFields().keySet()) {
                data.put(key, fr.getFieldValue(key));
            }
        } catch (InvalidPdfException ipe) {

        }
        try {
            XfdfReader xfr = new XfdfReader(inputFdf);
            for (String key : xfr.getFields().keySet()) {
                data.put(key, xfr.getFieldValue(key));
                // TODO: Support lists?
            }
        } catch (InvalidPdfException ipe) {

        }

        return data;
    }
}
