package org.omg.dmn.tck.runner.camunda;

import io.inbot.utils.ReplacingInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class EscapeAmbiguousNamesInputStream extends FilterInputStream {

  public static final String ESCAPE_CHARACTER = "`";

  private static final List<String> TO_ESCAPE =
      Arrays.asList(
          "Full Name",
          "Monthly Salary",
          "Yearly Salary",
          "Years of Service",
          "is factor",
          "Another String",
          "Another Date",
          "Another number",
          "Another Date and Time",
          "Another Days and Time Duration",
          "Another Years and Months Duration",
          "Another Time",
          "Another boolean",
          "Base Vacation Days",
          "Extra days case 1",
          "Extra days case 2",
          "Extra days case 3",
          "Date-Time",
          "Date-Time2",
          "Employment Status",
          "Employment Status Statement",
          "Pre-bureauRiskCategory",
          "Post-bureauRiskCategory",
          "Pre-bureauAffordability",
          "Post-bureauAffordability");

  public EscapeAmbiguousNamesInputStream(InputStream in) {
    super(getFilterInputStream(in));
  }

  private static InputStream getFilterInputStream(InputStream in) {

    for (String replace : TO_ESCAPE) {
      in = new ReplacingInputStream(in, replace, ESCAPE_CHARACTER + replace + ESCAPE_CHARACTER);
    }

    return in;
  }
}
