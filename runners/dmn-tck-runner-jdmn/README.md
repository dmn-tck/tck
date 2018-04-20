# DMN TCK jDMN runner

This is a jDMN runner implementation for the DMN TCK suite of tests, to automate the TCK DMN tests execution for the [jDMN](https://github.com/goldmansachs/jdmn) engine.

## How to build and execute this runner

From this directory (`dmn-tck-runner-jdmn/`) with Maven:

```
$ mvn clean install
```

Alternatively, from the parent project directory, with Maven:

```
$ mvn clean install -Pjdmn
```

For the full list of running options, please reference the [general DMN TCK runners manual](https://github.com/dmn-tck/tck/tree/master/runners#how-to-buildexecute-test-for-a-vendors-engine).

## Details on the DMN TCK jDMN runner

This runner configuration is meant to run normally out-of-the-box.

This DMN TCK jDMN runner reference a published jDMN version as governed in this `pom.xml`.

The runner leverages the general DMN TCK jUnit runners architecture, to automate the execution of the TCK DMN tests on the referenced jDMN version of the [jDMN](https://github.com/goldmansachs/jdmn) engine.
