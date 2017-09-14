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
package org.omg.dmn.tck.runner.trisotech.junit4;

import org.junit.runner.Description;
import org.junit.runner.RunWith;

@RunWith(TrisotechTCKTestSuite.class)

public class TrisotechTCKTest {

    private String id;

    private boolean passed = false;

    private Description testDescription;

    private Exception exception = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean getPassed() {
        return passed;
    }

    public void setDescription(Description testDescription) {
        this.testDescription = testDescription;

    }

    public Description getDescription() {
        return testDescription;
    }

    public void setException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

}
