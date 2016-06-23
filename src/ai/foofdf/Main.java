package ai.foofdf;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = Options.parse(args);
        if (options.getInputPdf() == null) {
            Options.printUsage();
            return;
        }

        FooFdfTool tool = new FooFdfTool(options);
        tool.run();
    }
}
