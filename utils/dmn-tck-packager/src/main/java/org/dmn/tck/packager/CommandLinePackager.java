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

package org.dmn.tck.packager;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CommandLinePackager {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        if (args.length == 3 && "-x".equals(args[0])) {
            System.exit(unpackageTests(args[1], args[2]));
        }
        if (args.length == 4 && "-c".equals(args[0])) {
            System.exit(packageTests(args[1], args[2], args[3]));
        }
        exitWithUsageMessage();
    }

    public static int unpackageTests(String dmnSource, String tckDestination)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {
        File source = new File(dmnSource);
        if (!source.exists()) {
            System.err.println("Could not find source file: " + dmnSource + " (" + source.getAbsolutePath() + ")");
            return 1;
        }
        File destination = new File(tckDestination);
        if (!destination.getParentFile().exists()) {
            if (!destination.getParentFile().mkdirs()) {
                System.err.println("Could not create destination folder: " + destination.getParentFile().getAbsolutePath());
                return 1;
            }
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document sourceDMNXML = dBuilder.parse(source);

        Document tckDestinationXML = Packager.unpackageTests(sourceDMNXML);
        if (tckDestinationXML == null) {
            System.err.println("No DMN TCK Tests found in source file: " + dmnSource + " (" + source.getAbsolutePath() + ")");
            return 2;
        }

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource tckDestinationXMLSrc = new DOMSource(tckDestinationXML);
        StreamResult result = new StreamResult(destination);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(tckDestinationXMLSrc, result);
        return 0;
    }

    public static int packageTests(String dmnSource, String tckSource, String dmnDestination)
            throws SAXException, IOException, ParserConfigurationException, TransformerException {
        File dmnSourceFile = new File(dmnSource);
        if (!dmnSourceFile.exists()) {
            System.err.println("Could not find DMN source file: " + dmnSource + " (" + dmnSourceFile.getAbsolutePath() + ")");
            return 1;
        }
        File tckSourceFile = new File(tckSource);
        if (!tckSourceFile.exists()) {
            System.err.println("Could not find TCK source file: " + tckSource + " (" + tckSourceFile.getAbsolutePath() + ")");
            return 1;
        }
        File destination = new File(dmnDestination);
        if (!destination.getParentFile().exists()) {
            if (!destination.getParentFile().mkdirs()) {
                System.err.println("Could not create destination folder: " + destination.getParentFile().getAbsolutePath());
                return 1;
            }
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document sourceDMNXML = dBuilder.parse(dmnSourceFile);

        Document sourceTCKXML = dBuilder.parse(tckSourceFile);

        Document dmnAugmentedXML = Packager.packageTests(sourceDMNXML, sourceTCKXML);
        if (dmnAugmentedXML == null) {
            System.err.println("No TCK Tests found in source file: " + tckSource);
            return 2;

        }

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource tckDestinationXMLSrc = new DOMSource(dmnAugmentedXML);
        StreamResult result = new StreamResult(destination);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(tckDestinationXMLSrc, result);
        return 0;

    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java -jar dmn-tck-packager.jar [-c|-x] files....");
        System.err.println("-------------------------------------------------------------");
        System.err.println("");
        System.err.println("Extract Test Cases from DMN XML:");
        System.err.println("Usage: java -jar dmn-tck-packager.jar -x dmn-source.xml tck-destination.xml");
        System.err.println("");
        System.err.println("Package Test Cases in DMN XML:");
        System.err.println("Usage: java -jar dmn-tck-packager.jar -c dmn-source.xml tck-source.xml dmn-destination.xml");
        System.err.println("");
        System.exit(1);
    }

}
