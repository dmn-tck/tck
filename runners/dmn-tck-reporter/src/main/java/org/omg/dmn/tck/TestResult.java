/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
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

package org.omg.dmn.tck;

public class TestResult {

    public enum Result {
        SUCCESS, ERROR, IGNORED, UNKNOWN;

        public static Result fromString(String result) {
            if( SUCCESS.toString().equalsIgnoreCase( result ) ) {
                return SUCCESS;
            } else if( ERROR.toString().equalsIgnoreCase( result ) ) {
                return ERROR;
            } else if( IGNORED.toString().equalsIgnoreCase( result ) ) {
                return IGNORED;
            } else {
                return UNKNOWN;
            }
        }
    }

    private String testFolder;
    private String testSuit;
    private String testId;
    private Result result;
    private String comment;

    public TestResult(String testFolder, String testSuit, String testId, Result result, String comment) {
        this.testFolder = testFolder;
        this.testSuit = testSuit;
        this.testId = testId;
        this.result = result;
        this.comment = comment;
    }

    public String getTestFolder() {
        return testFolder;
    }

    public void setTestFolder(String testFolder) {
        this.testFolder = testFolder;
    }

    public String getTestSuit() {
        return testSuit;
    }

    public void setTestSuit(String testSuit) {
        this.testSuit = testSuit;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof TestResult) ) return false;

        TestResult that = (TestResult) o;

        if ( testFolder != null ? !testFolder.equals( that.testFolder ) : that.testFolder != null ) return false;
        if ( testSuit != null ? !testSuit.equals( that.testSuit ) : that.testSuit != null ) return false;
        if ( testId != null ? !testId.equals( that.testId ) : that.testId != null ) return false;
        if ( result != that.result ) return false;
        return comment != null ? comment.equals( that.comment ) : that.comment == null;
    }

    @Override
    public int hashCode() {
        int result1 = testFolder != null ? testFolder.hashCode() : 0;
        result1 = 31 * result1 + (testSuit != null ? testSuit.hashCode() : 0);
        result1 = 31 * result1 + (testId != null ? testId.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (comment != null ? comment.hashCode() : 0);
        return result1;
    }
}
