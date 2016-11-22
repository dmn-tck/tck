# DMN TCK jUnit runner

This is a jUnit runner implementation to help vendors automate test execution for their products, in case they wish to use jUnit.

## How to build the runner

Just use maven to build the marshaller and runner jars:

```
$ mvn clean install
```

## How to build/execute test for a vendor's engine

Just build with maven enabling the corresponding vendor plugin. For instance, to execute tests for drools, use the following command line:

```
$ mvn clean install -P drools
```

Alternatively, if you would like to permanently activate a given profile in your machine, just create an empty file named `enable-<VENDOR>.flag`. 
Eg, to always activate the Drools profile, create a file named `enable-drools.flag`. On linux or Mac, you can use the following command line:

```
$ touch enable-drools.flag
```

## How to add your own vendor's runner
 
Easiest way is to copy&paste an existing implementation and update it to run your engine. Follow naming conventions when naming your module and
folder structure. E.g.:

```
$ cp -R dmn-tck-runner-drools dmn-tck-runner-myEngine
```

Then, add a profile into the `pom.xml` file for your module:

```xml
    <profile>
      <id>myEngine</id>
      <activation>
        <file>
          <exists>enable-myEngine.flag</exists>
        </file>
      </activation>
      <modules>
        <module>dmn-tck-runner-myEngine</module>
      </modules>
    </profile>
```
