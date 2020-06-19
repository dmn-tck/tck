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

package org.omg.dmn.tck.marshaller;

import org.omg.dmn.tck.marshaller._20160719.TestCases;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * A simple helper class
 */
public class TckMarshallingHelper {

    @SuppressWarnings({ "unchecked" })
    public static final TestCases load(InputStream inputStream)
            throws JAXBException {
        JAXBContext context = JAXBContext.newInstance( TestCases.class);
        Unmarshaller um = context.createUnmarshaller();

        Object obj = um.unmarshal(inputStream);
        if (obj instanceof JAXBElement<?> ) {
            return ((JAXBElement<TestCases>) obj).getValue();
        } else {
            return (TestCases) obj;
        }
    }

}
