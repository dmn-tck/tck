1120-feel-duration-function
--------------------

### Description ###

DMN Model [1120-feel-duration-function.dmn](./1120-feel-duration-function.dmn) tests DMN specification conformance of `FEEL built-in function 'duration(from [String])'`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result|
|-------------|-------------------------- |----------------|
|feel-duration-function_ErrorCase_1|duration(null)|null (null)|
|feel-duration-function_ErrorCase_2|duration()|null (null)|
|feel-duration-function_3|duration("P1D")|P1D (days and time duration)|
|feel-duration-function_4|duration("PT2H")|PT2H (days and time duration)|
|feel-duration-function_5|duration("PT3M")|PT3M (days and time duration)|
|feel-duration-function_6|duration("PT4S")|PT4S (days and time duration)|
|feel-duration-function_7|duration("PT0.999S")|PT0.999S (days and time duration)|
|feel-duration-function_8|duration("P1DT2H3M4.123456789S")|P1DT2H3M4.123456789S (days and time duration)|
|feel-duration-function_9|duration("PT0S")|PT0S (days and time duration)|
|feel-duration-function_10|duration("PT0.000S")|PT0S (days and time duration)|
|feel-duration-function_11|duration("PT0.S")|PT0S (days and time duration)|
|feel-duration-function_12|duration("PT0M")|PT0S (days and time duration)|
|feel-duration-function_13|duration("PT0H")|PT0S (days and time duration)|
|feel-duration-function_14|duration("P0D")|PT0S (days and time duration)|
|feel-duration-function_15|duration("-PT1H2M")|-PT1H2M (days and time duration)|
|feel-duration-function_16|duration("PT1000M")|PT16H40M (days and time duration)|
|feel-duration-function_17|duration("PT1000M0.999999999S")|PT16H40M0.999999999S (days and time duration)|
|feel-duration-function_18|duration("PT555M")|PT9H15M (days and time duration)|
|feel-duration-function_19|duration("PT61M")|PT1H1M (days and time duration)|
|feel-duration-function_20|duration("PT24H")|P1D (days and time duration)|
|feel-duration-function_21|duration("PT240H")|P10D (days and time duration)|
|feel-duration-function_22|duration("P2DT100M")|P2DT1H40M (days and time duration)|
|feel-duration-function_23|duration("PT3600S")|PT1H (days and time duration)|
|feel-duration-function_24|duration("P2DT274M")|P2DT4H34M (days and time duration)|
|feel-duration-function_25|duration("P1Y2M")|P1Y2M (years and months duration)|
|feel-duration-function_26|duration("P1Y")|P1Y (years and months duration)|
|feel-duration-function_27|duration("P0Y")|P0M (years and months duration)|
|feel-duration-function_28|duration("P0M")|P0M (years and months duration)|
|feel-duration-function_29|duration("-P1Y")|-P1Y (years and months duration)|
|feel-duration-function_30|duration("P26M")|P2Y2M (years and months duration)|
|feel-duration-function_31|duration("P1Y27M")|P3Y3M (years and months duration)|
|feel-duration-function_32|duration("P100M")|P8Y4M (years and months duration)|
|feel-duration-function_33|duration("-P100M")|-P8Y4M (years and months duration)|
|feel-duration-function_34|duration("P999999999M")|P83333333Y3M (years and months duration)|
|feel-duration-function_35|duration("-P999999999M")|-P83333333Y3M (years and months duration)|
|feel-duration-function_36|duration("P99999999Y")|P99999999Y (years and months duration)|
|feel-duration-function_37|duration("-P99999999Y")|-P99999999Y (years and months duration)|
|feel-duration-function_38|duration(from:"P1Y")|P1Y (years and months duration)|
|feel-duration-function_39|duration(from:"PT24H")|P1D (days and time duration)|
|feel-duration-function_40|duration(from:"P26M")|P2Y2M (years and months duration)|
|feel-duration-function_ErrorCase_41|duration("")|null (null)|
|feel-duration-function_ErrorCase_42|duration(2017)|null (null)|
|feel-duration-function_ErrorCase_43|duration("2012T-12-2511:00:00Z")|null (null)|
|feel-duration-function_ErrorCase_44|duration([])|null (null)|
|feel-duration-function_ErrorCase_45|duration("1Y")|null (null)|
|feel-duration-function_ErrorCase_46|duration("P")|null (null)|
|feel-duration-function_ErrorCase_47|duration("P0")|null (null)|
|feel-duration-function_ErrorCase_48|duration("P-1Y")|null (null)|
|feel-duration-function_ErrorCase_49|duration("P-2M")|null (null)|
|feel-duration-function_ErrorCase_50|duration("P-1Y-2M")|null (null)|
|feel-duration-function_ErrorCase_51|duration("1D")|null (null)|
|feel-duration-function_ErrorCase_52|duration("P")|null (null)|
|feel-duration-function_ErrorCase_53|duration("P0")|null (null)|
|feel-duration-function_ErrorCase_54|duration("P-1D")|null (null)|
|feel-duration-function_ErrorCase_55|duration("PT-2H")|null (null)|
|feel-duration-function_ErrorCase_56|duration("PT-3M")|null (null)|
|feel-duration-function_ErrorCase_57|duration("PT-4S")|null (null)|
|feel-duration-function_ErrorCase_58|duration("PT-1DT-2H-3M-4S")|null (null)|
|feel-duration-function_ErrorCase_59|duration("P1H")|null (null)|
|feel-duration-function_ErrorCase_60|duration("P1S")|null (null)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1120-feel-duration-function.dmn](./1120-feel-duration-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  