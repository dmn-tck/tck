# dmn-tck
# Technology Compatibility Kit for the Decision Model and Notation (DMN) standard

## Current Result Web Site

* https://dmn-tck.github.io/tck/

## Collaboration Site

* https://s06.circleweaver.com/weaver/t/wfmc/dmn-reference-implementation/frontPage.htm

## How to join

* https://github.com/dmn-tck/tck/wiki/Joining-the-Effort

## Current Folders:

TestExamples - data files used to share and isolate the proper format for tests.

ReferenceTests - the actual collection of reference tests.

Eventually we will have folders for every tool (software product, SaaS service, etc) that claims conformance that will hold the output of their test run.



For this effort, here are the goals to achieve in order to consider this a success

* **Deliverables**: We will collect and organize test cases that can be used by implementers to demonstrate compliance to the spec when evaluating a DMN model.
* **Format**: A single DMN model might be tested with any number of different inputs, and each combination of model and input data is considered a test case.. A test case consists of
  * Some document on what the test is designed to test,
  * Human readible / end user visual representation of the decision: a screen shot at the minimum,
    * a serialized DMN model,
    * a serialized set of input data,
    * serialized set of output/response data.
* **Availability**: Test cases will be files that can be accessed freely by anyone using a creative commons Share-Alike-With-Attribution license.
* **Conformance**: Test cases will be associated to specific elements of the spec so it is clear what aspect of conformance is being tested.
* **Promotion**: We will promote the tests in order to assure that potential users of DMN are aware.
* **Completeness**: we will aim to test all aspects of conformance level 3.
* **Uniformity**: We will strive to eliminate contradictory tests. That is, if there is a test which according to two different parts of the spec would produce two different outputs, then the correct test output will be chosen so that a single consistent implementation might generate all the test responses.

To be clear, there are several things that are not the goals of this group

* We will not be involved in defining or extending the DMN specification -- this is the job of the OMG committee.
* We will focus on concrete input and output examples. We will avoid general discussion about what should and should not generally be true.
* We will include only test cases that are known to pass on at least one implementation. Caveat: at the current time there are no known implementations at conformance level 3, so necessarily there are tests that no implementation can run today, but once CL3 has been attained it will not be our practice to invent new, far-reaching tests for abstract situations.
* We will strive to get close to the spec, but if parts of the spec prove impossible to implement, we will not get involved correcting the specification.
* Similarly, we strive to implement as much of the spec as possible, but if the spec is too expansive we may limit the scope to a subset that we all agree upon.
* If the spec is ambiguous, we will make an interpretation of the spec according to what can actually be realized in running code, document that, and remain consistent to that in the future.

## How to run the tests youself

A couple of vendors provide [Java-based runners](/runners), which you can run using the following steps:
1. Install a [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Install [Apache Maven](http://maven.apache.org/).
3. Download the [DMN TCK including the runners](https://github.com/dmn-tck/tck/archive/master.zip) and extract it.
4. Open a terminal in the `runners` directory and run the tests in one or more engines using e.g.
```sh
$ mvn clean install -P camunda,drools,jdmn
```
5. Check the log output for errors or exceptions and compare number of errors or skipped tests reported for each vendor with what's on the [TCK website](https://dmn-tck.github.io/tck/). For details on failed tests you can also look into the `target/surefire-reports` directory within each executed runner's sub-directory.

If you have trouble executing a runner consult the `README.md` file in its sub-directory or contact the vendor.
