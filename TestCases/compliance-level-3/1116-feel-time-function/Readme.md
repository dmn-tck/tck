1116-feel-time-function
--------------------

### Description ###

DMN Model [1116-feel-time-function.dmn](./1116-feel-time-function.dmn) tests DMN specification conformance of `FEEL built-in function 'time(from [string])', 'time(from [time, date and time])' and 'time(hour,minute,second,offset)'`. Tested category is `conversion functions`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result (Expected Type)|
|-------------|-------------------------- |--------------------------------|
|feel-time-function_ErrorCase_001_bdf26fdc72|time(null)|null (time)|
|feel-time-function_ErrorCase_002_9d2e399b96|time(null,11,45,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_003_d1f0ea5bb9|time(12,null,45,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_004_57aea91d1c|time(12,0,null,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_005_32ea20b34f|time(null,null,45,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_006_e266498180|time(null,11,null,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_007_ee82c7bf12|time(null,11,45,null)|null (time)|
|feel-time-function_ErrorCase_008_08078c6c29|time(12,null,null,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_009_804c21ed52|time(12,11,null,null)|null (time)|
|feel-time-function_ErrorCase_010_cc773bb44b|time(12,null,null,null)|null (time)|
|feel-time-function_ErrorCase_011_ad5b3a26b5|time(null,0,null,null)|null (time)|
|feel-time-function_ErrorCase_012_3c2f416fc9|time(null,null,15,null)|null (time)|
|feel-time-function_ErrorCase_013_7f22c0bda8|time(null,null,null,duration("P0D"))|null (time)|
|feel-time-function_ErrorCase_014_0dc13176e8|time(null,null,null,null)|null (time)|
|feel-time-function_015_376d693a79|time(12,00,00,null)|12:00:00 (time)|
|feel-time-function_ErrorCase_016_c3cccff405|time()|null (time)|
|feel-time-function_017_f3683885f5|time("01:02:03")|01:02:03 (time)|
|feel-time-function_018_35f1f2cce8|time("00:00:00")|00:00:00 (time)|
|feel-time-function_019_879be89d63|time("11:22:33.444")|11:22:33.444 (time)|
|feel-time-function_020_72b421086e|time("11:22:33.123456789")|11:22:33.123456789 (time)|
|feel-time-function_021_5c50fa1dff|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_022_5c50fa1dff|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_023_55e76d3595|time("11:00:00Z")|11:00:00Z (time)|
|feel-time-function_024_5cbbb85435|time("00:00:00Z")|00:00:00Z (time)|
|feel-time-function_025_5f7f735e8f|time("13:20:00+02:00")|13:20:00+02:00 (time)|
|feel-time-function_026_139b25b795|time("13:20:00-05:00")|13:20:00-05:00 (time)|
|feel-time-function_027_c5208af118|time("11:22:33-00:00")|11:22:33Z (time)|
|feel-time-function_028_45082fd26c|time("11:22:33+00:00")|11:22:33Z (time)|
|feel-time-function_029_eaea7a943c|string(time("00:01:00@Etc/UTC"))|"00:01:00@Etc/UTC" (string)|
|feel-time-function_030_f0d5c2c16a|string(time("00:01:00@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_031_390d4f4648|time(date and time("2017-08-10T10:20:00"))|10:20:00 (time)|
|feel-time-function_032_4d086a3b59|time(date and time("2017-08-10T10:20:00+00:00"))|10:20:00Z (time)|
|feel-time-function_033_d9b0d7f931|time(date and time("2017-08-10T10:20:00-00:00"))|10:20:00Z (time)|
|feel-time-function_034_8420160da1|time(date and time("2017-08-10T10:20:00+01:00"))|10:20:00+01:00 (time)|
|feel-time-function_035_13c312c376|time(date and time("2017-08-10T10:20:00-01:00"))|10:20:00-01:00 (time)|
|feel-time-function_036_fbfce88ac4|time(date and time("2017-08-10T10:20:00Z"))|10:20:00Z (time)|
|feel-time-function_037_eb05fabc01|string(time(date and time("2017-08-10T10:20:00@Europe/Paris")))|"10:20:00@Europe/Paris" (string)|
|feel-time-function_038_eed195f693|string(time(date and time("2017-09-04T11:20:00@Asia/Dhaka")))|"11:20:00@Asia/Dhaka" (string)|
|feel-time-function_039_05b311131c|time(11, 59, 45, null)|11:59:45 (time)|
|feel-time-function_040_5b65992f0d|time(11, 59, 45, duration("PT0H"))|11:59:45Z (time)|
|feel-time-function_041_6c9d17b491|time(11, 59, 45, duration("PT2H"))|11:59:45+02:00 (time)|
|feel-time-function_042_29a448d57e|time(11, 59, 45, duration("-PT2H"))|11:59:45-02:00 (time)|
|feel-time-function_043_00146f2977|time(11, 59, 00, duration("PT2H1M"))|11:59:00+02:01 (time)|
|feel-time-function_044_2edfae8414|time(11, 59, 00, duration("-PT2H1M"))|11:59:00-02:01 (time)|
|feel-time-function_045_3073ffd026|time(11, 59, 00, duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_046_ad1339e858|time(11, 59, 00, duration("-PT2H1M0S"))|11:59:00-02:01 (time)|
|feel-time-function_047_7b80221ec1|string(time(11, 59, 45, duration("PT2H45M55S")))|"11:59:45+02:45:55" (string)|
|feel-time-function_048_33cd7b9b15|string(time(11, 59, 45, duration("-PT2H45M55S")))|"11:59:45-02:45:55" (string)|
|feel-time-function_049_9bedd52886|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_050_9bedd52886|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_051_617d9e09d6|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01")))|23:59:01 (time)|
|feel-time-function_052_524d9a8146|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01.987654321")))|23:59:01.987654321 (time)|
|feel-time-function_053_a71d2a08f7|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30+02:00")))|09:15:30+02:00 (time)|
|feel-time-function_054_d825d58888|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30Z")))|09:15:30Z (time)|
|feel-time-function_055_3d956966c0|time(date("2017-08-10"))|00:00:00Z (time)|
|feel-time-function_ErrorCase_056_fdc3094237|time(2017)|null (time)|
|feel-time-function_ErrorCase_057_9b47db6ea4|time([])|null (time)|
|feel-time-function_ErrorCase_058_a8e828d64d|time("")|null (time)|
|feel-time-function_ErrorCase_059_d039115cce|time("23:59:60")|null (time)|
|feel-time-function_ErrorCase_060_81dd4b1639|time("24:00:01")|null (time)|
|feel-time-function_ErrorCase_061_c7e1705fe1|time("24:01:00")|null (time)|
|feel-time-function_ErrorCase_062_0cf4734fae|time("25:00:00")|null (time)|
|feel-time-function_ErrorCase_063_da2717f085|time("00:60:00")|null (time)|
|feel-time-function_ErrorCase_064_6cd1313fa9|time("00:00:61")|null (time)|
|feel-time-function_ErrorCase_065_e85c40b474|time("7:00:00")|null (time)|
|feel-time-function_ErrorCase_066_df74038c67|time("07:1:00")|null (time)|
|feel-time-function_ErrorCase_067_79eaef6fee|time("07:01:2")|null (time)|
|feel-time-function_ErrorCase_068_5116e12fd3|time("13:20:00@xyz/abc")|null (time)|
|feel-time-function_ErrorCase_069_8285edad7b|time("13:20:00+19:00")|null (time)|
|feel-time-function_ErrorCase_070_ad528abb23|time("13:20:00-19:00")|null (time)|
|feel-time-function_ErrorCase_071_5096701e2e|time("13:20:00+5:00")|null (time)|
|feel-time-function_ErrorCase_072_5096701e2e|time("13:20:00+5:00")|null (time)|
|feel-time-function_ErrorCase_073_8b2e39f570|time("13:20:00+5")|null (time)|
|feel-time-function_ErrorCase_074_cf9417648b|time("13:20:00+02:00@Europe/Paris")|null (time)|
|feel-time-function_ErrorCase_075_4c8c3835e4|time("7:20")|null (time)|
|feel-time-function_ErrorCase_076_a5fc245959|time("07:2")|null (time)|
|feel-time-function_ErrorCase_077_387d4411ea|time("11:30:00T")|null (time)|
|feel-time-function_ErrorCase_078_1606dda03d|time("2012T-12-2511:00:00Z")|null (time)|
|feel-time-function_ErrorCase_079_cb117ca612|time(24, 59, 45, null)|null (time)|
|feel-time-function_ErrorCase_080_a4daad060c|time(-24, 59, 45, null)|null (time)|
|feel-time-function_ErrorCase_081_c2fe73418b|time(23, 60, 45, null)|null (time)|
|feel-time-function_ErrorCase_082_d2d226c3cd|time(23, 59, 60, null)|null (time)|
|feel-time-function_083_2bbb8c86af|time(from:date and time("2012-12-24T23:59:00"))|23:59:00 (time)|
|feel-time-function_084_69f4e0231e|time(from: "12:45:00")|12:45:00 (time)|
|feel-time-function_085_36a78e5396|time(hour:11, minute:59, second:0, offset: duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_086_6b608254c7|time(hour:11, minute:59, second:0, offset: duration("-PT2H"))|11:59:00-02:00 (time)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1116-feel-time-function.dmn](./1116-feel-time-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  