package ai.foofdf;

import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.*;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FooFdfTool {
    private Options options;
    private Boolean verbose;

    FooFdfTool(Options options) {
        this.options = options;
        this.verbose = options.getVerbose();
    }

    public void run() throws Exception {
        PdfReader reader = new PdfReader(options.getInputPdf());
        if (options.getDump()) {
            dumpFields(reader);
        }
        OutputStream outputStream = (options.getOutputPdf() != null ? new FileOutputStream(options.getOutputPdf()) : new ByteArrayOutputStream());
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        HashMap<String, String> data = new HashMap<>();
        for (String path : options.getInputFdfs()) {
            readFDF(data, path);
        }
        for (String path : options.getInputJSONs()) {
            readJSON(data, path);
        }
        if(this.verbose) {
            System.err.println("Total " + data.size() + " fields read from " + options.getInputFdfs().size() + " paths");
        }
        AcroFields fields = stamper.getAcroFields();
        for (String key : data.keySet()) {
            String value = data.get(key);
            if (!fields.setField(key, value)) {
                System.err.println("Unable to set '" + key + "' to '" + value + "'");
            } else {
                if(this.verbose) {
                    System.err.println("OK: set '" + key + "' to '" + value + "'");
                }
            }
        }
        stamper.close();
        outputStream.close();
    }

    private void dumpFields(PdfReader reader) {
        final Map<String, AcroFields.Item> fields = reader.getAcroFields().getFields();
        for (String name : fields.keySet()) {
            final AcroFields.Item item = fields.get(name);
            PdfDictionary dct = null;
            try {
                dct = item.getMerged(0);
            } catch(Exception exc) {
                System.out.println(name);
                continue;
            }
            for (PdfName prop : dct.getKeys()) {
                final PdfObject value = dct.get(prop);
                if(value instanceof PdfString) {
                    System.out.println(name + "#" + prop.toString() + "=" + ((PdfString)value).toUnicodeString());
                }
                if(value instanceof PdfNumber || value instanceof PdfBoolean || value instanceof PdfName) {
                    System.out.println(name + "#" + prop.toString() + "=" + value);
                }

            }

        }
    }

    private void readFDF(HashMap<String, String> data, String inputFdf) throws Exception {
        try {
            FdfReader fr = new FdfReader(inputFdf);
            for (String key : fr.getFields().keySet()) {
                data.put(key, fr.getFieldValue(key));
            }
            return;
        } catch (InvalidPdfException ipe) {
            if(this.verbose) {
                System.err.println(inputFdf + " is not valid FDF: " + ipe);
            }
        }
        try {
            XfdfReader xfr = new XfdfReader(inputFdf);
            for (String key : xfr.getFields().keySet()) {
                data.put(key, xfr.getFieldValue(key));
                // TODO: Support lists?
            }
            return;
        } catch (InvalidPdfException ipe) {
            if(this.verbose) {
                System.err.println(inputFdf + " is not valid XFDF: " + ipe);
            }
        }
        if(this.verbose) {
            System.err.println("Not a valid FDF/XFDF map: " + inputFdf);
        }
    }

    private void readJSON(HashMap<String, String> data, String inputFdf) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(inputFdf)), StandardCharsets.UTF_8);
        JSONObject jo = new JSONObject(json);
        for (String key : jo.keySet()) {
            data.put(key, jo.getString(key));
        }
    }
}
