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

import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Packager {

    public final static String DMN_TCK_NS = "http://www.omg.org/spec/DMN/20160719/testcase";

    public static Document packageTests(Document dmnXML, Document tckXML) throws ParserConfigurationException {

        String dmnNS = dmnXML.getDocumentElement().getNamespaceURI();

        if (!DMN_TCK_NS.equals(tckXML.getDocumentElement().getNamespaceURI())) {
            return null;
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document newDMNXML = dBuilder.newDocument();
        Node adoptedNode = newDMNXML.adoptNode(dmnXML.getDocumentElement().cloneNode(true));
        newDMNXML.appendChild(adoptedNode);

        NodeList rootNodes = newDMNXML.getDocumentElement().getChildNodes();
        Element extensions = null;
        Element description = null;
        for (int i = 0; i < rootNodes.getLength(); i++) {
            if ((rootNodes.item(i) instanceof Element) && dmnNS.equals(rootNodes.item(i).getNamespaceURI())
                    && "extensionElements".equals(rootNodes.item(i).getLocalName())) {
                extensions = (Element) rootNodes.item(i);
            } else if ((rootNodes.item(i) instanceof Element) && dmnNS.equals(rootNodes.item(i).getNamespaceURI())
                    && "description".equals(rootNodes.item(i).getLocalName())) {
                description = (Element) rootNodes.item(i);
            }
        }

        // If the extensions does not exists, append it after a description if it existed
        if (extensions == null) {
            extensions = newDMNXML.createElementNS(dmnNS, "extensionElements");
            if (description != null) {
                newDMNXML.getDocumentElement().insertBefore(extensions, description.getNextSibling());
            } else {
                newDMNXML.getDocumentElement().insertBefore(extensions, newDMNXML.getDocumentElement().getFirstChild());
            }
        }

        // remove existing test cases if they existed
        NodeList extensionsNodes = extensions.getChildNodes();
        LinkedList<Node> nodesToRemove = new LinkedList<Node>();
        for (int i = 0; i < extensionsNodes.getLength(); i++) {
            if ((extensionsNodes.item(i) instanceof Element) && DMN_TCK_NS.equals(extensionsNodes.item(i).getNamespaceURI())) {
                nodesToRemove.add(extensionsNodes.item(i));
            }
        }
        for (Node nodeToRemove : nodesToRemove) {
            extensions.removeChild(nodeToRemove);
        }

        adoptedNode = newDMNXML.adoptNode(tckXML.getDocumentElement().cloneNode(true));
        extensions.appendChild(adoptedNode);

        return newDMNXML;
    }

    public static Document unpackageTests(Document dmnXML) throws ParserConfigurationException {

        NodeList testCases = dmnXML.getElementsByTagNameNS(DMN_TCK_NS, "testCases");
        if (testCases.getLength() == 0) {
            return null;
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document tckTests = dBuilder.newDocument();

        Node testCasesRoot = testCases.item(0);
        Node adoptedNode = tckTests.adoptNode(testCasesRoot.cloneNode(true));
        tckTests.appendChild(adoptedNode);

        NamedNodeMap attributes = dmnXML.getDocumentElement().getAttributes();

        // Copy XML Schema and XML Schema instance namespace prefix. Especially useful for qnames resolution of types
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().startsWith("xmlns:") && attributes.item(i).getNodeValue().startsWith("http://www.w3.org/")) {
                tckTests.getDocumentElement().setAttribute(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
            }
        }

        return tckTests;

    }

    public static Document removeTests(Document dmnXML) throws ParserConfigurationException {

        NodeList testCasesList = dmnXML.getElementsByTagNameNS(DMN_TCK_NS, "testCases");
        if (testCasesList.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < testCasesList.getLength(); i++) {
            Element testCases = (Element) testCasesList.item(i);
            Node parentNode = testCases.getParentNode();
            parentNode.removeChild(testCases);
            NodeList extensions = parentNode.getChildNodes();
            boolean hasMoreElements = false;
            for (int j = 0; j < extensions.getLength(); j++) {
                if (extensions.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    hasMoreElements = true;
                    break;
                }
            }
            if (!hasMoreElements) {
                // Remove extension as the test case was the only extension
                parentNode.getParentNode().removeChild(parentNode);
            }
        }

        return dmnXML;

    }

}
