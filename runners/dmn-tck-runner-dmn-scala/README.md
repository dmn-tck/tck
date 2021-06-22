# DMN TCK DMN-Scala runner

Runs the DMN TCK test suite for the [DMN-Scala](https://github.com/camunda-community-hub/dmn-scala) engine.

## How to build and execute this runner

From this directory (`dmn-tck-runner-dmn-scala/`) with Maven:

```
$ mvn clean install
```

Alternatively, from the parent project directory, with Maven:

```
$ mvn clean install -Pdmn-scala
```

For the full list of running options, please reference the [general DMN TCK runners manual](https://github.com/dmn-tck/tck/tree/master/runners#how-to-buildexecute-test-for-a-vendors-engine).
