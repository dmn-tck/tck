package org.omg.dmn.tck.runner.camunda;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.omg.spec.dmn._20151101.dmn.TDefinitions;
import org.slf4j.Logger;

import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.Slf4jBuildLogger;
import com.gs.dmn.runtime.DMNRuntimeException;
import com.gs.dmn.serialization.DMNNamespacePrefixMapper;
import com.gs.dmn.serialization.DMNReader;

public class DMNReaderWriter extends DMNReader {

  static final String CONTEXT_PATH = "org.omg.spec.dmn._20151101.dmn";
  private static final JAXBContext JAXB_CONTEXT;

  static {
      try {
          JAXB_CONTEXT = JAXBContext.newInstance(CONTEXT_PATH);
      } catch (JAXBException e) {
          throw new DMNRuntimeException("Cannot create JAXB Context", e);
      }
  }

  public DMNReaderWriter(BuildLogger logger, boolean validateSchema) {
    super(logger, validateSchema);
  }

  public DMNReaderWriter(Logger logger, boolean validateSchema) {
    super(new Slf4jBuildLogger(logger), validateSchema);
  }

  public void write(TDefinitions definitions, OutputStream outputStream) {
    try {
      Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

      QName qName = new QName(DMNNamespacePrefixMapper.DMN_NS, "definitions");
      JAXBElement<TDefinitions> root = new JAXBElement<TDefinitions>(qName, TDefinitions.class, definitions);

      marshaller.marshal(root, outputStream);
    } catch (Exception e) {
      throw new DMNRuntimeException(String.format("Cannot write DMN to '%s'", outputStream.getClass()), e);
    }
    
  }

}
