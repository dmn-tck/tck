# DMN TCK Camunda runner

This is a Camunda runner implementation for the DMN TCK suite of tests, to automate the TCK DMN tests execution for the [Camunda](https://camunda.org) engine.

## How to build and execute this runner

From this directory (`dmn-tck-runner-camunda/`) with Maven:

```
$ mvn clean install
```

Alternatively, from the parent project directory, with Maven:

```
$ mvn clean install -Pcamunda
```

For the full list of running options, please reference the [general DMN TCK runners manual](https://github.com/dmn-tck/tck/tree/master/runners#how-to-buildexecute-test-for-a-vendors-engine).

## Details on the DMN TCK Camunda runner

This runner configuration is meant to run normally out-of-the-box.

This DMN TCK Camunda runner reference a published Camunda version as governed in this `pom.xml`.

The runner leverages the general DMN TCK jUnit runners architecture, to automate the execution of the TCK DMN tests on the referenced Camunda version of the [Camunda](https://camunda.org) engine.
