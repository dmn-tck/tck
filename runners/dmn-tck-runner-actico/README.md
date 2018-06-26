# ACTICO DMN TCK test runner

The *ACTICO DMN TCK test runner* is a Java command line application that is able to automatically execute the DMN TCK suite of tests.
It uses the *ACTICO DMN runtime* and is based on the DMN TCK test runner. 
The *ACTICO DMN runtime* is compliant with level 3 of the DMN 1.1 specification. It is able to execute any DMN model without any preprocessing.

The test runner can execute multiple tests that are stored in test subfolders. 
Each subfolder name must comply with the DMN TCK naming convention: `<4 digit number>-<testname>`
Each subfolder must contain a test XML file describing the test cases and the tested DMN model file. The filename of the test XML File must comply with the DMN TCK naming 
convention: `<4-digit number>-<testname>-test-<testnumber>.xml`
The test XML file must comply with the XML schema testCases.xsd. 
The DMN model file must comply with the XML schema of the DMN 1.1 specification.

The test runner is also able to execute a single test folder, containing the test XML file and the DMN model file.

The test runner creates a result file in CSV format used by the DMN TCK.

## How to setup the runner

Check out the DMN TCK GitHub repository in order to get the DMN TCK tests.

Unzip the *ACTICO DMN TCK test runner* zip file `actico-dmn-tck-test-runner-<version>.zip`

It contains the following files:
- `3rdPartyDisclosureDocument.pdf` - license information about used third party libraries
- `ACTICO License terms for use without charge.pdf` - license information about the ACTICO DMN TCK runner
- `actico-dmn-tck-test-runner.jar` - test runner as executable Java archive
- `README.md` - this file

## How to execute the runner

In order to list all command line arguments, start the *ACTICO DMN TCK test runner* with argument `-help`:

`java -jar actico-dmn-tck-test-runner.jar -help`

The runner lists all available command line arguments and a description of them.

```
ACTICO GmbH, Germany. All rights reserved.
see ACTICO License terms for use without charge

ACTICO DMN TCK Test Runner
==========================
Command line utility to execute a single or multiple DMN TCK tests.

Usage: java -jar actico-dmn-tck-test-runner.jar [-help] <-tckdir <directory>>|<-tckdirs <list of directories>> -result <resultFile>

Command line arguments:
 -help                             display this help output
 -tckdir <directory>               directory containing a test cases XML file and the corresponding DMN file
 -tckdirs <list of directories>    list of directories separated by ','. Each directory must contain test
                                   directories with DMN TCK test cases (XML file and corresponding DMN file).
 -result <resultFile>              path and filename of created result file, containing the test results

 Examples:

 Execute all DMN TCK test cases:
 java -jar actico-dmn-tck-test-runner.jar -tckdirs ./TestCases/compliance-level-2,./TestCases/compliance-level-3 -result result.csv

 Execute a single DMN TCK test case:
 java -jar actico-dmn-tck-test-runner.jar -tckdir ./TestCases/compliance-level-3/0006-join -result ./result.csv


 NOTE: 
  - Log output may contain error message, since DMN TCK test cases also tests invalid FEEL expressions
  - Directory name for DMN TCK test must comply to DMN TCK naming convention: <4 digit number>-<testname> 
  - Filename of test case XML file must comply to DMN TCK naming convention: <4-digit number>-<testname>-test-<testnumber>.xml
```

## How to get the runner

In order to get the runner, visit [ACTICO DMN TCK Test Runner](https://www.actico.com/en/cf/cf-actico-dmn-tck-test-runner) and fill out the fields.
Use "DMN TCK test runner" for the message field.

After submitting the form, you will get the information how to download the *ACTICO DMN TCK test runner* ZIP file.

## Example execution of the runner

An example console log of the execution of the test runner can be found [here](https://github.com/dmn-tck/tck/blob/master/runners/dmn-tck-runner-actico/console.log).
