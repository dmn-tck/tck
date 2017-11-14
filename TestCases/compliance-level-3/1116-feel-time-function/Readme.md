1116-feel-time-function
--------------------

### Description ###

DMN Model [1116-feel-time-function.dmn](./1116-feel-time-function.dmn) tests DMN specification conformance of `FEEL built-in function 'time(from [string])', 'time(from [time, date and time])' and 'time(hour,minute,second,offset)'`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result|
|-------------|-------------------------- |----------------|
|feel-time-function_ErrorCase_1|time(null)|null (null)|
|feel-time-function_ErrorCase_2|time(null,11,45,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_3|time(12,null,45,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_4|time(12,0,null,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_5|time(null,null,45,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_6|time(null,11,null,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_7|time(null,11,45,null)|null (null)|
|feel-time-function_ErrorCase_8|time(12,null,null,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_9|time(12,11,null,null)|null (null)|
|feel-time-function_ErrorCase_10|time(12,null,null,null)|null (null)|
|feel-time-function_ErrorCase_11|time(null,0,null,null)|null (null)|
|feel-time-function_ErrorCase_12|time(null,null,15,null)|null (null)|
|feel-time-function_ErrorCase_13|time(null,null,null,duration("P0D"))|null (null)|
|feel-time-function_ErrorCase_14|time(null,null,null,null)|null (null)|
|feel-time-function_15|time(12,00,00,null)|12:00:00 (time)|
|feel-time-function_ErrorCase_16|time()|null (null)|
|feel-time-function_17|time("01:02:03")|01:02:03 (time)|
|feel-time-function_18|time("00:00:00")|00:00:00 (time)|
|feel-time-function_19|time("11:22:33.444")|11:22:33.444 (time)|
|feel-time-function_20|time("11:22:33.123456789")|11:22:33.123456789 (time)|
|feel-time-function_21|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_22|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_23|time("11:00:00Z")|11:00:00Z (time)|
|feel-time-function_24|time("00:00:00Z")|00:00:00Z (time)|
|feel-time-function_25|time("13:20:00+02:00")|13:20:00+02:00 (time)|
|feel-time-function_26|time("13:20:00-05:00")|13:20:00-05:00 (time)|
|feel-time-function_27|time("11:22:33-00:00")|11:22:33Z (time)|
|feel-time-function_28|time("11:22:33+00:00")|11:22:33Z (time)|
|feel-time-function_29|string(time("00:01:00@Etc/UTC"))|"00:01:00@Etc/UTC" (string)|
|feel-time-function_30|string(time("00:01:00@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_31|time(date and time("2017-08-10T10:20:00"))|10:20:00 (time)|
|feel-time-function_32|time(date and time("2017-08-10T10:20:00+00:00"))|10:20:00Z (time)|
|feel-time-function_33|time(date and time("2017-08-10T10:20:00-00:00"))|10:20:00Z (time)|
|feel-time-function_34|time(date and time("2017-08-10T10:20:00+01:00"))|10:20:00+01:00 (time)|
|feel-time-function_35|time(date and time("2017-08-10T10:20:00-01:00"))|10:20:00-01:00 (time)|
|feel-time-function_36|time(date and time("2017-08-10T10:20:00Z"))|10:20:00Z (time)|
|feel-time-function_37|string(time(date and time("2017-08-10T10:20:00@Europe/Paris")))|"10:20:00@Europe/Paris" (string)|
|feel-time-function_38|string(time(date and time("2017-09-04T11:20:00@Asia/Dhaka")))|"11:20:00@Asia/Dhaka" (string)|
|feel-time-function_39|time(11, 59, 45, null)|11:59:45 (time)|
|feel-time-function_40|time(11, 59, 45, duration("PT0H"))|11:59:45Z (time)|
|feel-time-function_41|time(11, 59, 45, duration("PT2H"))|11:59:45+02:00 (time)|
|feel-time-function_42|time(11, 59, 45, duration("-PT2H"))|11:59:45-02:00 (time)|
|feel-time-function_43|time(11, 59, 00, duration("PT2H1M"))|11:59:00+02:01 (time)|
|feel-time-function_44|time(11, 59, 00, duration("-PT2H1M"))|11:59:00-02:01 (time)|
|feel-time-function_45|time(11, 59, 00, duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_46|time(11, 59, 00, duration("-PT2H1M0S"))|11:59:00-02:01 (time)|
|feel-time-function_47|string(time(11, 59, 45, duration("PT2H45M55S")))|"11:59:45+02:45:55" (string)|
|feel-time-function_48|string(time(11, 59, 45, duration("-PT2H45M55S")))|"11:59:45-02:45:55" (string)|
|feel-time-function_49|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_50|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_51|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01")))|23:59:01 (time)|
|feel-time-function_52|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01.987654321")))|23:59:01.987654321 (time)|
|feel-time-function_53|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30+02:00")))|09:15:30+02:00 (time)|
|feel-time-function_54|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30Z")))|09:15:30Z (time)|
|feel-time-function_ErrorCase_55|time(2017)|null (null)|
|feel-time-function_ErrorCase_56|time([])|null (null)|
|feel-time-function_ErrorCase_57|time("")|null (null)|
|feel-time-function_ErrorCase_58|time(date("2017-08-10"))|null (null)|
|feel-time-function_ErrorCase_59|time("23:59:60")|null (null)|
|feel-time-function_ErrorCase_60|time("24:00:01")|null (null)|
|feel-time-function_ErrorCase_61|time("24:01:00")|null (null)|
|feel-time-function_ErrorCase_62|time("25:00:00")|null (null)|
|feel-time-function_ErrorCase_63|time("00:60:00")|null (null)|
|feel-time-function_ErrorCase_64|time("00:00:61")|null (null)|
|feel-time-function_ErrorCase_65|time("7:00:00")|null (null)|
|feel-time-function_ErrorCase_66|time("07:1:00")|null (null)|
|feel-time-function_ErrorCase_67|time("07:01:2")|null (null)|
|feel-time-function_ErrorCase_68|time("13:20:00@xyz/abc")|null (null)|
|feel-time-function_ErrorCase_69|time("13:20:00+19:00")|null (null)|
|feel-time-function_ErrorCase_70|time("13:20:00-19:00")|null (null)|
|feel-time-function_ErrorCase_71|time("13:20:00+5:00")|null (null)|
|feel-time-function_ErrorCase_72|time("13:20:00+5:00")|null (null)|
|feel-time-function_ErrorCase_73|time("13:20:00+5")|null (null)|
|feel-time-function_ErrorCase_74|time("13:20:00+02:00@Europe/Paris")|null (null)|
|feel-time-function_ErrorCase_75|time("7:20")|null (null)|
|feel-time-function_ErrorCase_76|time("07:2")|null (null)|
|feel-time-function_ErrorCase_77|time("11:30:00T")|null (null)|
|feel-time-function_ErrorCase_78|time("2017-08-10")|null (null)|
|feel-time-function_ErrorCase_79|time("2012T-12-2511:00:00Z")|null (null)|
|feel-time-function_ErrorCase_80|time(24, 59, 45, null)|null (null)|
|feel-time-function_ErrorCase_81|time(-24, 59, 45, null)|null (null)|
|feel-time-function_ErrorCase_82|time(23, 60, 45, null)|null (null)|
|feel-time-function_ErrorCase_83|time(23, 59, 60, null)|null (null)|
|feel-time-function_ErrorCase_84|time("2017-08-10T10:20:00")|null (null)|
|feel-time-function_ErrorCase_85|time("2017-08-10")|null (null)|
|feel-time-function_86|time(from:date and time("2012-12-24T23:59:00"))|23:59:00 (time)|
|feel-time-function_87|time(from: "12:45:00")|12:45:00 (time)|
|feel-time-function_88|time(hour:11, minute:59, second:0, offset: duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_89|time(hour:11, minute:59, second:0, offset: duration("-PT2H"))|11:59:00-02:00 (time)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1116-feel-time-function.dmn](./1116-feel-time-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  