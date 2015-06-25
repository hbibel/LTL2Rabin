package ltl2rabin;

import ltl2rabin.LTL.*;
import ltl2rabin.parser.LTLLexer;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Options options = new Options();
        options.addOption("nofile", false, "Don't read the LTL formula from a file, instead enter it in the console.");

        CommandLineParser parser = new DefaultParser();

        String inputString = null;
        LTLFactory.Result parserResult = null;
        List<String> remainingArguments; // The arguments that don't have a corresponding object of the Options type
        try {
            CommandLine cmd = parser.parse(options, args);
            remainingArguments = cmd.getArgList();
            if (cmd.hasOption("nofile")) {
                System.out.println("Please enter a valid LTL formula:"); // TODO: Print LTL rules
                inputString = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            else {
                if (0 == remainingArguments.size()) {
                    System.out.println("No file name detected. Please specify a name for the input file or run the " +
                            "program again with the -nofile argument.");
                    printHelp();
                }
            }

            if (null == inputString) {
                // Input via file
                LTLFactoryFromFile factoryFromFile = new LTLFactoryFromFile();
                // For now, only one file gets read per run.
                String fileName = remainingArguments.get(0);
                parserResult = new LTLFactoryFromFile().buildLTL(new File(fileName));
            }
            else {
                // String input
                parserResult = new LTLFactoryFromString().buildLTL(inputString);
            }
        } catch (ParseException e) {
            System.err.println("Command line arguments parsing failed. Reason: " + e.getMessage());
            printHelp();
        } catch (IOException e) {
            System.err.println("An error occured when reading the input. Reason: " + e.getMessage());
            e.printStackTrace();
        }

        if (null != parserResult) {
            GDRAFactory factory = new GDRAFactory(parserResult.getAlphabet());
            GDRA gdra = factory.createFrom(parserResult);
        }

        //String input = "b | (X G (a | (X (b U c))))";
        //String otherInput = "(r U s) & ((F G a) | (G F b)) & ((F G c) | (G F d)) ";

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Finished in " + elapsedTime + "ms " +
                "(=" + elapsedTime / 1000 / 60 + "m " + (elapsedTime % 60000) / 1000 + "s " + (elapsedTime % 1000) +"ms)");
    }

    private static void printHelp() {
        System.out.println();
    }

    // Note to developer: When parsing several LTLs, don't forget to call clear() in between to clean up
    // the BDD variable space.
    private static void clear() {
        PropEquivalenceClass.clear();
    }
}
