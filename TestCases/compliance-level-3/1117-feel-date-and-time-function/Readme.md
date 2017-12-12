1117-feel-date-and-time-function
--------------------

### Description ###

DMN Model [1117-feel-date-and-time-function.dmn](./1117-feel-date-and-time-function.dmn) tests DMN specification conformance of `FEEL built-in function 'date and time(from [string])' and 'date and time(date, time)'`. Tested category is `conversion functions`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result (Expected Type)|
|-------------|-------------------------- |--------------------------------|
|feel-date-and-time-function_ErrorCase_001_05fd7d6215|date and time(null)|null (date and time)|
|feel-date-and-time-function_ErrorCase_002_8c66ed2d1a|date and time(null,null)|null (date and time)|
|feel-date-and-time-function_ErrorCase_003_335cff371a|date and time(date and time("2017-08-10T10:20:00"),null)|null (date and time)|
|feel-date-and-time-function_ErrorCase_004_28ef3e7882|date and time(date("2017-08-10"),null)|null (date and time)|
|feel-date-and-time-function_ErrorCase_005_15df95b27a|date and time(null,time("23:59:01"))|null (date and time)|
|feel-date-and-time-function_ErrorCase_006_8c794da0bb|date and time()|null (date and time)|
|feel-date-and-time-function_007_59863d1b57|date and time("2012-12-24")|2012-12-24T00:00:00 (date and time)|
|feel-date-and-time-function_008_83eb9a93ba|date and time("2017-12-31T00:00:00")|2017-12-31T00:00:00 (date and time)|
|feel-date-and-time-function_009_1982fa549c|date and time("2017-12-31T11:22:33")|2017-12-31T11:22:33 (date and time)|
|feel-date-and-time-function_010_59f5b47012|date and time("-2017-12-31T11:22:33")|-2017-12-31T11:22:33 (date and time)|
|feel-date-and-time-function_011_eec2d5bdcd|string(date and time("99999-12-31T11:22:33"))|"99999-12-31T11:22:33" (string)|
|feel-date-and-time-function_012_225a105eef|string(date and time("-99999-12-31T11:22:33"))|"-99999-12-31T11:22:33" (string)|
|feel-date-and-time-function_013_c4fd0a0e8d|date and time("2017-12-31T11:22:33.345")|2017-12-31T11:22:33.345 (date and time)|
|feel-date-and-time-function_014_ded0e5fe2f|date and time("2017-12-31T11:22:33.123456789")|2017-12-31T11:22:33.123456789 (date and time)|
|feel-date-and-time-function_015_9e27148afd|date and time("2017-12-31T11:22:33Z")|2017-12-31T11:22:33Z (date and time)|
|feel-date-and-time-function_016_c08e4d417a|date and time("2017-12-31T11:22:33.567Z")|2017-12-31T11:22:33.567Z (date and time)|
|feel-date-and-time-function_017_47816add0e|date and time("2017-12-31T11:22:33+01:00")|2017-12-31T11:22:33+01:00 (date and time)|
|feel-date-and-time-function_018_0614e473e7|date and time("2017-12-31T11:22:33-02:00")|2017-12-31T11:22:33-02:00 (date and time)|
|feel-date-and-time-function_019_c312e3dfe3|date and time("2017-12-31T11:22:33+01:35")|2017-12-31T11:22:33+01:35 (date and time)|
|feel-date-and-time-function_020_29e0585b6f|date and time("2017-12-31T11:22:33-01:35")|2017-12-31T11:22:33-01:35 (date and time)|
|feel-date-and-time-function_021_99f0215b60|date and time("2017-12-31T11:22:33.456+01:35")|2017-12-31T11:22:33.456+01:35 (date and time)|
|feel-date-and-time-function_022_b8b20f0328|date and time("-2017-12-31T11:22:33.456+01:35")|-2017-12-31T11:22:33.456+01:35 (date and time)|
|feel-date-and-time-function_023_2e41497673|string(date and time("2011-12-31T10:15:30@Europe/Paris"))|"2011-12-31T10:15:30@Europe/Paris" (string)|
|feel-date-and-time-function_024_b4d1fb8735|string(date and time("2011-12-31T10:15:30@Etc/UTC"))|"2011-12-31T10:15:30@Etc/UTC" (string)|
|feel-date-and-time-function_025_0cb7f83ec6|string(date and time("2011-12-31T10:15:30.987@Europe/Paris"))|"2011-12-31T10:15:30.987@Europe/Paris" (string)|
|feel-date-and-time-function_026_5ba081cd5f|string(date and time("2011-12-31T10:15:30.123456789@Europe/Paris"))|"2011-12-31T10:15:30.123456789@Europe/Paris" (string)|
|feel-date-and-time-function_027_ae365197dd|string(date and time("999999999-12-31T23:59:59.999999999@Europe/Paris"))|"999999999-12-31T23:59:59.999999999@Europe/Paris" (string)|
|feel-date-and-time-function_028_1c3d56275f|string(date and time("-999999999-12-31T23:59:59.999999999+02:00"))|"-999999999-12-31T23:59:59.999999999+02:00" (string)|
|feel-date-and-time-function_029_e3a5e786a0|date and time(date("2017-01-01"),time("23:59:01"))|2017-01-01T23:59:01 (date and time)|
|feel-date-and-time-function_030_2f97bff606|date and time(date("2017-01-01"),time("23:59:01Z"))|2017-01-01T23:59:01Z (date and time)|
|feel-date-and-time-function_031_61e70c285f|date and time(date("2017-01-01"),time("23:59:01+02:00"))|2017-01-01T23:59:01+02:00 (date and time)|
|feel-date-and-time-function_032_1e95e8726e|string(date and time(date("2017-01-01"),time("23:59:01@Europe/Paris")))|"2017-01-01T23:59:01@Europe/Paris" (string)|
|feel-date-and-time-function_033_2fac4d6807|string(date and time(date("2017-01-01"),time("23:59:01.123456789@Europe/Paris")))|"2017-01-01T23:59:01.123456789@Europe/Paris" (string)|
|feel-date-and-time-function_034_75580be3aa|date and time(date and time("2017-08-10T10:20:00"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_035_831b1ad0c5|date and time(date and time("2017-08-10T10:20:00"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_036_189e1c3095|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_037_c7aec7ecf7|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_038_1493e6d873|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_039_593292b25c|date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_040_d9116e1daa|string(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_041_c6decfe6a3|date and time(date and time("2017-08-10T10:20:00+02:00"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_042_0cbcc3d1dc|date and time(date and time("2017-08-10T10:20:00+02:00"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_043_2e4177d00c|date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_044_9404547f9d|date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_045_5d93a541eb|date and time(date and time("2017-09-05T10:20:00+02:00"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_046_89c1cd8daa|date and time(date and time("2017-09-05T10:20:00+02:00"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_047_60ea7838ce|string(date and time(date and time("2017-09-05T10:20:00-01:00"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_048_e387922273|date and time(date and time("2017-08-10T10:20:00@Europe/Paris"),time("23:59:01"))|2017-08-10T23:59:01 (date and time)|
|feel-date-and-time-function_049_eb9cd1f777|date and time(date and time("2017-08-10T10:20:00@Europe/Paris"),time("23:59:01.987654321"))|2017-08-10T23:59:01.987654321 (date and time)|
|feel-date-and-time-function_050_2d960354af|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30+02:00"))|2017-09-05T09:15:30+02:00 (date and time)|
|feel-date-and-time-function_051_46bdaa00b0|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30Z"))|2017-09-05T09:15:30Z (date and time)|
|feel-date-and-time-function_052_911dbd0a24|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.987654321+02:00"))|2017-09-05T09:15:30.987654321+02:00 (date and time)|
|feel-date-and-time-function_053_283c083df9|date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.123456Z"))|2017-09-05T09:15:30.123456Z (date and time)|
|feel-date-and-time-function_054_2561a406fc|string(date and time(date and time("2017-09-05T10:20:00@Europe/Paris"),time("09:15:30.987654321@Europe/Paris")))|"2017-09-05T09:15:30.987654321@Europe/Paris" (string)|
|feel-date-and-time-function_ErrorCase_055_6ce9202e17|date and time(2017)|null (date and time)|
|feel-date-and-time-function_ErrorCase_056_e66397568e|date and time([])|null (date and time)|
|feel-date-and-time-function_ErrorCase_057_0452ca8719|date and time("")|null (date and time)|
|feel-date-and-time-function_ErrorCase_058_588040ceaa|date and time("11:00:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_059_dfc62a3ebc|date and time("2011-12-0310:15:30")|null (date and time)|
|feel-date-and-time-function_ErrorCase_060_890c302575|date and time("2011-12-03T10:15:30+01:00@Europe/Paris")|null (date and time)|
|feel-date-and-time-function_ErrorCase_061_38ea1fc94d|date and time("9999999999-12-27T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_062_528aa370a3|date and time("2017-13-10T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_063_2c94303011|date and time("2017-00-10T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_064_926a372666|date and time("2017-13-32T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_065_a13de18ee4|date and time("2017-13-0T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_066_e9f3d6d2c2|date and time("998-12-31T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_067_35fef99b53|date and time("01211-12-31T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_068_abaa1c2774|date and time("+99999-12-01T11:22:33")|null (date and time)|
|feel-date-and-time-function_ErrorCase_069_ca84e9c806|date and time("2017-12-31T24:00:01")|null (date and time)|
|feel-date-and-time-function_ErrorCase_070_889c75a0cf|date and time("2017-12-31T24:01:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_071_e90b813dfe|date and time("2017-12-31T25:00:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_072_9f3e9b9c21|date and time("2017-12-31T00:60:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_073_717548bec6|date and time("2017-12-31T00:00:61")|null (date and time)|
|feel-date-and-time-function_ErrorCase_074_a15e7f8d29|date and time("2017-12-31T7:00:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_075_4c3b8e7097|date and time("2017-12-31T07:1:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_076_4d31fed18e|date and time("2017-12-31T07:01:2")|null (date and time)|
|feel-date-and-time-function_ErrorCase_077_f83b3ac8bb|date and time("2017-12-31T13:20:00@xyz/abc")|null (date and time)|
|feel-date-and-time-function_ErrorCase_078_e113dabcdd|date and time("2017-12-31T13:20:00+19:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_079_2e6f80eb94|date and time("2017-12-31T13:20:00-19:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_080_69de952053|date and time("2017-12-31T13:20:00+05:0")|null (date and time)|
|feel-date-and-time-function_ErrorCase_081_e063215a7c|date and time("2017-12-31T13:20:00+5:00")|null (date and time)|
|feel-date-and-time-function_ErrorCase_082_5b6ed4e801|date and time("2017-12-31T13:20:00+5")|null (date and time)|
|feel-date-and-time-function_ErrorCase_083_4f41731f2a|date and time("2017-12-31T13:20:00+02:00@Europe/Paris")|null (date and time)|
|feel-date-and-time-function_ErrorCase_084_c633b01603|date and time("2017-12-31T7:20")|null (date and time)|
|feel-date-and-time-function_ErrorCase_085_a604a1bc80|date and time("2017-12-31T07:2")|null (date and time)|
|feel-date-and-time-function_086_12ca8ac1d3|date and time(from:"2012-12-24T23:59:00")|2012-12-24T23:59:00 (date and time)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1117-feel-date-and-time-function.dmn](./1117-feel-date-and-time-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  