1121-feel-years-and-months-duration-function
--------------------

### Description ###

DMN Model [1121-feel-years-and-months-duration-function.dmn](./1121-feel-years-and-months-duration-function.dmn) tests DMN specification conformance of `FEEL built-in function 'years and months duration(from [date and time], to [date and time])'`. Tested category is `conversion functions`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result (Expected Type)|
|-------------|-------------------------- |--------------------------------|
|feel-years-and-months-duration-function_ErrorCase_001_b24a0c91f2|years and months duration(null)|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_002_4e7651ae0e|years and months duration(null,null)|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_003_0886738d31|years and months duration(date("2017-08-11"),null)|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_004_1bdfef922b|years and months duration(date and time("2017-12-31T13:00:00"),null)|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_005_d0a077da4e|years and months duration(null,date("2017-08-11"))|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_006_f20de28d3f|years and months duration(null,date and time("2019-10-01T12:32:59"))|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_007_0921c3d61a|years and months duration()|null (years and months duration)|
|feel-years-and-months-duration-function_008_015d35b442|years and months duration(date("2011-12-22"),date("2013-08-24"))|P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_009_635028a5d8|years and months duration(date("2013-08-24"),date("2011-12-22"))|-P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_010_caaa2e5002|years and months duration(date("2015-01-21"),date("2016-01-21"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_011_3fac022eb0|years and months duration(date("2016-01-21"),date("2015-01-21"))|-P1Y (years and months duration)|
|feel-years-and-months-duration-function_012_331ef38ce0|years and months duration(date("2016-01-01"),date("2016-01-01"))|P0M (years and months duration)|
|feel-years-and-months-duration-function_013_2f3cc46d9d|years and months duration(date and time("2017-12-31T13:00:00"), date and time("2017-12-31T12:00:00"))|P0M (years and months duration)|
|feel-years-and-months-duration-function_014_1fadbba7cd|years and months duration(date and time("2016-09-30T23:25:00"), date and time("2017-12-28T12:12:12"))|P1Y2M (years and months duration)|
|feel-years-and-months-duration-function_015_0e496f94fc|years and months duration(date and time("2010-05-30T03:55:58"), date and time("2017-12-15T00:59:59"))|P7Y6M (years and months duration)|
|feel-years-and-months-duration-function_016_b38662aa93|years and months duration(date and time("2014-12-31T23:59:59"), date and time("2019-10-01T12:32:59"))|P4Y9M (years and months duration)|
|feel-years-and-months-duration-function_017_86744b9a54|years and months duration(date and time("-2016-01-30T09:05:00"), date and time("-2017-02-28T02:02:02"))|-P11M (years and months duration)|
|feel-years-and-months-duration-function_018_8a9ed1d66d|years and months duration(date and time("2014-12-31T23:59:59"), date and time("-2019-10-01T12:32:59"))|-P4033Y2M (years and months duration)|
|feel-years-and-months-duration-function_019_90c2084588|years and months duration(date and time("2017-09-05T10:20:00-01:00"), date and time("-2019-10-01T12:32:59+02:00"))|-P4035Y11M (years and months duration)|
|feel-years-and-months-duration-function_020_8ead9a0377|years and months duration(date and time("2017-09-05T10:20:00+05:00"), date and time("2019-10-01T12:32:59"))|P2Y (years and months duration)|
|feel-years-and-months-duration-function_021_8a7d311ae9|years and months duration(date and time("2016-08-25T15:20:59+02:00"), date and time("2017-08-10T10:20:00@Europe/Paris"))|P11M (years and months duration)|
|feel-years-and-months-duration-function_022_87e369773b|years and months duration(date and time("2011-12-31T10:15:30@Etc/UTC"), date and time("2017-08-10T10:20:00@Europe/Paris"))|P5Y7M (years and months duration)|
|feel-years-and-months-duration-function_023_6385c7a83e|years and months duration(date and time("2017-09-05T10:20:00@Etc/UTC"), date and time("2018-10-01T23:59:59"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_024_e96d1bd93a|years and months duration(date and time("2011-08-25T15:59:59@Europe/Paris"), date and time("2015-08-25T15:20:59+02:00"))|P4Y (years and months duration)|
|feel-years-and-months-duration-function_025_161f6fca54|years and months duration(date and time("2015-12-31T23:59:59.9999999"), date and time("2018-10-01T12:32:59.111111"))|P2Y9M (years and months duration)|
|feel-years-and-months-duration-function_026_fcc906b375|years and months duration(date and time("2016-09-05T22:20:55.123456+05:00"), date and time("2019-10-01T12:32:59.32415645"))|P3Y (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_027_3374dd86c6|years and months duration(date(""),date(""))|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_028_77600e7b35|years and months duration(2017)|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_029_15a0d0d9c1|years and months duration("2012T-12-2511:00:00Z")|null (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_030_ec16878596|years and months duration([],[])|null (years and months duration)|
|feel-years-and-months-duration-function_031_4fd9c09d89|years and months duration(date("2013-08-24"), date and time("2017-12-15T00:59:59"))|P4Y3M (years and months duration)|
|feel-years-and-months-duration-function_032_2a09ac80d0|years and months duration(date and time("2017-02-28T23:59:59"), date("2019-07-23") )|P2Y4M (years and months duration)|
|feel-years-and-months-duration-function_033_7333eca866|years and months duration(from:date and time("2016-12-31T00:00:01"),to:date and time("2017-12-31T23:59:59"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_034_c2cc06724c|years and months duration(from:date and time("2014-12-31T23:59:59"),to:date and time("2016-12-31T00:00:01"))|P2Y (years and months duration)|
|feel-years-and-months-duration-function_035_dc05f9555d|years and months duration(from:date("2011-12-22"),to:date("2013-08-24"))|P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_036_f8c8b02ba3|years and months duration(from:date("2016-01-21"),to:date("2015-01-21"))|-P1Y (years and months duration)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1121-feel-years-and-months-duration-function.dmn](./1121-feel-years-and-months-duration-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  