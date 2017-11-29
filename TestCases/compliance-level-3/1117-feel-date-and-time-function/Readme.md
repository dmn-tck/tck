1117-feel-date-and-time-function
--------------------

### Description ###

DMN Model [1117-feel-date-and-time-function.dmn](./1117-feel-date-and-time-function.dmn) tests DMN specification conformance of `FEEL built-in function 'date and time(from [string])' and 'date and time(date, time)'`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result|
|-------------|-------------------------- |----------------|
|feel-date-and-time-function_ErrorCase_1|date and time(null)|null (null)|
|feel-date-and-time-function_ErrorCase_2|date and time(null,null)|null (null)|
|feel-date-and-time-function_ErrorCase_3|date and time(date and time("2017-08-10T10:20:00"),null)|null (null)|
|feel-date-and-time-function_ErrorCase_4|date and time(date("2017-08-10"),null)|null (null)|
|feel-date-and-time-function_ErrorCase_5|date and time(null,time("23:59:01"))|null (null)|
|feel-date-and-time-function_ErrorCase_6|date and time()|null (null)|
|feel-date-and-time-function_7|date and time("2017-12-31T00:00:00")|2017-12-31T00:00:00 (date and time)|
|feel-date-and-time-function_8|date and time("2017-12-31T11:22:33")|2017-12-31T11:22:33 (date and time)|
|feel-date-and-time-function_9|date and time("-2017-12-31T11:22:33")|-2017-12-31T11:22:33 (date and time)|
|feel-date-and-time-function_10|string(date and time("99999-12-31T11:22:33"))|"99999-12-31T11:22:33" (string)|
|feel-date-and-time-function_11|string(date and time("-99999-12-31T11:22:33"))|"-99999-12-31T11:22:33" (string)|
|feel-date-and-time-function_12|date and time("2017-12-31T11:22:33.345")|2017-12-31T11:22:33.345 (date and time)|
|feel-date-and-time-function_13|date and time("2017-12-31T11:22:33.123456789")|2017-12-31T11:22:33.123456789 (date and time)|
|feel-date-and-time-function_14|date and time("2017-12-31T11:22:33Z")|2017-12-31T11:22:33Z (date and time)|
|feel-date-and-time-function_15|date and time("2017-12-31T11:22:33.567Z")|2017-12-31T11:22:33.567Z (date and time)|
|feel-date-and-time-function_16|date and time("2017-12-31T11:22:33+01:00")|2017-12-31T11:22:33+01:00 (date and time)|
|feel-date-and-time-function_17|date and time("2017-12-31T11:22:33-02:00")|2017-12-31T11:22:33-02:00 (date and time)|
|feel-date-and-time-function_18|date and time("2017-12-31T11:22:33+01:35")|2017-12-31T11:22:33+01:35 (date and time)|
|feel-date-and-time-function_19|date and time("2017-12-31T11:22:33-01:35")|2017-12-31T11:22:33-01:35 (date and time)|
|feel-date-and-time-function_20|date and time("2017-12-31T11:22:33.456+01:35")|2017-12-31T11:22:33.456+01:35 (date and time)|
|feel-date-and-time-function_21|date and time("-2017-12-31T11:22:33.456+01:35")|-2017-12-31T11:22:33.456+01:35 (date and time)|
|feel-date-and-time-function_22|string(date and time("2011-12-31T10:15:30@Europe/Paris"))|"2011-12-31T10:15:30@Europe/Paris" (string)|
|feel-date-and-time-function_23|string(date and time("2011-12-31T10:15:30@Etc/UTC"))|"2011-12-31T10:15:30@Etc/UTC" (string)|
|feel-date-and-time-function_24|string(date and time("2011-12-31T10:15:30.987@Europe/Paris"))|"2011-12-31T10:15:30.987@Europe/Paris" (string)|
|feel-date-and-time-function_25|string(date and time("2011-12-31T10:15:30.123456789@Europe/Paris"))|"2011-12-31T10:15:30.123456789@Europe/Paris" (string)|
|feel-date-and-time-function_26|string(date and time("999999999-12-31T23:59:59.999999999@Europe/Paris"))|"999999999-12-31T23:59:59.999999999@Europe/Paris" (string)|
|feel-date-and-time-function_27|string(date and time("-999999999-12-31T23:59:59.999999999+02:00"))|"-999999999-12-31T23:59:59.999999999+02:00" (string)|
|feel-date-and-time-function_28|date and time(date("2017-01-01"),time("23:59:01"))|2017-01-01T23:59:01 (date and time)|
|feel-date-and-time-function_29|date and time(date("2017-01-01"),time("23:59:01Z"))|2017-01-01T23:59:01Z (date and time)|
|feel-date-and-time-function_30|date and time(date("2017-01-01"),time("23:59:01+02:00"))|2017-01-01T23:59:01+02:00 (date and time)|
|feel-date-and-time-function_31|string(date and time(date("2017-01-01"),time("23:59:01@Europe/Paris")))|"2017-01-01T23:59:01@Europe/Paris" (string)|
|feel-date-and-time-function_32|string(date and time(date("2017-01-01"),time("23:59:01.123456789@Europe/Paris")))|"2017-01-01T23:59:01.123456789@Europe/Paris" (string)|
|feel-date-and-time-function_33|date and time(date and time("2017-08-10T10:20:00"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_34|date and time(date and time("2017-08-10T10:20:00"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_35|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_36|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_37|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_38|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_39|string(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_40|date and time(date and time("2017-08-10T10:20:00+02:00"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_41|date and time(date and time("2017-08-10T10:20:00+02:00"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_42|date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_43|date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_44|date and time(date and time("2017-09-05T10:20:00+02:00"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_45|date and time(date and time("2017-09-05T10:20:00+02:00"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_46|string(date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_47|date and time(date and time("2017-08-10T10:20:00@Europe/Paris"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_48|date and time(date and time("2017-08-10T10:20:00@Europe/Paris"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_49|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_50|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_51|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_52|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_53|string(date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_ErrorCase_54|date and time(2017)|null (null)|
|feel-date-and-time-function_ErrorCase_55|date and time([])|null (null)|
|feel-date-and-time-function_ErrorCase_56|date and time("")|null (null)|
|feel-date-and-time-function_57|date and time("2012-12-24")|2012-12-24T00:00:00 (date and time)|
|feel-date-and-time-function_ErrorCase_58|date and time("11:00:00")|null (null)|
|feel-date-and-time-function_ErrorCase_59|date and time("2011-12-0310:15:30")|null (null)|
|feel-date-and-time-function_ErrorCase_60|date and time("2011-12-03T10:15:30+01:00@Europe/Paris")|null (null)|
|feel-date-and-time-function_ErrorCase_61|date and time("9999999999-12-27T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_62|date and time("2017-13-10T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_63|date and time("2017-00-10T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_64|date and time("2017-13-32T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_65|date and time("2017-13-0T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_66|date and time("998-12-31T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_67|date and time("01211-12-31T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_68|date and time("+99999-12-01T11:22:33")|null (null)|
|feel-date-and-time-function_ErrorCase_69|date and time("2017-12-31T24:00:01")|null (null)|
|feel-date-and-time-function_ErrorCase_70|date and time("2017-12-31T24:01:00")|null (null)|
|feel-date-and-time-function_ErrorCase_71|date and time("2017-12-31T25:00:00")|null (null)|
|feel-date-and-time-function_ErrorCase_72|date and time("2017-12-31T00:60:00")|null (null)|
|feel-date-and-time-function_ErrorCase_73|date and time("2017-12-31T00:00:61")|null (null)|
|feel-date-and-time-function_ErrorCase_74|date and time("2017-12-31T7:00:00")|null (null)|
|feel-date-and-time-function_ErrorCase_75|date and time("2017-12-31T07:1:00")|null (null)|
|feel-date-and-time-function_ErrorCase_76|date and time("2017-12-31T07:01:2")|null (null)|
|feel-date-and-time-function_ErrorCase_77|date and time("2017-12-31T13:20:00@xyz/abc")|null (null)|
|feel-date-and-time-function_ErrorCase_78|date and time("2017-12-31T13:20:00+19:00")|null (null)|
|feel-date-and-time-function_ErrorCase_79|date and time("2017-12-31T13:20:00-19:00")|null (null)|
|feel-date-and-time-function_ErrorCase_80|date and time("2017-12-31T13:20:00+05:0")|null (null)|
|feel-date-and-time-function_ErrorCase_81|date and time("2017-12-31T13:20:00+5:00")|null (null)|
|feel-date-and-time-function_ErrorCase_82|date and time("2017-12-31T13:20:00+5")|null (null)|
|feel-date-and-time-function_ErrorCase_83|date and time("2017-12-31T13:20:00+02:00@Europe/Paris")|null (null)|
|feel-date-and-time-function_ErrorCase_84|date and time("2017-12-31T7:20")|null (null)|
|feel-date-and-time-function_ErrorCase_85|date and time("2017-12-31T07:2")|null (null)|
|feel-date-and-time-function_86|date and time(from:"2012-12-24T23:59:00")|2012-12-24T23:59:00 (date and time)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1117-feel-date-and-time-function.dmn](./1117-feel-date-and-time-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  