package ltl2rabin;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * @author Hannes Bibel
 *
 */

import ltl2rabin.LTL.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        PropEquivalenceClass.suppressBDDOutput(); // keep System.out clean from messages by the BDD library.
        /* Right now, this program parses the input args into an ArrayList. I strongly recommend using the
         * org.apache.commons.cli package since it follows the POSIX Program Argument Syntax Conventions and also it
         * makes life easier. Unfortunately, it is not compatible with the argument format used by Rabinizer 3.1,
         * so for now we stick to handling arguments as a List. */
        List<String> argList = Arrays.asList(args);

        String helpMessage = "usage: java -jar LTL2Rabin.jar [options] [<filename>/<formula>]\n" +
                " -h,--help     Display this help message.\n" +
                " -v,--version  Display the number of this version of LTL2Rabin\n" +
                " -in=file      LTL2Rabin will read the formula from a file. Don't forget \n" +
                                "               " +
                                "to specify a file name. Multiple formulas per file are not \n" +
                                "               " +
                                "supported.\n" +
                " -out=std      The resulting automaton will be printed to the terminal\n";

        if (argList.contains("-h") || argList.contains("--help") || 0 == args.length) {
            System.out.println(helpMessage);
            return;
        }

        if (argList.contains("--version") || argList.contains("-v")) {
            System.out.println("This is LTL2Rabin version 0.9\n");
        }

        boolean fileOutput = true;
        if (argList.remove("-out=std")) {
            fileOutput = false;
        }

        LTLFactory.Result parserResult = null;
        String inputString = "";
        String outputFilename = "ltl2rabin.hoa";
        if (argList.remove("-in=file")) {
            // File input
            String fileName = argList.get(argList.size() - 1);
            outputFilename = fileName + ".hoa";
            File inputFile = new File(fileName);
            if (inputFile.exists() && !inputFile.isDirectory()) {
                parserResult = new LTLFactoryFromFile().buildLTL(inputFile);
            }
            else {
                System.out.println("Sorry, I could not find a file named " + fileName + "\n");
                System.out.println(helpMessage);
            }
        }
        else {
            // Parse input formula from args
            // If there are additional arguments starting with '-', just ignore them.
            inputString = String.join(" ", argList.stream().filter(s -> s.charAt(0) != '-').collect(Collectors.toList()));
            System.out.println("Okay, I will generate an automaton for this formula: " + inputString + "\n");
            parserResult = new LTLFactoryFromString().buildLTL(inputString);
        }

        if (null != parserResult) {
            long startTime = System.currentTimeMillis();

            GDRAFactory factory = new GDRAFactory(parserResult.getAlphabet());
            GDRA gdra = factory.createFrom(parserResult);

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Construction of GDRA for " + parserResult.getLtlFormula() + " finished in " + elapsedTime + "ms " +
                    "(=" + elapsedTime / 1000 / 60 + "m " + (elapsedTime % 60000) / 1000 + "s " + (elapsedTime % 1000) +"ms)\n\n");

            if (fileOutput) {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));
                    out.print(HanoiFormatStringFactory.toHOAFv1(gdra));
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(HanoiFormatStringFactory.toHOAFv1(gdra));
            }
        }
    }
}
