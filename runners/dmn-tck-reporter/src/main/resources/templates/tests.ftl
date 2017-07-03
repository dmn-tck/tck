<#-- @ftlvariable name="header" type="org.omg.dmn.tck.ReportHeader" -->
<#-- @ftlvariable name="tAllTests" type="org.omg.dmn.tck.ReportTable" -->
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <!-- PatternFly Styles -->
    <link href="css/patternfly.min.css" rel="stylesheet" media="screen, print">
    <link href="css/patternfly-additions.min.css" rel="stylesheet" media="screen, print">
    <!-- jQuery -->
    <script src="js/jquery.min.js"></script>
    <!-- Bootstrap -->
    <script src="js/bootstrap.min.js"></script>
    <!-- PatternFly -->
    <script src="js/patternfly.min.js"></script>
    <!-- c3 -->
    <script src="js/c3.min.js"></script>
    <!-- c3 -->
    <script src="js/d3.min.js"></script>
    <!-- jquery max height -->
    <script src="js/jquery.matchHeight-min.js"></script>

    <!-- site libs -->
    <script src="js/lib.js"></script>
    <link href="css/lib.css" rel="stylesheet" media="screen, print">

    <title>DMN TCK test cases</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<div class="wrapper">
    <div class="header">
        <nav class="navbar navbar-default navbar-pf" role="navigation">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">
                    <img src="images/logo.png" alt="Decision Model and Notation TCK"/>
                </a>
            </div>
            <div class="collapse navbar-collapse navbar-collapse-1">
                <ul class="nav navbar-nav navbar-primary">
                    <li>
                        <a href="index.html">Submitters</a>
                    </li>
                    <li>
                        <a href="glossary.html">Glossary</a>
                    </li>
                    <li class="active">
                        <a href="tests.html">Tests</a>
                    </li>

                </ul>
            </div>
        </nav>
        <div class="breadcrumbs">
            <ol class="breadcrumb">
                <li>
                    <a href="index.html">Home</a>
                </li>
                <li class="active"><strong>Test Cases</strong></li>
            </ol>
        </div>
    </div>
    <div class="content">
        <div class="container">
            <div class="report-header ">
                <div class="row">
                    <div class="col-md-12">
                        <h1>DMN TCK Test Cases</h1>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <small>
                            <p><strong>Tests:</strong> ${header.totalTests}</p>
                        </small>
                    </div>
                    <div class="col-md-2">
                        <small>
                            <p><strong>Labels:</strong> ${header.totalLabels}</p>
                        </small>
                    </div>
                </div>
            </div>

            <div class="content container">
                <div id="all-tests">
                    <table class="table table-striped table-bordered table-hover" id="all-tests-table">
                        <thead>
                        <tr>
                        <#list tAllTests.headers as head>
                            <#if head_index != 1>
                                <th style="vertical-align:middle"> ${head} <br/>
                                    <small>${tAllTests.headerDetails[head_index]}</small>
                                </th>
                            </#if>
                        </#list>
                        </tr>
                        </thead>
                        <tbody>
                        <#list tAllTests.rows as row>
                        <tr>
                            <#if row.rowspan[0] gt 0 >
                                <td class="text-nowrap" scope="row" rowspan="${row.rowspan[0]}" style="vertical-align:middle">${row.text[0]}</td>
                            </#if>
                            <#if false && row.rowspan[1] gt 0 >
                                <td class="text-nowrap" scope="row" rowspan="${row.rowspan[1]}" style="vertical-align:middle">${row.text[1]}</td>
                            </#if>
                            <#if row.rowspan[2] gt 0 >
                                <td class="text-nowrap" scope="row" rowspan="${row.rowspan[2]}" style="vertical-align:middle">${row.text[2]}</td>
                                <td align="center" rowspan="${row.rowspan[2]}" style="vertical-align:middle">
                                    <#if row.text[3] == "OK">
                                    <a target="_blank" href="https://github.com/dmn-tck/tck/blob/master/TestCases/${row.text[0]}/${row.text[1]}/${row.text[1]}.pdf">
                                        <span class="${row.icons[3]}" aria-hidden="true"></span>
                                    </a>
                                    <#else>
                                        <span class="${row.icons[3]} text-muted" aria-hidden="true"></span>
                                    </#if>
                                </td>
                                <td align="center" rowspan="${row.rowspan[2]}" style="vertical-align:middle">
                                    <a target="_blank" href="https://github.com/dmn-tck/tck/tree/master/TestCases/${row.text[0]}/${row.text[1]}">
                                        <span class="${row.icons[4]}" aria-hidden="true"></span>
                                    </a>
                                </td>
                            </#if>
                            <td class="text-nowrap" scope="row">${row.text[5]}</td>
                            <td class="text-nowrap" scope="row">${row.text[6]}</td>
                        </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <#include "footer.ftl">

</div>
</body>
</html>