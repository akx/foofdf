package ai.foofdf;

class Options {
    private String inputPdf;
    private String inputFdf;
    private String outputPdf;
    private Boolean dump = false;

    public static Options parse(String[] args) {
        Options options = new Options();
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            System.out.println(arg);
            if (arg.equals("-i")) {
                options.inputPdf = args[i + 1];
                i ++;
                continue;
            }
            if (arg.equals("-o")) {
                options.outputPdf = args[i + 1];
                i ++;
                continue;
            }
            if (arg.equals("-f")) {
                options.inputFdf = args[i + 1];
                i ++;
                continue;
            }
            if (arg.equals("-d")) {
                options.dump = true;
                continue;
            }
            System.err.println("skipping invalid arg " + arg);
        }
        return options;
    }

    public String getInputPdf() {
        return inputPdf;
    }

    public String getInputFdf() {
        return inputFdf;
    }

    public String getOutputPdf() {
        return outputPdf;
    }

    public Boolean getDump() {
        return dump;
    }
}
