1116-feel-time-function
--------------------

### Description ###

DMN Model [1116-feel-time-function.dmn](./1116-feel-time-function.dmn) tests DMN specification conformance of `FEEL built-in function 'time(from [string])', 'time(from [time, date and time])' and 'time(hour,minute,second,offset)'`.

#### Specification Reference(s): ####
 * DMN 1.1 - 10.3.4.1 Table 58

### Test Cases ###

|Decision Name| Literal Expression (FEEL) | Expected Result|
|-------------|-------------------------- |----------------|
|feel-time-function_ErrorCase_1|time(null)|feel-null (null)|
|feel-time-function_ErrorCase_2|time(null,11,45,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_3|time(12,null,45,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_4|time(12,0,null,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_5|time(null,null,45,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_6|time(null,11,null,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_7|time(null,11,45,null)|feel-null (null)|
|feel-time-function_ErrorCase_8|time(12,null,null,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_9|time(12,11,null,null)|feel-null (null)|
|feel-time-function_ErrorCase_10|time(12,null,null,null)|feel-null (null)|
|feel-time-function_ErrorCase_11|time(null,0,null,null)|feel-null (null)|
|feel-time-function_ErrorCase_12|time(null,null,15,null)|feel-null (null)|
|feel-time-function_ErrorCase_13|time(null,null,null,duration("P0D"))|feel-null (null)|
|feel-time-function_ErrorCase_14|time(null,null,null,null)|feel-null (null)|
|feel-time-function_15|time(12,00,00,null)|12:00:00 (time)|
|feel-time-function_ErrorCase_16|time()|feel-null (null)|
|feel-time-function_17|time("T23:59:00")|23:59:00 (time)|
|feel-time-function_18|time("01:02:03")|01:02:03 (time)|
|feel-time-function_19|time("01:02")|01:02:00 (time)|
|feel-time-function_20|time("00:00:00")|00:00:00 (time)|
|feel-time-function_21|time("00:00")|00:00:00 (time)|
|feel-time-function_22|time("24:00:00")|00:00:00 (time)|
|feel-time-function_23|time("11:22:33.444")|11:22:33.444 (time)|
|feel-time-function_24|time("11:22:33,444")|11:22:33.444 (time)|
|feel-time-function_25|time("11:22:33.123456789")|11:22:33.123456789 (time)|
|feel-time-function_26|time("11:22:33,123456789")|11:22:33.123456789 (time)|
|feel-time-function_27|time("T23:59:00z")|23:59:00Z (time)|
|feel-time-function_28|time("T23:59:00Z")|23:59:00Z (time)|
|feel-time-function_29|time("23:59:00z")|23:59:00Z (time)|
|feel-time-function_30|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_31|time("23:59:00Z")|23:59:00Z (time)|
|feel-time-function_32|time("11:00:00Z")|11:00:00Z (time)|
|feel-time-function_33|time("00:00:00Z")|00:00:00Z (time)|
|feel-time-function_34|time("13:20:00+02:00")|13:20:00+02:00 (time)|
|feel-time-function_35|time("13:20:00-05:00")|13:20:00-05:00 (time)|
|feel-time-function_36|time("T13:20:00+02:00")|13:20:00+02:00 (time)|
|feel-time-function_37|time("T13:20:00-05:00")|13:20:00-05:00 (time)|
|feel-time-function_38|time("11:22:33-00:00")|11:22:33Z (time)|
|feel-time-function_39|time("11:22:33+00:00")|11:22:33Z (time)|
|feel-time-function_40|time("13:20:00+02")|13:20:00+02:00 (time)|
|feel-time-function_41|time("13:20:00-05")|13:20:00-05:00 (time)|
|feel-time-function_42|time("T13:20:00+02")|13:20:00+02:00 (time)|
|feel-time-function_43|time("T13:20:00-05")|13:20:00-05:00 (time)|
|feel-time-function_44|time("11:22:33-00")|11:22:33Z (time)|
|feel-time-function_45|time("11:22:33+00")|11:22:33Z (time)|
|feel-time-function_46|time("01:02Z")|01:02:00Z (time)|
|feel-time-function_47|time("01:02z")|01:02:00Z (time)|
|feel-time-function_48|time("00:00Z")|00:00:00Z (time)|
|feel-time-function_49|time("13:20+02:00")|13:20:00+02:00 (time)|
|feel-time-function_50|string(time("00:01:00@Etc/UTC"))|"00:01:00@Etc/UTC" (string)|
|feel-time-function_51|string(time("00:01:00@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_52|string(time("00:01@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_53|string(time("T00:01:00@Etc/UTC"))|"00:01:00@Etc/UTC" (string)|
|feel-time-function_54|string(time("T00:01:00@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_55|string(time("T00:01@Europe/Paris"))|"00:01:00@Europe/Paris" (string)|
|feel-time-function_56|time(date and time("2017-08-10T10:20:00"))|10:20:00 (time)|
|feel-time-function_57|time(date and time("2017-08-10T10:20:00+00:00"))|10:20:00Z (time)|
|feel-time-function_58|time(date and time("2017-08-10T10:20:00-00:00"))|10:20:00Z (time)|
|feel-time-function_59|time(date and time("2017-08-10T10:20:00+01:00"))|10:20:00+01:00 (time)|
|feel-time-function_60|time(date and time("2017-08-10T10:20:00-01:00"))|10:20:00-01:00 (time)|
|feel-time-function_61|time(date and time("2017-08-10T10:20:00z"))|10:20:00Z (time)|
|feel-time-function_62|time(date and time("2017-08-10T10:20:00Z"))|10:20:00Z (time)|
|feel-time-function_63|string(time(date and time("2017-08-10T10:20:00@Europe/Paris")))|"10:20:00@Europe/Paris" (string)|
|feel-time-function_64|string(time(date and time("2017-09-04T11:20:00@Asia/Dhaka")))|"11:20:00@Asia/Dhaka" (string)|
|feel-time-function_65|time(11, 59, 45, null)|11:59:45 (time)|
|feel-time-function_66|time(11, 59, 45, duration("PT0H"))|11:59:45Z (time)|
|feel-time-function_67|time(11, 59, 45, duration("PT2H"))|11:59:45+02:00 (time)|
|feel-time-function_68|time(11, 59, 45, duration("-PT2H"))|11:59:45-02:00 (time)|
|feel-time-function_69|time(11, 59, 00, duration("PT2H1M"))|11:59:00+02:01 (time)|
|feel-time-function_70|time(11, 59, 00, duration("-PT2H1M"))|11:59:00-02:01 (time)|
|feel-time-function_71|time(11, 59, 00, duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_72|time(11, 59, 00, duration("-PT2H1M0S"))|11:59:00-02:01 (time)|
|feel-time-function_73|string(time(11, 59, 45, duration("PT2H45M55S")))|"11:59:45+02:45:55" (string)|
|feel-time-function_74|string(time(11, 59, 45, duration("-PT2H45M55S")))|"11:59:45-02:45:55" (string)|
|feel-time-function_75|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_76|time(11, 59, 45, duration("-PT0H"))|11:59:45Z (time)|
|feel-time-function_77|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01")))|23:59:01 (time)|
|feel-time-function_78|time(date and time(date and time("2017-08-10T10:20:00"),time("23:59:01.987654321")))|23:59:01.987654321 (time)|
|feel-time-function_79|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30+02:00")))|09:15:30+02:00 (time)|
|feel-time-function_80|time(date and time(date and time("2017-09-05T10:20:00"),time("09:15:30Z")))|09:15:30Z (time)|
|feel-time-function_ErrorCase_81|time(2017)|feel-null (null)|
|feel-time-function_ErrorCase_82|time([])|feel-null (null)|
|feel-time-function_ErrorCase_83|time("")|feel-null (null)|
|feel-time-function_ErrorCase_84|time(date("2017-08-10"))|feel-null (null)|
|feel-time-function_ErrorCase_85|time("23:59:60")|feel-null (null)|
|feel-time-function_ErrorCase_86|time("24:00:01")|feel-null (null)|
|feel-time-function_ErrorCase_87|time("24:01:00")|feel-null (null)|
|feel-time-function_ErrorCase_88|time("25:00:00")|feel-null (null)|
|feel-time-function_ErrorCase_89|time("00:60:00")|feel-null (null)|
|feel-time-function_ErrorCase_90|time("00:00:61")|feel-null (null)|
|feel-time-function_ErrorCase_91|time("7:00:00")|feel-null (null)|
|feel-time-function_ErrorCase_92|time("07:1:00")|feel-null (null)|
|feel-time-function_ErrorCase_93|time("07:01:2")|feel-null (null)|
|feel-time-function_ErrorCase_94|time("13:20:00@xyz/abc")|feel-null (null)|
|feel-time-function_ErrorCase_95|time("13:20:00+19:00")|feel-null (null)|
|feel-time-function_ErrorCase_96|time("13:20:00-19:00")|feel-null (null)|
|feel-time-function_ErrorCase_97|time("13:20:00+5:00")|feel-null (null)|
|feel-time-function_ErrorCase_98|time("13:20:00+5:00")|feel-null (null)|
|feel-time-function_ErrorCase_99|time("13:20:00+5")|feel-null (null)|
|feel-time-function_ErrorCase_100|time("13:20:00+02:00@Europe/Paris")|feel-null (null)|
|feel-time-function_ErrorCase_101|time("7:20")|feel-null (null)|
|feel-time-function_ErrorCase_102|time("07:2")|feel-null (null)|
|feel-time-function_ErrorCase_103|time("11:30:00T")|feel-null (null)|
|feel-time-function_ErrorCase_104|time("2017-08-10")|feel-null (null)|
|feel-time-function_ErrorCase_105|time("2012T-12-2511:00:00Z")|feel-null (null)|
|feel-time-function_ErrorCase_106|time(24, 59, 45, null)|feel-null (null)|
|feel-time-function_ErrorCase_107|time(-24, 59, 45, null)|feel-null (null)|
|feel-time-function_ErrorCase_108|time(23, 60, 45, null)|feel-null (null)|
|feel-time-function_ErrorCase_109|time(23, 59, 60, null)|feel-null (null)|
|feel-time-function_ErrorCase_110|time("2017-08-10T10:20:00")|feel-null (null)|
|feel-time-function_ErrorCase_111|time("2017-08-10")|feel-null (null)|
|feel-time-function_112|time(from:date and time("2012-12-24T23:59:00"))|23:59:00 (time)|
|feel-time-function_113|time(from: "12:45:00")|12:45:00 (time)|
|feel-time-function_114|time(hour:11, minute:59, second:0, offset: duration("PT2H1M0S"))|11:59:00+02:01 (time)|
|feel-time-function_115|time(hour:11, minute:59, second:0, offset: duration("-PT2H"))|11:59:00-02:00 (time)|

         

### Disclaimer ###
This page is a simple view for the underlying DMN model file [1116-feel-time-function.dmn](./1116-feel-time-function.dmn).
The purpose of the model is to test and validate FEEL expressions. Therefore the underlying DMN model is simplistic:
Each decision node contains one literal expression under test. The table above shows the decision, the underlying FEEL expression and the expected result.

Site generated by [ACTICO GmbH](https://actico.com) for [Technology Compatibility Kit (TCK)](https://dmn-tck.github.io/tck/) for the Decision Model and Notation (DMN) standard.

[DMN 1.1. Specification Document](http://www.omg.org/spec/DMN/1.1/) 
  