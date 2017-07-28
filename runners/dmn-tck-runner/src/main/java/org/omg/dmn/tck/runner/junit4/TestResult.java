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

package org.omg.dmn.tck.runner.junit4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class to return a test result.
 * <p>
 * Allows to add nested lines for fine-grained reporting of TestResults. For
 * example to capture a report on comparison errors.
 * </p>
 */
public class TestResult {

	/**
	 * An enum for the possible test result.
	 */
	public enum Result {
		SUCCESS, ERROR, IGNORED;
	}

	private Result result;
	private String msg;
	private List<TestResult> lines = new ArrayList<>();

	public TestResult(Result result) {
		this(result, null);
	}

	public TestResult(Result result, String msg) {
		this.result = result;
		this.msg = msg;
	}

	/**
	 * Factory method to create TestResult with {@link Result#ERROR}.
	 * 
	 * @param msg
	 *            the msg for this TestResult
	 * @return the newly created error TestResult
	 */
	public static TestResult error( String msg ) {
		return new TestResult(Result.ERROR, msg);
	}

	/**
	 * Factory method to create TestResult with {@link Result#SUCCESS}.
	 * 
	 * @param msg
	 *            the msg for this TestResult
	 * @return the newly created success TestResult
	 */
	public static TestResult success( String msg ) {
		return new TestResult(Result.SUCCESS, msg);
	}

	/**
	 * Factory method to create TestResult with {@link Result#IGNORED}.
	 * 
	 * @param msg
	 *            the msg for this TestResult
	 * @return the newly created TestResult with status ignored
	 */
	public static TestResult ignore( String msg ) {
		return new TestResult(Result.IGNORED, msg);
	}

	public Result getResult() {
		return result;
	}

	public void setResult( Result result ) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg( String msg ) {
		this.msg = msg;
	}

	/**
	 * Adds a line to this TestResult. 
	 * 
	 * <p>
	 *  Lines are useful to provide detailed feedback or additional information on the 'overall' 
	 *  result and message. 
	 *  For example one could add detailed information about errors during node execution or 
	 *  for failed comparisons. 
	 * </p> 
	 * @param line the line to add
	 * @return this TestResult for method concatenation
	 */
	public TestResult addLine( TestResult line ) {
		this.lines.add(line);
		return this;
	}

	/**
	 * Adds lines to this TestResult.
	 * 
	 * @see #addLine(TestResult)
	 * @param lines
	 *            the lines to add
	 * @return this TestResult for method concatenation
	 */
	public TestResult addLines( Iterable<TestResult> lines ) {
		lines.forEach(this.lines::add);
		return this;
	}

	/**
	 * @see #addLine(TestResult)
	 * @return true in case this TestResult has lines. false otherwise.
	 */
	public boolean hasLines() {
		return !lines.isEmpty();
	}

	@Override
	public String toString() {
		return "TestResult{" + "result=" + result + ", msg='" + msg + '\'' + '}';
	}

	/**
	 * @return a stream representation of this TestResult containing this TestResult
	 *         as first element followed by its lines.
	 */
	public Stream<TestResult> toStream() {
		return Stream.concat(Stream.of(this), this.lines.stream().flatMap(TestResult::toStream));
	}

	/**
	 * Prints this TestResult to a string
	 * 
	 * @return a string representation of this TestResult including lines
	 */
	public String toStringWithLines() {
		return string(this) + (hasLines() ? "\n Lines: \n" + linesToString(l -> true) : "");
	}

	/**
	 * Returns the lines as string with a line break after each line. Nested lines
	 * are flattened. Example:
	 * 
	 * <pre>
	 * 	IGNORED: my message
	 * 	ERROR: something failed
	 * </pre>
	 * 
	 * @see #addLine(TestResult)
	 * @return this TestResults' lines as string representation
	 */
	public String linesToString( Predicate<TestResult> filter ) {
		if (lines.isEmpty()) {
			return "";
		}
		return lines.stream().filter(filter).flatMap(TestResult::toStream).map(this::string)
				.collect(Collectors.joining("\n"));
	}

	String string(TestResult l){
		return (l.getResult() != null ? l.getResult() : "TestResult")
				+ (l.getMsg() != null ? ": " + l.getMsg() : ": <no message>");
	}
}
