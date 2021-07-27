# DMN TCK DMN-Scala runner

[DMN-Scala](https://github.com/camunda-community-hub/dmn-scala) is an open source DMN engine that is
build as a community extension for the Camunda Platform. It is based on Camunda's FEEL engine.

This repository contains the DMN TCK runner for the DMN engine.

## How to build and execute this runner

From this directory (`dmn-tck-runner-camunda-dmn-scala/`) with Maven:

```
$ mvn clean install
```

Alternatively, from the parent project directory, with Maven:

```
$ mvn clean install -Pdmn-scala
```

For the full list of running options, please reference
the [general DMN TCK runners manual](https://github.com/dmn-tck/tck/tree/master/runners#how-to-buildexecute-test-for-a-vendors-engine)
.
