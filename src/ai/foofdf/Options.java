package ai.foofdf;

import java.util.ArrayList;

class Options {
    private String inputPdf;
    private ArrayList<String> inputFdfs = new ArrayList<>();
    private ArrayList<String> inputJSONs = new ArrayList<>();
    private String outputPdf;
    private Boolean dump = false;
    private Boolean verbose = false;

    public static Options parse(String[] args) {
        Options options = new Options();
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.equals("-i")) {
                options.inputPdf = args[i + 1];
                i++;
                continue;
            }
            if (arg.equals("-o")) {
                options.outputPdf = args[i + 1];
                i++;
                continue;
            }
            if (arg.equals("-f")) {
                options.inputFdfs.add(args[i + 1]);
                i++;
                continue;
            }
            if (arg.equals("-j")) {
                options.inputJSONs.add(args[i + 1]);
                i++;
                continue;
            }
            if (arg.equals("-d")) {
                options.dump = true;
                continue;
            }

            if (arg.equals("-v")) {
                options.verbose = true;
                continue;
            }
            System.err.println("skipping invalid arg " + arg);
        }
        return options;
    }

    public String getInputPdf() {
        return inputPdf;
    }

    public ArrayList<String> getInputFdfs() {
        return inputFdfs;
    }

    public ArrayList<String> getInputJSONs() {
        return inputJSONs;
    }

    public String getOutputPdf() {
        return outputPdf;
    }

    public Boolean getDump() {
        return dump;
    }

    public Boolean getVerbose() { return verbose; }

    public static void printUsage() {
        for (String line : new String[]{
                "foofdf - a tool for filling pdf forms",
                "usage:",
                "  -i input_pdf  : the input pdf to be stamped",
                "  -f input_fdf  : path to an FDF or XFDF file with form data (can be repeated)",
                "  -j input_json : path to a file with a JSON mapping object with form data (can be repeated)",
                "  -o output_pdf : path to output PDF",
                "  -d            : dump input fields to stdout?",
                "  -v            : be verbose?",
        }) {
            System.err.println(line);
        }
    }
}
