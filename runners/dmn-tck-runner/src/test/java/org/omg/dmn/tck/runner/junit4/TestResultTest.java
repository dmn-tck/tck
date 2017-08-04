package org.omg.dmn.tck.runner.junit4;

import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.omg.dmn.tck.runner.junit4.TestResult.Result;

public class TestResultTest {

	@Test
	public void testToString() {

		TestResult testResult = new TestResult(Result.ERROR).addLine(new TestResult(Result.ERROR, "My Message"));
		Assertions.assertThat(testResult.toStringWithLines()).contains("ERROR: <no message>");
		Assertions.assertThat(testResult.toStringWithLines()).contains("Lines: ");
		Assertions.assertThat(testResult.toStringWithLines()).contains("ERROR: My Message");
	}

	@Test
	public void testToStringNull() {
		// Expect no exception
		TestResult result = new TestResult(null).addLine(new TestResult(null, null));
		result.toStringWithLines();
	}

	@Test
	public void testToStringWithLinesWithNoLines() {
		// Expect no exception
		TestResult result = TestResult.error("error");
		Assertions.assertThat(result.toStringWithLines()).isEqualTo("ERROR: error");
	}

	@Test
	public void testToStream() {
		// GIVEN
		TestResult r = TestResult.error("uh oh - something went wrong :-(").addLine(TestResult.error("this went wrong"))
				.addLine(TestResult.success("this was good"));

		// WHEN
		Stream<TestResult> stream = r.toStream();
		// THEN
		Assertions.assertThat(stream).extracting(TestResult::getMsg)
				.containsExactly(
						"uh oh - something went wrong :-(",
						"this went wrong",
						"this was good");
	}

	@Test
	public void testToStreamFlatMapped() {
		// GIVEN
		TestResult r = TestResult.error("uh oh - something went wrong :-(")
				.addLine(TestResult.error("this went wrong").addLine(TestResult.ignore("nestedLine")))
				.addLine(TestResult.success("this was good"));

		// WHEN
		Stream<TestResult> stream = r.toStream();
		// THEN
		Assertions.assertThat(stream).extracting(TestResult::getMsg)
				.containsExactly(
						"uh oh - something went wrong :-(",
						"this went wrong",
						"nestedLine",
						"this was good");
	}

	@Test
	public void testToStringWithLinesNested() {
		// GIVEN
		TestResult r = TestResult.error("uh oh - something went wrong :-(")
				.addLine(TestResult.error("this went wrong").addLine(TestResult.ignore("nestedLine")))
				.addLine(TestResult.success("this was good"));

		// WHEN
		String s = r.toStringWithLines();
		// THEN
		Assertions.assertThat(s).contains("IGNORED: nestedLine");
	}

}
