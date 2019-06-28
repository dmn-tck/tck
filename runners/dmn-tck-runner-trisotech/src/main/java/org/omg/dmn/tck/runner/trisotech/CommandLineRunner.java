/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omg.dmn.tck.runner.trisotech;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.fusesource.jansi.AnsiConsole;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.xml.sax.SAXException;

public class CommandLineRunner {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Properties properties = null;
        try {
            properties = TrisotechTCKHelper.getProperties();
        } catch (IOException e) {
            AnsiConsole.err.println(e.getMessage());
            AnsiConsole.err.println();
            exitOnUsage();
        }

        File tckTestsRoot = args.length > 1 ? new File(args[1]) : new File(".");
        if (!tckTestsRoot.exists()) {
            AnsiConsole.err.println(ansi().fg(RED).a("Invalid DMN TCK test case folder: " + tckTestsRoot.getAbsolutePath() + " it does not exist").reset());
            exitOnUsage();
        }

        if (!tckTestsRoot.isDirectory()) {
            AnsiConsole.err.println(ansi().fg(RED).a("Invalid DMN TCK test case folder: " + tckTestsRoot.getAbsolutePath() + " it's not a folder").reset());
            exitOnUsage();
        }

        File testCasesFolder = new File(".").getAbsoluteFile();
        if (args.length > 0) {
            testCasesFolder = new File(args[0]).getAbsoluteFile();
        }
        if (!testCasesFolder.exists()) {
            AnsiConsole.err.println(ansi().fg(RED).a("Invalid DMN TCK test case folder: " + testCasesFolder.getAbsolutePath() + " it does not exist").reset());
            exitOnUsage();
        }

        if (!testCasesFolder.isDirectory()) {
            AnsiConsole.err.println(ansi().fg(RED).a("Invalid DMN TCK test case folder: " + testCasesFolder.getAbsolutePath() + " it's not a folder").reset());
            exitOnUsage();
        }

        // Detect all test categories
        File[] testCategoriesFolders = testCasesFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.isDirectory() && name.startsWith("compliance");
            }
        });

        if (testCategoriesFolders == null || testCategoriesFolders.length == 0) {
            AnsiConsole.err.println(ansi().fg(RED)
                    .a("Invalid DMN TCK test case folder: " + testCasesFolder.getAbsolutePath() + " it does not contain compliance sub folders").reset());
            exitOnUsage();
        }

        // Inside each category, there are a list of tests
        for (File testCategoriesFolder : testCategoriesFolders) {
            String category = testCategoriesFolder.getName();

            File[] testFolders = testCategoriesFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            if (testFolders == null) {
                continue;
            }

            AnsiConsole.out.println(category);
            for (File testFolder : testFolders) {
                String testId = testFolder.getName();

                File[] testCasesFiles = testFolder.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(testId + "-test-") && name.endsWith(".xml");
                    }

                });
                for (File testCaseFile : testCasesFiles) {
                    try {
                        TestCases tcs = TckMarshallingHelper.load(new FileInputStream(testCaseFile));
                        String modelName = tcs.getModelName();

                        LinkedList<File> dmnFiles = new LinkedList<>();
                        dmnFiles.add(new File(testCaseFile.getParentFile(), modelName));

                        for (File fileInDirectory : testCaseFile.getParentFile().listFiles()) {
                            if ((fileInDirectory.getName().endsWith(".dmn")) && (!fileInDirectory.getName().equals(modelName))) {
                                dmnFiles.add(fileInDirectory);
                            }
                        }

                        AnsiConsole.out.print(testCaseFile.getName() + " ");
                        try {
                            if (TrisotechTCKHelper.pushTestCase(category, testId, properties, dmnFiles.toArray(new File[dmnFiles.size()]))) {
                                Map<String, Boolean> results = TrisotechTCKHelper.runTestCase(category, testId, testCaseFile, properties);
                                int total = 0;
                                int passed = 0;
                                for (Map.Entry<String, Boolean> result : results.entrySet()) {
                                    total += 1;
                                    if (result.getValue()) {
                                        passed += 1;
                                    }
                                }
                                if (total == 0) {
                                    AnsiConsole.out.println(ansi().a("[").fg(YELLOW).a("NO TESTS FOUND").reset().a("]"));
                                } else if (total == passed) {
                                    AnsiConsole.out.println(ansi().a("[").fg(GREEN).a("PASSED " + passed + "/" + total).reset().a("]"));
                                } else {
                                    AnsiConsole.out.println(ansi().a("[").fg(RED).a("FAILED " + passed + "/" + total).reset().a("]"));
                                }
                            }
                        } catch (IOException | SAXException e) {
                            AnsiConsole.out.println(ansi().a("[").fg(RED).a("ERROR").reset().a("]"));
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException | JAXBException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    private static void exitOnUsage() {
        AnsiConsole.err.println("Usage: java -jar tck-runner-trisotech.jar -jar [DMN TCK test case folder]");
        AnsiConsole.err.println("------------------------------------------------");
        AnsiConsole.err.println("");
        AnsiConsole.err.println("If the DMN TCK folder is not provided, the current working directory will be used");
        AnsiConsole.err.println("");
        AnsiConsole.err.println(
                "This application also require a configuration file named .dmn-tck-runner-trisotech.properties under the user home directory with the following content:");
        AnsiConsole.err.println("url=https://xxx.trisotech.com #The Digital Enterprise Server instance ");
        AnsiConsole.err.println("bearer=xxxxx #A DES Client App bearer token that can be used to deploy and execute the DMN TCK Tests");
        AnsiConsole.err.println("repo=tck #The name of the deployment repository to use to push/run the TCK");
        AnsiConsole.err.println("");
        System.exit(1);
    }

}
