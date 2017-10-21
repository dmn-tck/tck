/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.omg.dmn.tck.marshaller._20160719;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase.InputNode;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("restriction")
public class TestCasesTest
{

   private static ObjectMapper objectMapper;

   private static File baseOutputDir;

   private static File level2Dir;

   @BeforeClass
   public static void setUpClass()
   {
      objectMapper = new ObjectMapper();

      baseOutputDir = new File("target", "test-classes");
      baseOutputDir.mkdirs();

      level2Dir = new File(baseOutputDir, "compliance-level-2");
      level2Dir.mkdirs();
   }

   @Test
   public void testRead0001() throws Exception
   {
      InputStream is = null;
      try
      {
         is = getClass()
            .getResourceAsStream(
               "/compliance-level-2/0001-input-data-string/0001-input-data-string-test-01.xml");
         TestCases testCases = load(is);
         assertNotNull(testCases);
         assertEquals(2, testCases.getTestCase().size());
         for (TestCase testCase : testCases.getTestCase())
         {
            for (InputNode inputNode : testCase.getInputNode())
            {
               if (inputNode.getValue() != null)
               {
                  System.out
                     .println("inputNode is simple type with content: "
                        + ((Element) inputNode.getValue().getValue())
                           .getTextContent());
               }
               else
               {
                  System.out.println("inputNode is complex: "
                     + inputNode.getComponent());
               }
            }
         }

         String json = objectMapper.writeValueAsString(testCases);
         // assertions on JSON go here

         File testOutputDir = new File(baseOutputDir,
            "0001-input-data-string");
         testOutputDir.mkdirs();
         File jsonFile = new File(testOutputDir,
            "0001-input-data-string-test-01.json");
         System.out.println("Writing JSON output to: "
            + jsonFile.getAbsolutePath());
         objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile,
            testCases);

      }
      finally
      {
         try
         {
            is.close();
         }
         catch (Exception e)
         {
            ;
         }
      }
   }

   @SuppressWarnings({ "unchecked" })
   protected final TestCases load(InputStream inputStream)
      throws JAXBException
   {
      JAXBContext context = JAXBContext.newInstance(TestCases.class);
      Unmarshaller um = context.createUnmarshaller();

      Object obj = um.unmarshal(inputStream);
      if (obj instanceof JAXBElement<?>)
      {
         return ((JAXBElement<TestCases>) obj).getValue();
      }
      else
      {
         return (TestCases) obj;
      }
   }

}
