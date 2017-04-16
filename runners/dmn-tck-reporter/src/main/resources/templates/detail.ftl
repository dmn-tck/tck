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
    <link href="bower_components/patternfly/dist/css/patternfly.min.css" rel="stylesheet" media="screen, print">
    <link href="bower_components/patternfly/dist/css/patternfly-additions.min.css" rel="stylesheet" media="screen, print">
    <!-- jQuery -->
    <script src="bower_components/jquery/dist/jquery.min.js"></script>
    <!-- Bootstrap -->
    <script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <!-- PatternFly -->
    <script src="bower_components/patternfly/dist/js/patternfly.min.js"></script>
    <!-- c3 -->
    <script src="bower_components/c3/c3.min.js"></script>
    <!-- c3 -->
    <script src="bower_components/d3/d3.min.js"></script>
    <!-- jquery max height -->
    <script src="bower_components/matchHeight/dist/jquery.matchHeight-min.js"></script>
    <script src="bower_components/datatables/media/js/jquery.dataTables.js"></script>

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
                <a class="navbar-brand" href="/">
                    <img src="images/logo.png" alt="PatternFly Enterprise Application"/>
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
                            <th class="text-nowrap" scope="row" rowspan="${row.rowspan[0]}" style="vertical-align:middle">${row.text[0]}</th>
                        </#if>
                        <#if false && row.rowspan[1] gt 0 >
                            <th class="text-nowrap" scope="row" rowspan="${row.rowspan[1]}" style="vertical-align:middle">${row.text[1]}</th>
                        </#if>
                        <#if row.rowspan[2] gt 0 >
                            <th class="text-nowrap" scope="row" rowspan="${row.rowspan[2]}" style="vertical-align:middle">${row.text[2]}</th>
                        </#if>
                        <th class="text-nowrap" scope="row">${row.text[3]}</th>
                        <td align="center" style="vertical-align:middle"><span class="glyphicon ${row.icons[4]}" aria-hidden="true"></span></td>
                    </tr>
                    </#list>
                    <tr class="info">
                        <th colspan="3" style="vertical-align:middle">Total</th>
                        <td align="center" style="vertical-align:middle">${tAllTests.totals[0]}</td>
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
                                    <th class="text-nowrap" scope="row" rowspan="${row.rowspan[0]}" style="vertical-align:middle">${row.text[0]}</th>
                                </#if>
                                <#if false && row.rowspan[1] gt 0 >
                                    <th class="text-nowrap" scope="row" rowspan="${row.rowspan[1]}" style="vertical-align:middle">${row.text[1]}</th>
                                </#if>
                                <#if row.rowspan[2] gt 0 >
                                    <th class="text-nowrap" scope="row" rowspan="${row.rowspan[2]}" style="vertical-align:middle">${row.text[2]}</th>
                                </#if>
                                <th class="text-nowrap" scope="row" style="vertical-align:middle">${row.text[3]}</th>
                                <td align="center" style="vertical-align:middle"><span class="glyphicon ${row.icons[4]}" aria-hidden="true"></span></td>
                            </tr>
                            </#list>
                            <tr class="info">
                                <th colspan="3" style="vertical-align:middle">Total</th>
                                <td align="center" style="vertical-align:middle">${tlbl.totals[0]}</td>
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
