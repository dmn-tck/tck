1121-feel-years-and-months-duration-function
--------------------

### Description ###

DMN Model [1121-feel-years-and-months-duration-function.dmn](./1121-feel-years-and-months-duration-function.dmn) tests DMN specification conformance of `FEEL built-in function 'years and months duration(from [date and time], to [date and time])'`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result|
|-------------|-------------------------- |----------------|
|feel-years-and-months-duration-function_ErrorCase_1|years and months duration(null)|null (null)|
|feel-years-and-months-duration-function_ErrorCase_2|years and months duration(null,null)|null (null)|
|feel-years-and-months-duration-function_ErrorCase_3|years and months duration(date("2017-08-11"),null)|null (null)|
|feel-years-and-months-duration-function_ErrorCase_4|years and months duration(date and time("2017-12-31T13:00:00"),null)|null (null)|
|feel-years-and-months-duration-function_ErrorCase_5|years and months duration(null,date("2017-08-11"))|null (null)|
|feel-years-and-months-duration-function_ErrorCase_6|years and months duration(null,date and time("2019-10-01T12:32:59"))|null (null)|
|feel-years-and-months-duration-function_ErrorCase_7|years and months duration()|null (null)|
|feel-years-and-months-duration-function_8|years and months duration(date("2011-12-22"),date("2013-08-24"))|P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_9|years and months duration(date("2013-08-24"),date("2011-12-22"))|-P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_10|years and months duration(date("2015-01-21"),date("2016-01-21"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_11|years and months duration(date("2016-01-21"),date("2015-01-21"))|-P1Y (years and months duration)|
|feel-years-and-months-duration-function_12|years and months duration(date("2016-01-01"),date("2016-01-01"))|P0M (years and months duration)|
|feel-years-and-months-duration-function_13|years and months duration(date and time("2017-12-31T13:00:00"), date and time("2017-12-31T12:00:00"))|P0M (years and months duration)|
|feel-years-and-months-duration-function_14|years and months duration(date and time("2016-09-30T23:25:00"), date and time("2017-12-28T12:12:12"))|P1Y2M (years and months duration)|
|feel-years-and-months-duration-function_15|years and months duration(date and time("2010-05-30T03:55:58"), date and time("2017-12-15T00:59:59"))|P7Y6M (years and months duration)|
|feel-years-and-months-duration-function_16|years and months duration(date and time("2014-12-31T23:59:59"), date and time("2019-10-01T12:32:59"))|P4Y9M (years and months duration)|
|feel-years-and-months-duration-function_17|years and months duration(date and time("-2016-01-30T09:05:00"), date and time("-2017-02-28T02:02:02"))|-P11M (years and months duration)|
|feel-years-and-months-duration-function_18|years and months duration(date and time("2014-12-31T23:59:59"), date and time("-2019-10-01T12:32:59"))|-P4033Y2M (years and months duration)|
|feel-years-and-months-duration-function_19|years and months duration(date and time("2017-09-05T10:20:00-01:00"), date and time("-2019-10-01T12:32:59+02:00"))|-P4035Y11M (years and months duration)|
|feel-years-and-months-duration-function_20|years and months duration(date and time("2017-09-05T10:20:00+05:00"), date and time("2019-10-01T12:32:59"))|P2Y (years and months duration)|
|feel-years-and-months-duration-function_21|years and months duration(date and time("2016-08-25T15:20:59+02:00"), date and time("2017-08-10T10:20:00@Europe/Paris"))|P11M (years and months duration)|
|feel-years-and-months-duration-function_22|years and months duration(date and time("2011-12-31T10:15:30@Etc/UTC"), date and time("2017-08-10T10:20:00@Europe/Paris"))|P5Y7M (years and months duration)|
|feel-years-and-months-duration-function_23|years and months duration(date and time("2017-09-05T10:20:00@Etc/UTC"), date and time("2018-10-01T23:59:59"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_24|years and months duration(date and time("2011-08-25T15:59:59@Europe/Paris"), date and time("2015-08-25T15:20:59+02:00"))|P4Y (years and months duration)|
|feel-years-and-months-duration-function_25|years and months duration(date and time("2015-12-31T23:59:59.9999999"), date and time("2018-10-01T12:32:59.111111"))|P2Y9M (years and months duration)|
|feel-years-and-months-duration-function_26|years and months duration(date and time("2016-09-05T22:20:55.123456+05:00"), date and time("2019-10-01T12:32:59.32415645"))|P3Y (years and months duration)|
|feel-years-and-months-duration-function_ErrorCase_27|years and months duration(date(""),date(""))|null (null)|
|feel-years-and-months-duration-function_ErrorCase_28|years and months duration(2017)|null (null)|
|feel-years-and-months-duration-function_ErrorCase_29|years and months duration("2012T-12-2511:00:00Z")|null (null)|
|feel-years-and-months-duration-function_ErrorCase_30|years and months duration([],[])|null (null)|
|feel-years-and-months-duration-function_33|years and months duration(from:date and time("2016-12-31T00:00:01"),to:date and time("2017-12-31T23:59:59"))|P1Y (years and months duration)|
|feel-years-and-months-duration-function_34|years and months duration(from:date and time("2014-12-31T23:59:59"),to:date and time("2016-12-31T00:00:01"))|P2Y (years and months duration)|
|feel-years-and-months-duration-function_35|years and months duration(from:date("2011-12-22"),to:date("2013-08-24"))|P1Y8M (years and months duration)|
|feel-years-and-months-duration-function_36|years and months duration(from:date("2016-01-21"),to:date("2015-01-21"))|-P1Y (years and months duration)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1121-feel-years-and-months-duration-function.dmn](./1121-feel-years-and-months-duration-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  