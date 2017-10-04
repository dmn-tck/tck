package org.omg.dmn.tck.validation;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URI;

/**
 * Created by opatrascoiu on 10/04/2017.
 */
public class SchemaValidator {
    private final File schemaLocation;
    private final String context;

    public SchemaValidator(File schemaLocation, String context) {
        this.schemaLocation = schemaLocation;
        this.context = context;
    }

    public void validateFile(File file, String extension) throws Exception {
        if (file.isDirectory()) {
            for(File child: file.listFiles()) {
                validateFile(child, extension);
            }
        } else {
            if (file.getName().endsWith(extension)) {
                validateSchema(file);
            }
        }
    }

    private void validateSchema(File file) throws Exception {
        try {
            JAXBContext jc = JAXBContext.newInstance(context);
            Unmarshaller u = jc.createUnmarshaller();
            setSchema(u, schemaLocation);

            u.unmarshal(file.toURI().toURL());
            System.out.println(String.format("'%s' is valid", file.getName()));
        } catch (Exception e) {
            System.out.println(String.format("'%s' is invalid", file.getName()));
            e.printStackTrace();
        }
    }

    private void setSchema(Unmarshaller u, File schemaLocation) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URI schemaURI = schemaLocation.toURI();
        File schemaFile = new File(schemaURI.getPath());
        Schema schema = sf.newSchema(schemaFile);
        u.setSchema(schema);
    }

    public static void main(String[] args) throws Exception {
        File rootFolder = new File("TestCases");
        File testCasesSchemaLocation = new File(rootFolder, "testCases.xsd");

        new SchemaValidator(testCasesSchemaLocation, "org.omg.dmn.tck.marshaller._20160719").validateFile(rootFolder, ".xml");
    }
}
