1115-feel-date-function
--------------------

### Description ###

DMN Model [1115-feel-date-function.dmn](./1115-feel-date-function.dmn) tests DMN specification conformance of `FEEL built-in function 'date(from [string])', 'date(from [date and time])' and 'date(year,month,day)'`. Tested category is `conversion functions`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result (Expected Type)|
|-------------|-------------------------- |--------------------------------|
|feel-date-function_ErrorCase_001_e9ae035ab9|date(null)|null (date)|
|feel-date-function_ErrorCase_002_9b9e6085ce|date(null,null)|null (date)|
|feel-date-function_ErrorCase_003_e4b7918d8f|date(null,2,1)|null (date)|
|feel-date-function_ErrorCase_004_f24ed41117|date(2017,null,1)|null (date)|
|feel-date-function_ErrorCase_005_3540a22062|date(2017,1,null)|null (date)|
|feel-date-function_ErrorCase_006_616e24dbb7|date(null,null,1)|null (date)|
|feel-date-function_ErrorCase_007_cda82a5d01|date(null,02,null)|null (date)|
|feel-date-function_ErrorCase_008_492649d3d0|date(2017,null,null)|null (date)|
|feel-date-function_ErrorCase_009_9e00bbdad3|date(null,null,null)|null (date)|
|feel-date-function_ErrorCase_010_6d4d58d23a|date()|null (date)|
|feel-date-function_011_5f0b42b1f8|date("2017-12-31")|2017-12-31 (date)|
|feel-date-function_012_d9e4b97438|date("2017-01-01")|2017-01-01 (date)|
|feel-date-function_013_d7e901ee86|date("-2017-12-31")|-2017-12-31 (date)|
|feel-date-function_014_fad7e00633|date("-2017-01-01")|-2017-01-01 (date)|
|feel-date-function_015_1dd66594cf|string(date("999999999-12-31"))|"999999999-12-31" (string)|
|feel-date-function_016_31f3fef4a0|string(date("-999999999-12-31"))|"-999999999-12-31" (string)|
|feel-date-function_017_887dfef005|date(date and time("2017-08-14T14:25:00"))|2017-08-14 (date)|
|feel-date-function_018_fc0ef0c8cb|date(date and time(date and time("2017-08-14T14:25:00"),time("10:50:00")))|2017-08-14 (date)|
|feel-date-function_019_b2b82796ce|date(date and time("2017-08-14T14:25:00.123456789"))|2017-08-14 (date)|
|feel-date-function_020_7d56b7bf63|date(date and time("2017-09-03T09:45:30@Europe/Paris"))|2017-09-03 (date)|
|feel-date-function_021_95fb3d9984|date(date and time("2017-09-06T09:45:30@Asia/Dhaka"))|2017-09-06 (date)|
|feel-date-function_022_4063db2d59|date(date and time("2012-12-25T11:00:00Z"))|2012-12-25 (date)|
|feel-date-function_023_4a1f604006|date(date and time("2017-08-03T10:15:30+01:00"))|2017-08-03 (date)|
|feel-date-function_024_3cb98a2bb8|date(date("2017-10-11"))|2017-10-11 (date)|
|feel-date-function_025_cf0ad1313c|date(2017,12,31)|2017-12-31 (date)|
|feel-date-function_026_cedd7e5e5f|date(2017,01,01)|2017-01-01 (date)|
|feel-date-function_027_987c5be372|date(-2017,12,31)|-2017-12-31 (date)|
|feel-date-function_028_35ca79a6cd|date(-2017,01,01)|-2017-01-01 (date)|
|feel-date-function_029_88f5c7c90f|string(date(999999999,12,31))|"999999999-12-31" (string)|
|feel-date-function_030_9184a7bfc3|string(date(-999999999,12,31))|"-999999999-12-31" (string)|
|feel-date-function_ErrorCase_031_4f5ec70669|date("2012-12-25T")|null (date)|
|feel-date-function_ErrorCase_032_fc66cc2fec|date("")|null (date)|
|feel-date-function_ErrorCase_033_c3a5600c62|date("2012/12/25")|null (date)|
|feel-date-function_ErrorCase_034_7d2e18a10c|date("0000-12-25T")|null (date)|
|feel-date-function_ErrorCase_035_e6c1bb43fd|date("9999999999-12-25")|null (date)|
|feel-date-function_ErrorCase_036_b826a6b5f9|date("2017-13-10")|null (date)|
|feel-date-function_ErrorCase_037_cfd70896b6|date("2017-12-32")|null (date)|
|feel-date-function_ErrorCase_038_c26782f559|date("998-12-31")|null (date)|
|feel-date-function_ErrorCase_039_67a6eafa3f|date("01211-12-31")|null (date)|
|feel-date-function_ErrorCase_040_dd2a2ed4a2|date("2012T-12-2511:00:00Z")|null (date)|
|feel-date-function_ErrorCase_041_9e7e388146|date("+2012-12-02")|null (date)|
|feel-date-function_ErrorCase_042_8f5dd97588|date(2017,13,31)|null (date)|
|feel-date-function_ErrorCase_043_8f82301fac|date(2017,12,32)|null (date)|
|feel-date-function_ErrorCase_044_74893220b4|date(2017,-8,2)|null (date)|
|feel-date-function_ErrorCase_045_969723fed5|date(2017,8,-2)|null (date)|
|feel-date-function_ErrorCase_046_36bf30268a|date(-1000999999,12,01)|null (date)|
|feel-date-function_ErrorCase_047_ba717eb672|date(1000999999,12,32)|null (date)|
|feel-date-function_ErrorCase_048_25595a6420|date(1)|null (date)|
|feel-date-function_ErrorCase_049_a1644ce710|date([])|null (date)|
|feel-date-function_050_8f1e299951|date(from:"2012-12-25")|2012-12-25 (date)|
|feel-date-function_051_ad98079864|date(from:date and time("2017-08-30T10:25:00"))|2017-08-30 (date)|
|feel-date-function_052_63457d78b7|date(year:2017,month:08,day:30)|2017-08-30 (date)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1115-feel-date-function.dmn](./1115-feel-date-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  