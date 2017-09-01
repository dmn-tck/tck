<#-- @ftlvariable name="vendor" type="org.omg.dmn.tck.Vendor" -->
<#-- @ftlvariable name="cByLabels" type="java.util.Collection<org.omg.dmn.tck.ReportChart>" -->
<#-- @ftlvariable name="header" type="org.omg.dmn.tck.ReportHeader" -->
<#-- @ftlvariable name="tIndLabels" type="java.util.Collection<org.omg.dmn.tck.ReportTable>" -->
<#-- @ftlvariable name="tByLabels" type="org.omg.dmn.tck.ReportTable" -->
<#-- @ftlvariable name="tAllTests" type="org.omg.dmn.tck.ReportTable" -->
<!DOCTYPE html>
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
    <script src="js/jquery.dataTables.js"></script>

    <!-- site libs -->
    <script src="js/lib.js"></script>
    <script src="js/detail.js"></script>
    <link href="css/lib.css" rel="stylesheet" media="screen, print">

    <!-- chart js -->
    <title>DMN TCK results for ${vendor.product} ${vendor.version}</title>
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
                    <li class="active">
                        <a href="index.html">Submitters</a>
                    </li>
                    <li>
                        <a href="glossary.html">Glossary</a>
                    </li>
                    <li>
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
                <li>
                    <a href="overview_${vendor.fileNameId}.html">${vendor.product} ${vendor.version}</a>
                </li>
                <li class="active"><strong id="breadcrumb-value">Details by Label</strong></li>
            </ol>
        </div>
    </div>
    <div class="content container">
        <div>
            <label>Filter By: </label>
            <select id="label-filter" class="combobox">
                <option value="all-tests">All Tests</option>
                <#list tIndLabels as tlbl>
                    <option value="tb${tlbl_index}">${tlbl.title}</option>
                </#list>
            </select>
        </div>
        <div id="results">  <!-- ponto de partida da filtragem -->

            <div id="all-tests">
                <!-- para cada tabela, ela deve ser iniciada no details.js-->
                <table class="table table-condensed table-striped table-bordered table-hover" id="all-tests-table">
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
                        <td align="center" style="vertical-align:middle"><span class="glyphicon ${row.icons[6]}" aria-hidden="true"></span></td>
                        <td scope="row">${row.text[7]}</td>
                    </tr>
                    </#list>
                    <tr class="info">
                        <td colspan="5" style="vertical-align:middle">Total</td>
                        <td align="center" style="vertical-align:middle">${tAllTests.totals[0]}</td>
                        <td colspan="1" style="vertical-align:middle"></td>
                    </tr>
                    </tbody>
                </table>
            </div>

        <#list tIndLabels as tlbl>
            <div id="tb${tlbl_index}">
                <div class="table-responsive">
                    <table class="table table-condensed table-bordered table-striped table-hover">
                        <thead>
                        <tr class="info">
                            <#list tlbl.headers as head>
                                <#if head_index != 1>
                                    <th style="vertical-align:middle"> ${head} <br/>
                                        <small>${tlbl.headerDetails[head_index]}</small>
                                    </th>
                                </#if>
                            </#list>
                        </tr>
                        </thead>
                        <tbody>
                            <#list tlbl.rows as row>
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
                                <td class="text-nowrap" scope="row" style="vertical-align:middle">${row.text[5]}</td>
                                <td align="center" style="vertical-align:middle"><span class="glyphicon ${row.icons[6]}" aria-hidden="true"></span></td>
                                <td scope="row">${row.text[7]}</td>
                            </tr>
                            </#list>
                            <tr class="info">
                                <td colspan="5" style="vertical-align:middle">Total</td>
                                <td align="center" style="vertical-align:middle">${tlbl.totals[0]}</td>
                                <td colspan="1" style="vertical-align:middle"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </#list>
        </div>
    </div>


    <#include "footer.ftl">
</div>
</body>
</html>
