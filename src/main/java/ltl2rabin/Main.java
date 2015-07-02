package ltl2rabin;

import ltl2rabin.LTL.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        PropEquivalenceClass.suppressBDDOutput(); // keep System.out clean from messages by the BDD library.

        Options options = new Options();
        options.addOption("nofile", false, "Don't read the LTL formula from a file, instead enter it in the console (via stdin).\n");
        Option formulaOption = Option.builder("f").argName("formula")
                                               .longOpt("formula")
                                               .hasArgs()
                                               .desc("Read the LTL formula as command line argument.").build();
        options.addOption(formulaOption);
        options.addOption("h", "help", false, "Display help message.");

        if (0 == args.length) {
            printHelp(options);
            return;
        }

        CommandLineParser parser = new DefaultParser();

        String inputString = "";
        String outputFilename = "ltl2rabin.hoa";
        LTLFactory.Result parserResult = null;
        CommandLine cmdLine;
        List<String> remainingArguments; // The arguments that don't have a corresponding object of the Options type
        try {
            cmdLine = parser.parse(options, args);
        }
        catch (ParseException e) {
            System.err.println("Command line arguments parsing failed. Reason: " + e.getMessage());
            printHelp(options);
            return;
        }

        remainingArguments = cmdLine.getArgList();
        if (cmdLine.hasOption("h") || cmdLine.hasOption("help")) {
            printHelp(options);
            return;
        }
        else if (cmdLine.hasOption("nofile")) {
            System.out.println("Please enter a valid LTL formula:"); // TODO: Print LTL rules
            try {
                inputString = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                System.err.println("An error occured when reading the input. Reason: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else if (cmdLine.hasOption("formula")) {
            inputString = String.join(" ", Arrays.asList(cmdLine.getOptionValues("formula")));
            System.out.println("Okay, I will generate an automaton for this formula: " + inputString);
        }

        if (0 == inputString.length()) {
            if (0 == remainingArguments.size()) {
                System.out.println("No file name detected. Please specify a name for the input file or run the " +
                        "program again with the -nofile argument.");
                printHelp(options);
                return;
            }
            // Input via file
            // For now, only one file gets read per run.
            String fileName = remainingArguments.get(0);
            outputFilename = fileName + ".hoa";
            parserResult = new LTLFactoryFromFile().buildLTL(new File(fileName));
        }
        else {
            // String input
            parserResult = new LTLFactoryFromString().buildLTL(inputString);
        }

        if (null != parserResult) {
            long startTime = System.currentTimeMillis();

            GDRAFactory factory = new GDRAFactory(parserResult.getAlphabet());
            GDRA gdra = factory.createFrom(parserResult);
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
                out.print(HanoiFormat.toHOAFv1(gdra));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println(parserResult.getLtlFormula() + " Finished in " + elapsedTime + "ms " +
                    "(=" + elapsedTime / 1000 / 60 + "m " + (elapsedTime % 60000) / 1000 + "s " + (elapsedTime % 1000) +"ms)");
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar LTL2Rabin.jar [options] [<filename>]", options);
    }
}
