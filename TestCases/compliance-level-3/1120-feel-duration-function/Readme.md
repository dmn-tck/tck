1120-feel-duration-function
--------------------

### Description ###

DMN Model [1120-feel-duration-function.dmn](./1120-feel-duration-function.dmn) tests DMN specification conformance of `FEEL built-in function 'duration(from [String])'`. Tested category is `conversion functions`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result (Expected Type)|
|-------------|-------------------------- |--------------------------------|
|feel-duration-function_ErrorCase_001_f2c6cd6866|duration(null)|null (null)|
|feel-duration-function_ErrorCase_002_ddca5756ca|duration()|null (null)|
|feel-duration-function_003_951e1d1c31|duration("P1D")|P1D (days and time duration)|
|feel-duration-function_004_6b31e7cde7|duration("PT2H")|PT2H (days and time duration)|
|feel-duration-function_005_202d863d07|duration("PT3M")|PT3M (days and time duration)|
|feel-duration-function_006_a885f926d9|duration("PT4S")|PT4S (days and time duration)|
|feel-duration-function_007_2f0ad399f3|duration("PT0.999S")|PT0.999S (days and time duration)|
|feel-duration-function_008_747f56743d|duration("P1DT2H3M4.123456789S")|P1DT2H3M4.123456789S (days and time duration)|
|feel-duration-function_009_cef3c1ed26|duration("PT0S")|PT0S (days and time duration)|
|feel-duration-function_010_5b452a4975|duration("PT0.000S")|PT0S (days and time duration)|
|feel-duration-function_011_2169615b94|duration("PT0.S")|PT0S (days and time duration)|
|feel-duration-function_012_2affe6d169|duration("PT0M")|PT0S (days and time duration)|
|feel-duration-function_013_0e8e26513c|duration("PT0H")|PT0S (days and time duration)|
|feel-duration-function_014_598ba6fabd|duration("P0D")|PT0S (days and time duration)|
|feel-duration-function_015_ce2cb09830|duration("-PT1H2M")|-PT1H2M (days and time duration)|
|feel-duration-function_016_af3e37fdbd|duration("PT1000M")|PT16H40M (days and time duration)|
|feel-duration-function_017_4f4549fda4|duration("PT1000M0.999999999S")|PT16H40M0.999999999S (days and time duration)|
|feel-duration-function_018_f5ec776811|duration("PT555M")|PT9H15M (days and time duration)|
|feel-duration-function_019_2e6885755a|duration("PT61M")|PT1H1M (days and time duration)|
|feel-duration-function_020_af58b3766e|duration("PT24H")|P1D (days and time duration)|
|feel-duration-function_021_e48e70ad4e|duration("PT240H")|P10D (days and time duration)|
|feel-duration-function_022_668f24bed7|duration("P2DT100M")|P2DT1H40M (days and time duration)|
|feel-duration-function_023_6fc32087db|duration("PT3600S")|PT1H (days and time duration)|
|feel-duration-function_024_fd7000d72f|duration("P2DT274M")|P2DT4H34M (days and time duration)|
|feel-duration-function_025_f8ffbd8658|duration("P1Y2M")|P1Y2M (years and months duration)|
|feel-duration-function_026_e6c47f0cae|duration("P1Y")|P1Y (years and months duration)|
|feel-duration-function_027_33b7fb8704|duration("P0Y")|P0M (years and months duration)|
|feel-duration-function_028_971b94f16d|duration("P0M")|P0M (years and months duration)|
|feel-duration-function_029_1a12a226cc|duration("-P1Y")|-P1Y (years and months duration)|
|feel-duration-function_030_afac0f2062|duration("P26M")|P2Y2M (years and months duration)|
|feel-duration-function_031_1ddad718b9|duration("P1Y27M")|P3Y3M (years and months duration)|
|feel-duration-function_032_72c46a9ec9|duration("P100M")|P8Y4M (years and months duration)|
|feel-duration-function_033_5d1540abaf|duration("-P100M")|-P8Y4M (years and months duration)|
|feel-duration-function_034_aa9cbb21a6|duration("P999999999M")|P83333333Y3M (years and months duration)|
|feel-duration-function_035_93eef01ae7|duration("-P999999999M")|-P83333333Y3M (years and months duration)|
|feel-duration-function_036_5f2775875e|duration("P99999999Y")|P99999999Y (years and months duration)|
|feel-duration-function_037_8c9ea9c0e6|duration("-P99999999Y")|-P99999999Y (years and months duration)|
|feel-duration-function_038_67dc4c254c|duration(from:"P1Y")|P1Y (years and months duration)|
|feel-duration-function_039_4aa0b67804|duration(from:"PT24H")|P1D (days and time duration)|
|feel-duration-function_040_7d8eae461f|duration(from:"P26M")|P2Y2M (years and months duration)|
|feel-duration-function_ErrorCase_041_264bc9d682|duration("")|null (null)|
|feel-duration-function_ErrorCase_042_59a0000245|duration(2017)|null (null)|
|feel-duration-function_ErrorCase_043_253815dc6c|duration("2012T-12-2511:00:00Z")|null (null)|
|feel-duration-function_ErrorCase_044_f3b338d877|duration([])|null (null)|
|feel-duration-function_ErrorCase_045_2ffcc37801|duration("P")|null (null)|
|feel-duration-function_ErrorCase_046_eb637de5f6|duration("P0")|null (null)|
|feel-duration-function_ErrorCase_047_3210c46a5a|duration("1Y")|null (null)|
|feel-duration-function_ErrorCase_048_ab6244f767|duration("1D")|null (null)|
|feel-duration-function_ErrorCase_049_2225b503a0|duration("P1H")|null (null)|
|feel-duration-function_ErrorCase_050_dd2ef33bbd|duration("P1S")|null (null)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1120-feel-duration-function.dmn](./1120-feel-duration-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  