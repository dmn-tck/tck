![TCK Validation](https://github.com/dmn-tck/tck/actions/workflows/global_validation.yml/badge.svg?branch=master)

# Technology Compatibility Kit for the Decision Model and Notation (DMN) standard

The **Decision Model and Notation Technology Compatibility Kit** (DMN TCK) is a community-led proposal for a verifiable and executable method to demonstrate the Conformance level of support provided by a Vendor-supplied DMN implementation.

In addition, this method provides more finer-grained details on the actual support for specific DMN constructs for each implementation.

The DMN TCK working group is composed by vendors and practitioners of DMN, with the goal to assist and ensure Conformance to the specification, by defining test cases and expected results, by providing tools to run these tests and validate results; the outcome also represent an additional and pragmatical way to recognize and publicize vendor success.

Joining the TCK is free, it also holds regular conference calls, and new members are always welcome.

## Current Result Web Site

* https://dmn-tck.github.io/tck/

## Collaboration Site

* https://docs.google.com/document/d/1GnXZcwSczIbyNtMDM8ZnITVNgpIVi9nGbyt-0WzKxec/edit

## How to join

* https://github.com/dmn-tck/tck/wiki/Joining-the-Effort

## Current Folders:

TestExamples - data files used to share and isolate the proper format for tests.

ReferenceTests - the actual collection of reference tests.

Eventually we will have folders for every tool (software product, SaaS service, etc) that claims conformance that will hold the output of their test run.

## Scope of work

Goals:
* 📝 Define a set of test cases
* 🔬 Carefully assure conformance to the spec
* 🛠️ Provide tools to run the tests
* 👏 Recognize vendor successes

NON-Goals:
* 🚫 Extend or enhance the DMN Spec; instead, RTF is responsible for that
* 🚫 Focus on esoteric features; instead, focus Only features that exist in one or more implementations
* 🚫 Favor an implementation over another; instead, Remain technology and vendor neutral

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
* We will include only test cases that are known to pass on at least one implementation.
Caveat: ~~at the current~~ at the very beginning of this project, there were no known implementations at conformance level 3, so necessarily there has been tests that no implementation could run, but once CL3 has been attained it will not be our practice to invent new, far-reaching tests for abstract situations.
* We will strive to get close to the spec, but if parts of the spec prove impossible to implement, we will not get involved correcting the specification.
* Similarly, we strive to implement as much of the spec as possible, but if the spec is too expansive we may limit the scope to a subset that we all agree upon.
* If the spec is ambiguous, we will make an interpretation of the spec according to what can actually be realized in running code, document that, and remain consistent to that in the future.

Reasons to be interested in this community-led project:

* A way for Vendors to **demonstrate** their compliance to the Standard
* Provide files to **help** Vendors test for error and become compliant
* A way for Customers and Users to **assess** how compliant a Vendor is

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

## Some DMN TCK presentations

* Keith Swenson: Close is not Close Enough ([DecisionCamp 2019](https://decisioncamp2019.wordpress.com/program/#KeithSwenson))
* Keith Swenson: DMN TCK Working Group ([bpmNEXT 2019](https://www.youtube.com/watch?v=75fk-i3K9U0))
* Matteo Mortari: Introduction and Updates on DMN TCK ([DecisionCamp 2018](https://decisioncamp2018.wordpress.com/program/#DMNTCK))
* Keith Swenson: DMN Technology Compatiability Toolkit (TCK) ([bpmNEXT 2018](https://www.youtube.com/watch?v=MqSbBtY2dKQ))
* Keith Swenson: Making the Standard Real: The DMN TCK ([bpmNEXT 2017](https://www.youtube.com/watch?v=M8goCq72lbo))
