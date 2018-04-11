# DMN TCK Drools runner

This is a Drools runner implementation for the DMN TCK suite of tests, to automate the TCK DMN tests execution for the [Drools](https://drools.org) engine.

## How to build and execute this runner

From this directory (`dmn-tck-runner-drools/`) with Maven:

```
$ mvn clean install
```

Alternatively, from the parent project directory, with Maven:

```
$ mvn clean install -Pdrools
```

For the full list of running options, please reference the [general DMN TCK runners manual](https://github.com/dmn-tck/tck/tree/master/runners#how-to-buildexecute-test-for-a-vendors-engine).

## Details on the DMN TCK Drools runner

This runner configuration is meant to run normally out-of-the-box.

This DMN TCK Drools runner reference a published Drools version as governed in this `pom.xml`.

The runner leverages the general DMN TCK jUnit runners architecture, to automate the execution of the TCK DMN tests on the referenced Drools version of the [Drools](https://drools.org) engine.
