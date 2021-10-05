<#-- @ftlvariable name="header" type="org.omg.dmn.tck.ReportHeader" -->
<#-- @ftlvariable name="tAllLabels" type="org.omg.dmn.tck.ReportTable" -->
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

    <title>DMN TCK Glossary</title>
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
                <a class="navbar-brand" href="index.html">
                    <img src="images/logo.png" alt="DMN Technology Compatibility Kit"/>
                </a>
            </div>
            <div class="collapse navbar-collapse navbar-collapse-1">
                <ul class="nav navbar-nav navbar-primary">
                    <li>
                        <a href="index.html">Submitters</a>
                    </li>
                    <li class="active">
                        <a href="glossary.html">Glossary</a>
                    </li>
                    <li>
                        <a href="tests.html">Tests</a>
                    </li>
                    <li>
                        <a href="about.html">About</a>
                    </li>

                </ul>
            </div>
        </nav>
        <div class="breadcrumbs">
            <ol class="breadcrumb">
                <li>
                    <a href="index.html">Home</a>
                </li>
                <li class="active"><strong>Glossary</strong></li>
            </ol>
        </div>
    </div>
    <div class="content container">
        <div id="results">
            <div id="all-labels">
            <table class="table table-condensed table-striped table-bordered table-hover table-info" id="all-labels-table">
                <thead class="table-head">
                <tr>
                <#list tAllLabels.headers as head>
                    <th style="vertical-align:middle"> ${head} <br/>
                        <small>${tAllLabels.headerDetails[head_index]}</small>
                    </th>
                </#list>
                </tr>
                </thead>
                <tbody>
                <#list tAllLabels.rows as row>
                <tr>
                    <td class="text-nowrap" scope="row" style="vertical-align:middle"><b>${row.text[0]}</b></td>
                    <td scope="row" style="vertical-align:middle">${row.text[1]}</td>
                    <td scope="row" style="vertical-align:middle">${row.text[2]}</td>
                </tr>
                </#list>
                </tbody>
            </table>
            </div>
        </div>
    </div>

    <#include "footer.ftl">

</div>
</body>
</html>