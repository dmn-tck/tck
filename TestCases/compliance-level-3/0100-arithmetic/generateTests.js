const fs = require('fs');

const TYPE_NUMBER = "number";
const TYPE_STRING = "string";
const TYPE_BOOLEAN = "boolean";
const TYPE_DATE = "date";
const TYPE_DATE_AND_TIME = "dateAndTime";
const TYPE_TIME = "time";
const TYPE_LIST = "list";
const TYPE_CONTEXT = "context";
const TYPE_RANGE = "range";
const TYPE_FUNCTION = "function";
const TYPE_DT_DURATION = "dtDuration";
const TYPE_YM_DURATION = "ymDuration";
const TYPE_NULL = "null";

const SCHEMA_TYPE_DECIMAL = "decimal";
const SCHEMA_TYPE_DURATION = "duration";
const SCHEMA_TYPE_DATE_TIME = "dateTime";
const SCHEMA_TYPE_DATE = "date";
const SCHEMA_TYPE_TIME = "time";
const SCHEMA_TYPE_NIL = "nil";
const SCHEMA_TYPE_STRING = "string";

const OP_SYMBOLS = {
    "multiply": "*",
    "divide": "/",
    "add": "+",
    "subtract": "-",
    "exponent": "**",
}

const OP_WORDS = {
    "multiply": "by",
    "divide": "by",
    "add": "to",
    "subtract": "minus",
    "exponent": "exp",
}

const TYPES = [
    TYPE_NUMBER,
    TYPE_STRING,
    TYPE_BOOLEAN,
    TYPE_DATE,
    TYPE_DATE_AND_TIME,
    TYPE_TIME,
    TYPE_LIST,
    TYPE_CONTEXT,
    TYPE_DT_DURATION,
    TYPE_YM_DURATION,
    TYPE_RANGE,
    TYPE_FUNCTION,
    TYPE_NULL
]

const SAMPLE_DATA = {
    [TYPE_NUMBER]: "10",
    [TYPE_STRING]: '"10"',
    [TYPE_BOOLEAN]: "true",
    [TYPE_DATE]: '@"2021-01-01"',
    [TYPE_DATE_AND_TIME]: '@"2021-01-01T10:10:10"',
    [TYPE_TIME]: '@"10:10:10"',
    [TYPE_LIST]: "[10]",
    [TYPE_CONTEXT]: "{a: 10}",
    [TYPE_DT_DURATION]: '@"P1D"',
    [TYPE_YM_DURATION]: '@"P1Y"',
    [TYPE_RANGE]: "[1..10]",
    [TYPE_FUNCTION]: "(function(a) a)",
    [TYPE_NULL]: "null"
}

const DMN_TYPES_FROM_SCHEMA = {
    [SCHEMA_TYPE_DECIMAL]: "number",
    [SCHEMA_TYPE_DURATION]: "duration",
    [SCHEMA_TYPE_DATE_TIME]: "date and time",
    [SCHEMA_TYPE_DATE]: "date",
    [SCHEMA_TYPE_TIME]: "time",
    [SCHEMA_TYPE_NIL]: "null",
    [SCHEMA_TYPE_STRING]: "string",
}

// Table 59: Specific semantics of multiplication and division
const multiply = {

    [TYPE_NUMBER]: {
        [TYPE_NUMBER]: [ // <number> * <number>
            // leftVal, rightVal, result, result XSD type, <optional> description
            [10, 10, 100, SCHEMA_TYPE_DECIMAL],
            [10, -10, -100, SCHEMA_TYPE_DECIMAL],
            [-10, -10, 100, SCHEMA_TYPE_DECIMAL],
            [10, 0, 0, SCHEMA_TYPE_DECIMAL]
        ],
        [TYPE_DT_DURATION]: [ // <number> * <days and time duration>
            [10, '@"P1D"', "P10D", SCHEMA_TYPE_DURATION],
            [10, '@"-P1D"', "-P10D", SCHEMA_TYPE_DURATION],
            [-10, '@"-P1D"', "P10D", SCHEMA_TYPE_DURATION],
            [24, '@"PT1H"', "P1D", SCHEMA_TYPE_DURATION],
            [26, '@"PT1H"', "P1DT2H", SCHEMA_TYPE_DURATION],
            [0, '@"P1D"', "P0D", SCHEMA_TYPE_DURATION],
            [1.5, '@"P4DT1H"', "P6DT1H30M", SCHEMA_TYPE_DURATION],
            [2.5, '@"PT23H"', "P2DT9H30M", SCHEMA_TYPE_DURATION]
        ],
        [TYPE_YM_DURATION]: [ // <number> * <years and months duration>
            [10, '@"P1Y"', "P10Y", SCHEMA_TYPE_DURATION],
            [10, '@"-P1Y"', "-P10Y", SCHEMA_TYPE_DURATION],
            [-10, '@"-P1Y"', "P10Y", SCHEMA_TYPE_DURATION],
            [12, '@"P1M"', "P1Y", SCHEMA_TYPE_DURATION],
            [24, '@"P1M"', "P2Y", SCHEMA_TYPE_DURATION],
            [26, '@"P1M"', "P2Y2M", SCHEMA_TYPE_DURATION],
            [0, '@"P1M"', "P0Y", SCHEMA_TYPE_DURATION],
            [1.5, '@"P2M"', "P3M", SCHEMA_TYPE_DURATION],
            [-1.5, '@"P2M"', "-P3M", SCHEMA_TYPE_DURATION],
            [-2.5, '@"P1Y11M"', "-P4Y9M", SCHEMA_TYPE_DURATION]
        ]
    },

    [TYPE_YM_DURATION]: {
        [TYPE_NUMBER]: [ // <years and months duration> * <number>. commutative.
            ['@"P1Y"', 10, "P10Y", SCHEMA_TYPE_DURATION],
            ['@"-P1Y"', 10, "-P10Y", SCHEMA_TYPE_DURATION]
        ],
    },

    [TYPE_DT_DURATION]: {
        [TYPE_NUMBER]: [ // <days and time duration> * <number>.  commutative.
            ['@"P1D"', 10, "P10D", SCHEMA_TYPE_DURATION],
            ['@"-P1D"', 10, "-P10D", SCHEMA_TYPE_DURATION]
        ]
    }
}

// Table 59: Specific semantics of multiplication and division
const divide = {

    [TYPE_NUMBER]: {
        [TYPE_NUMBER]: [ // <number> / <number>
            [100, 10, 10, SCHEMA_TYPE_DECIMAL],
            [100, 0, "null", SCHEMA_TYPE_NIL]
        ]
    },

    [TYPE_YM_DURATION]: {
        [TYPE_NUMBER]: [ // <years and months duration> / <number>
            ['@"P10Y"', 10, "P1Y", SCHEMA_TYPE_DURATION],
            ['@"P10Y"', 0, "null", SCHEMA_TYPE_NIL],
            ['@"P10Y11M"', 2.5, "P4Y4M", SCHEMA_TYPE_DURATION],
            ['@"P10Y11M"', -2.5, "-P4Y4M", SCHEMA_TYPE_DURATION]
        ],
        [TYPE_YM_DURATION]: [ // <years and months duration> / <years and months duration>
            ['@"P10Y"', '@"P5Y"', 2, SCHEMA_TYPE_DECIMAL],
            ['@"P10D"', '@"P0Y"', "null", SCHEMA_TYPE_NIL],
        ]
    },

    [TYPE_DT_DURATION]: {
        [TYPE_NUMBER]: [ // <days and time duration> /  <number>
            ['@"P10D"', 10, "P1D", SCHEMA_TYPE_DURATION],
            ['@"P10D"', 0, "null", SCHEMA_TYPE_NIL],
            ['@"P10DT23H"', 2.5, "P4DT9H12M", SCHEMA_TYPE_DURATION]
        ],
        [TYPE_DT_DURATION]: [ // <days and time duration> /  <days and time duration>
            ['@"P10D"', '@"P5D"', 2, SCHEMA_TYPE_DECIMAL],
            ['@"P10D"', '@"P0D"', "null", SCHEMA_TYPE_NIL]
        ]
    }
}

// Table 57: Specific semantics of addition and subtraction
const add = {

    [TYPE_NUMBER]: {
        [TYPE_NUMBER]: [ // <number> + <number>
            [10, 5, 15, SCHEMA_TYPE_DECIMAL],
            [10, -5, 5, SCHEMA_TYPE_DECIMAL],
            [-10, 5, -5, SCHEMA_TYPE_DECIMAL],
            [-10, -5, -15, SCHEMA_TYPE_DECIMAL],
            [1.2345, 2.234, 3.4685, SCHEMA_TYPE_DECIMAL],
        ]
    },

    [TYPE_DATE_AND_TIME]: {
        [TYPE_DATE_AND_TIME]: [ // <date and time> + <date and time>
            // addition is undefined
        ],
        [TYPE_DATE]: [ // <date and time> + <date>
            // addition is undefined.
        ],
        [TYPE_YM_DURATION]: [ // <date and time> + <years and months duration>
            ['@"2021-01-01T10:10:10"', '@"P1Y"', "2022-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P1M"', "2021-02-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-P1Y"', "2020-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-P1M"', "2020-12-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P0Y"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P0M"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"P1M"', "2021-02-01T10:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"-P1M"', "2020-12-01T10:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10@Australia/Melbourne"', '@"P1M"', "2021-02-01T10:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ],
        [TYPE_DT_DURATION]: [ // <date and time> + <days and time duration >
            ['@"2021-01-12T10:10:10"', '@"P1DT1H"', "2021-01-13T11:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-12T10:10:10+11:00"', '@"P1DT1H"', "2021-01-13T11:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P1D"', "2021-01-02T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"PT1H"', "2021-01-01T11:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-P1D"', "2020-12-31T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-PT1H"', "2021-01-01T09:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P0D"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"PT0H"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"PT1H"', "2021-01-01T11:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"-PT1H"', "2021-01-01T09:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-12T10:10:10@Australia/Melbourne"', '@"P1DT1H"', "2021-01-13T11:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ]
    },

    [TYPE_DATE]: {
        [TYPE_DATE_AND_TIME]: [ // <date> + <date and time>
            // addition is undefined
        ],
        [TYPE_DATE]: [ // <date> + <date> (
            // addition is undefined
        ],
        [TYPE_YM_DURATION]: [ // <date> + <years and month duration>.  Commutative.
            ['@"P1Y"', '@"2021-01-01"', "2022-01-01", SCHEMA_TYPE_DATE],
            ['@"P1M"', '@"2021-01-01"', "2021-02-01", SCHEMA_TYPE_DATE],
        ],
        [TYPE_DT_DURATION]: [ // <date> + <days and time duration>.  Commutative.
            ['@"2021-01-01"', '@"P1D"', "2021-01-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-01"', '@"PT36H"', "2021-01-02", SCHEMA_TYPE_DATE, "Date + 1.5 days (in hours) gives date + 1 day"],
            ['@"2021-01-01"', '@"PT48H"', "2021-01-03", SCHEMA_TYPE_DATE, "Date + 2 days (in hours) gives date + 2 days"],
        ]
    },

    [TYPE_TIME]: {
        [TYPE_TIME]: [ // <time> + <time>
            // addition is undefined
        ],
        [TYPE_DT_DURATION]: [ // <time> + <days and time duration>.  Commutative.
            ['@"10:15:00"', '@"PT1H"', "11:15:00", SCHEMA_TYPE_TIME],
            ['@"10:15:00"', '@"P1D"', "10:15:00", SCHEMA_TYPE_TIME, "Time + days duration gives time"],
            ['@"10:15:00+11:00"', '@"P1D"', "10:15:00+11:00", SCHEMA_TYPE_TIME],
            ['@"10:15:00@Australia/Melbourne"', '@"P1D"', "10:15:00@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ]
    },

    [TYPE_YM_DURATION]: {
        [TYPE_YM_DURATION]: [ // <years and month duration> + <years and month duration>
            ['@"P1Y"', '@"P2M"', "P1Y2M", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"-P2M"', "P10M", SCHEMA_TYPE_DURATION],
            ['@"-P1Y"', '@"P2M"', "-P10M", SCHEMA_TYPE_DURATION],
            ['@"-P1Y"', '@"-P2M"', "-P1Y2M", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"P0M"', "P1Y", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"-P0M"', "P1Y", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_DATE_AND_TIME]: [ // <years and months duration> + <days and time duration>
            ['@"P1Y"', '@"2021-01-01T10:10:10"', "2022-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"P1M"', '@"2021-01-01T10:10:10"', "2021-02-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"-P1Y"', '@"2021-01-01T10:10:10"', "2020-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"-P1M"', '@"2021-01-01T10:10:10"', "2020-12-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"P0Y"', '@"2021-01-01T10:10:10"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"P0M"', '@"2021-01-01T10:10:10"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"P1M"', '@"2021-01-01T10:10:10+11:00"', "2021-02-01T10:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"P1M"', '@"2021-01-01T10:10:10@Australia/Melbourne"', "2021-02-01T10:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ],
        [TYPE_DATE]: [ // <years and months duration> + <date>.  commutative.
            ['@"2021-01-01"', '@"P1Y"', "2022-01-01", SCHEMA_TYPE_DATE],
        ]
    },

    [TYPE_DT_DURATION]: {
        [TYPE_DT_DURATION]: [ // <days and time duration> + <days and time duration>
            ['@"P1D"', '@"P2D"', "P3D", SCHEMA_TYPE_DURATION],
            ['@"PT24H"', '@"P1D"', "P2D", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_DATE_AND_TIME]: [ // <days and time duration> + <date time>. commutative
            ['@"P1DT1H"', '@"2021-01-12T10:10:10"', "2021-01-13T11:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"P1DT1H"', '@"2021-01-12T10:10:10+11:00"', "2021-01-13T11:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"P1DT1H"', '@"2021-01-12T10:10:10@Australia/Melbourne"', "2021-01-13T11:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ],
        [TYPE_TIME]: [ // <days and time duration> + <time>.  commutative.
            ['@"PT1H"', '@"10:15:00"', "11:15:00", SCHEMA_TYPE_TIME],
            ['@"P1D"', '@"10:15:00"', "10:15:00", SCHEMA_TYPE_TIME],
            ['@"P1D"', '@"10:15:00+11:00"', "10:15:00+11:00", SCHEMA_TYPE_TIME],
            ['@"P1D"', '@"10:15:00@Australia/Melbourne"', "10:15:00@Australia/Melbourne", SCHEMA_TYPE_STRING],
            ['@"-P1D"', '@"10:15:00@Australia/Melbourne"', "10:15:00@Australia/Melbourne", SCHEMA_TYPE_STRING],
            ['@"PT1H"', '@"10:15:00@Australia/Melbourne"', "11:15:00@Australia/Melbourne", SCHEMA_TYPE_STRING],
            ['@"-PT1H"', '@"10:15:00@Australia/Melbourne"', "09:15:00@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ],
        [TYPE_DATE]: [ // <days and time duration> + <date>\
            ['@"P1D"', '@"2021-01-01"', "2021-01-02", SCHEMA_TYPE_DATE],
        ]
    },

    [TYPE_STRING]: {
        [TYPE_STRING]: [ // <string> + <string>
            ['"foo"','"bar"', "foobar", SCHEMA_TYPE_STRING],
            ['"1"', '"1"', "11", SCHEMA_TYPE_STRING],
        ]
    }
}

// Table 57: Specific semantics of addition and subtraction
const subtract = {

    [TYPE_NUMBER]: {
        [TYPE_NUMBER]: [ // <number> - <number>
            [10, 5, 5, SCHEMA_TYPE_DECIMAL],
            [10, -5, 15, SCHEMA_TYPE_DECIMAL],
            [-10, 5, -15, SCHEMA_TYPE_DECIMAL],
            [-10, -5, -5, SCHEMA_TYPE_DECIMAL],
            [1.1234, 0.2345, 0.8889, SCHEMA_TYPE_DECIMAL],
            [1.1234, -0.2345, 1.3579, SCHEMA_TYPE_DECIMAL],
            [-1.1234, -0.2345, -0.8889, SCHEMA_TYPE_DECIMAL],
        ]
    },

    [TYPE_DATE_AND_TIME]: {
        [TYPE_DATE_AND_TIME]: [ // <date and time> - <date and time>
            ['@"2021-01-02T10:10:10"', '@"2021-01-01T10:10:10"', "P1D", SCHEMA_TYPE_DURATION],
            ['@"2021-01-02T10:10:10@Europe/Paris"', '@"2021-01-01T10:10:10"', "null", SCHEMA_TYPE_NIL, "Both or neither values must have zone info"],
            ['@"2021-01-02T10:10:10"', '@"2021-01-01T10:10:10@Europe/Paris"', "null", SCHEMA_TYPE_NIL, "Both or neither values must have zone info"],
            ['@"2021-01-02T10:10:10+02:00"', '@"2021-01-01T10:10:10"', "null", SCHEMA_TYPE_NIL, "Both or neither values must have zone info"],
            ['@"2021-01-02T10:10:10"', '@"2021-01-01T10:10:10+02:00"', "null", SCHEMA_TYPE_NIL, "Both or neither values must have zone info"],
            ['@"2021-01-02T10:10:10@Europe/Paris"', '@"2021-01-01T10:10:10@Europe/Paris"', "P1D", SCHEMA_TYPE_DURATION],
            ['@"2021-01-02T10:10:10@Europe/Paris"', '@"2021-01-01T10:10:10@Asia/Dhaka"', "P1DT5H", SCHEMA_TYPE_DURATION],
            ['@"2021-01-02T10:10:10+01:00"', '@"2021-01-01T10:10:10@Europe/Paris"', "P1D", SCHEMA_TYPE_DURATION, "Offset datetime is UTC and datetime has zone"],
            ['@"2021-01-02T10:10:10+01:00"', '@"2021-01-01T10:10:10"', "null", SCHEMA_TYPE_NIL, "Offset datetime is UTC and datetime has no zone"],
            ['@"2021-01-02T00:00:00Z"', '@"2021-01-02"', "P0D", SCHEMA_TYPE_DURATION, "Date is implicitly UTC 00:00:00"],
        ],
        [TYPE_YM_DURATION]: [ // <date and time> - <years and months duration>
            ['@"2021-01-01T10:10:10"', '@"P1Y"', "2020-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P1M"', "2020-12-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-P1Y"', "2022-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"-P1M"', "2021-02-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P0Y"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"P0M"', "2021-01-01T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"P1Y"', "2020-01-01T10:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10@Australia/Melbourne"', '@"P1Y"', "2020-01-01T10:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ],
        [TYPE_DATE]: [ // <date and time> - <date>
            ['@"2021-01-01T00:00:00"', '@"2021-01-02"', "null", SCHEMA_TYPE_NIL, "Both must have zone info and date implies UTC"],
            ['@"2021-01-02T10:10:10@Europe/Paris"', '@"2021-01-01"', "P1DT9H10M10S", SCHEMA_TYPE_DURATION, "datetime has zone and date implies UTC"],
            ['@"2021-01-02T10:10:10+01:00"', '@"2021-01-01"', "P1DT9H10M10S", SCHEMA_TYPE_DURATION, "Offset datetime is UTC and date implies UTC"],
        ],
        [TYPE_DT_DURATION]: [ // <date and time> - <days and time duration>
            ['@"2021-01-01T10:10:10"', '@"P1D"', "2020-12-31T10:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10"', '@"PT1H"', "2021-01-01T09:10:10", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-02T00:00:00"', '@"PT1H"', "2021-01-01T23:00:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10+11:00"', '@"P1D"', "2020-12-31T10:10:10+11:00", SCHEMA_TYPE_DATE_TIME],
            ['@"2021-01-01T10:10:10@Australia/Melbourne"', '@"P1D"', "2020-12-31T10:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ]
    },

    [TYPE_DATE]: {
        [TYPE_DATE_AND_TIME]: [ // <date> - <date and time> with date converted to datetime UTC 0H
            ['@"2021-01-02"', '@"2021-01-01T10:10:10"', "null", SCHEMA_TYPE_NIL], // ?
            ['@"2021-01-02"', '@"2021-01-01T10:10:10+11:00"', "P1DT49M50S", SCHEMA_TYPE_DURATION],
            ['@"2021-01-02"', '@"2021-01-01T10:10:10@Australia/Melbourne"', "P1DT49M50S", SCHEMA_TYPE_DURATION],
            ['@"2021-01-02"', '@"2021-01-02T00:00:00Z"', "P0D", SCHEMA_TYPE_DURATION, "Date is implicitly UTC 00:00:00"],
        ],
        [TYPE_DATE]: [ // <date > - <date> with date converted to datetime UTC 0H
            ['@"2021-01-02"', '@"2021-01-01"', "P1D", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_YM_DURATION]: [ // <date> - <years and months duration>
            ['@"2021-01-02"', '@"P1Y"', "2020-01-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"P1D"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"P1M"', "2020-12-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-P1Y"', "2022-01-02", SCHEMA_TYPE_DATE],
        ],
        [TYPE_DT_DURATION]: [ // <date> - <days and time duration>
            ['@"2021-01-02"', '@"P1D"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"PT1H"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"PT1M"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"PT1S"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-PT1H"', "2021-01-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-PT1M"', "2021-01-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-PT1S"', "2021-01-02", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"PT24H"', "2021-01-01", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"PT25H"', "2020-12-31", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-PT25H"', "2021-01-03", SCHEMA_TYPE_DATE],
            ['@"2021-01-02"', '@"-PT24H"', "2021-01-03", SCHEMA_TYPE_DATE],
            ['@"2020-03-01"', '@"PT24H"', "2020-02-29", SCHEMA_TYPE_DATE],
        ]
    },

    [TYPE_TIME]: {
        [TYPE_TIME]: [ // <time> - <time>
            ['@"10:10:10"', '@"09:10:10"', "PT1H", SCHEMA_TYPE_DURATION],
            ['@"10:10:10"', '@"11:10:10"', "-PT1H", SCHEMA_TYPE_DURATION],
            ['@"10:10:10+11:00"', '@"11:10:10+11:00"', "-PT1H", SCHEMA_TYPE_DURATION],
            ['@"10:10:10+11:00"', '@"09:10:10+11:00"', "PT1H", SCHEMA_TYPE_DURATION],
            ['@"10:10:10@Australia/Melbourne"', '@"11:10:10@Australia/Melbourne"', "-PT1H", SCHEMA_TYPE_DURATION],
            ['@"10:10:10@Australia/Melbourne"', '@"09:10:10@Australia/Melbourne"', "PT1H", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_DT_DURATION]: [ // <time> - <days and time duration>
            ['@"10:10:10"', '@"PT1H"', "09:10:10", SCHEMA_TYPE_TIME],
            ['@"10:10:10"', '@"P1D"', "10:10:10", SCHEMA_TYPE_TIME],
            ['@"10:10:10+11:00"', '@"P1D"', "10:10:10+11:00", SCHEMA_TYPE_TIME],
            ['@"10:10:10@Australia/Melbourne"', '@"P1D"', "10:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
            ['@"10:10:10+11:00"', '@"PT1H"', "09:10:10+11:00", SCHEMA_TYPE_TIME],
            ['@"10:10:10@Australia/Melbourne"', '@"PT1H"', "09:10:10@Australia/Melbourne", SCHEMA_TYPE_STRING],
        ]
    },

    [TYPE_YM_DURATION]: {
        [TYPE_YM_DURATION]: [ // <years and  months duration> - <years and  months duration>
            ['@"P1Y"', '@"P2M"', "P10M", SCHEMA_TYPE_DURATION],
            ['@"-P1Y"', '@"P2M"', "-P1Y2M", SCHEMA_TYPE_DURATION],
            ['@"-P1Y"', '@"-P2M"', "-P10M", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"-P2M"', "P1Y2M", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"P0M"', "P1Y", SCHEMA_TYPE_DURATION],
            ['@"P1Y"', '@"-P0M"', "P1Y", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_DATE_AND_TIME]: [ // <years and  months duration> - <date and time>
            // undefined
        ],
        [TYPE_DATE]: [ // <years and  months duration> - <date>
            // undefined
        ]
    },

    [TYPE_DT_DURATION]: {
        [TYPE_DT_DURATION]: [ // <days and time duration> - <days and time duration>
            ['@"P1D"', '@"PT2H"', "PT22H", SCHEMA_TYPE_DURATION],
            ['@"-P1D"', '@"PT2H"', "-P1DT2H", SCHEMA_TYPE_DURATION],
            ['@"-P1D"', '@"-PT2H"', "-PT22H", SCHEMA_TYPE_DURATION],
            ['@"P1D"', '@"-PT2H"', "P1DT2H", SCHEMA_TYPE_DURATION],
            ['@"P1D"', '@"PT0H"', "P1D", SCHEMA_TYPE_DURATION],
            ['@"P1D"', '@"-PT0H"', "P1D", SCHEMA_TYPE_DURATION],
        ],
        [TYPE_DATE_AND_TIME]: [ // <days and time duration> - <date and time>
            // undefined
        ],
        [TYPE_TIME]: [ // <days and time duration> - <time>
            // undefined
        ],
        [TYPE_DATE]: [ // <days and time duration> - <date>
            // undefined
        ]
    },

    [TYPE_STRING]: {
        [TYPE_STRING]: [ // <string> - <string>
            // undefined
        ]
    }
}

// Table 60: Semantics of exponentiation
const exponent = {
    [TYPE_NUMBER]: {
        [TYPE_NUMBER]: [ // <number> ** <number>
            [5, 2, 25, SCHEMA_TYPE_DECIMAL],
            [5, -2, 0.04, SCHEMA_TYPE_DECIMAL],
        ]
    },
}

const operations = {multiply, divide, add, subtract, exponent};

// create an object with every type as a key and an empty array as a value, like
// {number: [], string[], date: [] ... etc}.  Used for storing what RHS types are
// tested for each LHS type.
function createObjKeyedByTypeWithEmptyArraysAsValues() {
    return TYPES.reduce((accum, t) => {
        accum[t] = [];
        return accum;
    }, {});
}

// for each operation (like 'multiply') holds an object for every left side type that has
// been exercised with an associated array of types that have appeared on the right.
let operationMatrix = Object.keys(operations).reduce((o, k) => {
    o[k] = createObjKeyedByTypeWithEmptyArraysAsValues();
    return o
}, {});

function generateTest(name, description, testData) {

    let isError = testData[3] == SCHEMA_TYPE_NIL;

    let expected = isError ? `<value xsi:nil="true"></value>` : `<value xsi:type="xsd:${testData[3]}">${testData[2]}</value>`;

    return `
    <testCase id="${name}">
        ${description ? "<description>" + description + "</description>" : ""}
        <resultNode name="${name}" type="decision" ${isError ? 'errorResult="true"' : ""}>
            <expected>
                ${expected}
            </expected>
        </resultNode>
    </testCase>`;
}

function generateDecision(name, description, op, testData) {

    let testText = `${testData[0]} ${op} ${testData[1]}`;

    // if schema type is a string, then coerce to string
    if (testData[3] == SCHEMA_TYPE_STRING) {
        testText = `string(${testText})`;
    }

    return `
    <decision name="${name}" id="_${name}">
        ${description ? "<description>" + description + "</description>" : ""}
        <allowedAnswers>${testData[2]} (${DMN_TYPES_FROM_SCHEMA[testData[3]]})</allowedAnswers>
        <variable name="${name}"/>
        <literalExpression>
            <text>${testText}</text>
        </literalExpression>
    </decision>`;
}

function generateTestXml(tests) {
    return `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!-- Contributed to DMN TCK by StrayAlien -->
<testCases xmlns="http://www.omg.org/spec/DMN/20160719/testcase"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation=""
           xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <modelName>0100-arithmetic.dmn</modelName>
    ${tests.join('\n')}
    
</testCases>`;
}

function generateModel(decisions) {
    return `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<definitions namespace="http://www.montera.com.au/spec/DMN/0100-arithmetic"
     name="0100-arithmetic"
     id="_i9fboPUUEeesLuP4RHs4vA"
     xmlns="https://www.omg.org/spec/DMN/20191111/MODEL/"
     xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/"
     xmlns:dmndi="https://www.omg.org/spec/DMN/20191111/DMNDI/"
     xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <description>arithmetic</description>
    ${decisions.join('\n')}
    
</definitions>`;
}

function main() {

    let tests = [];
    let decisions = [];
    let nullTestCount = 0;
    let testCount = 0;


    // a bit loopy here, but we end up going down each test set, like multiply, add, etc
    // and for each LHS type and associated RHS type, generate test and decision XML
    // from a template.  We note which LHS / RHS type combinations are encountered and
    // we'll later that data to help create the null tests for combinations _not_ encountered.

    Object.entries(operations).forEach(([op, lhsTests]) => {

        Object.entries(lhsTests).forEach(([lhsType, rhsTypes]) => {

            Object.entries(rhsTypes).forEach(([rhsType, testsArray]) => {

                testsArray.forEach((test, i) => {

                    // translate (say) 'multiply' into '*'
                    const opSymbol = OP_SYMBOLS[op];
                    const opWord = OP_WORDS[op];

                    // an index number for the test name padded with zeroes
                    const idxStr = (++i).toString().padStart(3, '0');
                    const name = `${op}_lhs_${lhsType}_${opWord}_rhs_${rhsType}_${idxStr}`;

                    const description = test.length == 5 ? test[4] : name;

                    // create the test and associated decision XML from template
                    tests.push(generateTest(name, description, test));
                    decisions.push(generateDecision(name, description, opSymbol, test));

                    // note the combination of lhs/rhs types
                    let matrixEntry = operationMatrix[op][lhsType];
                    if (matrixEntry.indexOf(rhsType) == -1) {
                        matrixEntry.push(rhsType);
                    }

                    testCount++;
                })
            })
        })
    })

    // after all positive tests have been generated, generate tests to assert nulls results
    // this is a test for every type combination not covered by positive tests.

    Object.entries(operationMatrix).forEach(([op, lhsTypes]) => {

        Object.entries(lhsTypes).forEach(([lhsType, rhsTypes]) => {

            TYPES.forEach((rhsType) => {

                if (!rhsTypes.includes(rhsType)) {
                    const opSymbol = OP_SYMBOLS[op];
                    const opWord = OP_WORDS[op];
                    const name = `error_when_${op}_lhs_${lhsType}_${opWord}_rhs_${rhsType}`;
                    const testData = [SAMPLE_DATA[lhsType], SAMPLE_DATA[rhsType], "null", SCHEMA_TYPE_NIL];
                    const description = "invalid arithmetic combination";
                    tests.push(generateTest(name, description, testData));
                    decisions.push(generateDecision(name, description, opSymbol, testData ));
                    nullTestCount++;
                }
            })
        })
    })

    console.log("Explicit combination tests: " + testCount);
    console.log("Implicit error tests: " + nullTestCount);
    console.log("Total tests: " + (nullTestCount + testCount));

    fs.writeFileSync("0100-arithmetic-test-01.xml", generateTestXml(tests));
    fs.writeFileSync("0100-arithmetic.dmn", generateModel(decisions));
}

main();
