<#-- @ftlvariable name="header" type="org.omg.dmn.tck.ReportHeader" -->
<#-- @ftlvariable name="vendors" type="java.util.Map<java.lang.String,org.omg.dmn.tck.Vendor>" -->
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
    <!-- Chart.js -->
    <script src="js/Chart.bundle.js"></script>

    <!-- site libs -->
    <script src="js/index.js"></script>
    <link href="css/lib.css" rel="stylesheet" media="screen, print">

    <title>Decision Model and Notation TCK</title>
</head>
<body>

<script>
    window.chartColors = {
        red: 'rgb(255, 99, 132)',
        orange: 'rgb(255, 159, 64)',
        yellow: 'rgb(255, 205, 86)',
        green: 'rgb(75, 192, 192)',
        blue: 'rgb(54, 162, 235)',
        purple: 'rgb(153, 102, 255)',
        grey: 'rgb(231,233,237)'
    };

    var color = Chart.helpers.color;

<#list vendors?values as vendor>
        var c_data${vendor_index} = {
        labels: [
            "Succeeded", "Failed", "Skipped"
        ],
        datasets: [
            {
                backgroundColor: [
                    color(window.chartColors.green).alpha(0.5).rgbString(),
                    color(window.chartColors.red).alpha(0.5).rgbString(),
                    color(window.chartColors.yellow).alpha(0.5).rgbString()
                ],
                borderColor: [
                    window.chartColors.grey,
                    window.chartColors.grey,
                    window.chartColors.grey
                ],
                borderWidth: 1,
                data: [
                    ${vendor.succeeded}, ${vendor.failed}, ${header.totalTests - vendor.succeeded - vendor.failed}
                ]
            }
        ]
    };
</#list>

    window.onload = function() {
<#list vendors?values as vendor>
        var ctx = document.getElementById("chart${vendor_index}").getContext("2d");
        window.chart${vendor_index} = new Chart(ctx, {
            type: 'doughnut',
            data: c_data${vendor_index},
            options: {
                responsive: true,
                legend: {
                    position: 'right',
                },
                title: {
                    display: true,
                    text: 'Submitted results'
                }
            }
        });
</#list>
    };

</script>

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
                <li class="active"><strong>Home</strong></li>
            </ol>
        </div>
    </div>
    <div class="content">
        <div class="container-fluid">

            <div class="list-group list-view-pf list-view-pf-view">

            <#list vendors?values as vendor>
                <!-- ${vendor.product} ${vendor.version} -->
                <div class="list-group-item">
                    <div class="list-group-item-header">
                        <div class="list-view-pf-actions">
                            <a href="overview_${vendor.fileNameId}.html" class="btn btn-default">Overview</a>
                            <a href="detail_${vendor.fileNameId}.html?label_id=all-tests&breadcrumb_label=All%20Tests" class="btn btn-default">All Tests</a>
                            <!--div class="dropdown pull-right dropdown-kebab-pf">
                                <button class="btn btn-link dropdown-toggle" type="button" id="dropdownKebabRight${vendor_index}" data-toggle="dropdown" aria-haspopup="true"
                                        aria-expanded="true">
                                    <span class="fa fa-ellipsis-v"></span>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownKebabRight${vendor_index}">
                                    <li><a href="#">Action</a></li>
                                    <li><a href="#">Another action</a></li>
                                    <li><a href="#">Something else here</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="#">Separated link</a></li>
                                </ul>
                            </div-->
                        </div>
                        <div class="list-view-pf-main-info">
                            <div class="list-view-pf-left">
                                <span class="fa fa-university list-view-pf-icon-sm"></span>
                            </div>
                            <div class="list-view-pf-body">
                                <div class="list-view-pf-description">
                                    <div class="list-group-item-heading">
                                        ${vendor.name}
                                    </div>
                                    <div class="list-group-item-text">
                                        ${vendor.product} ${vendor.version}
                                    </div>
                                </div>
                                <div class="list-view-pf-additional-info">
                                    <div class="list-view-pf-additional-info-item">
                                        Submitted:&nbsp;<b>${vendor.submitted}/${header.totalTests}</b>
                                    </div>
                                    <div class="list-view-pf-additional-info-item">
                                        Last Submission:&nbsp;<b>${vendor.lastUpdate}</b>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="list-group-item-container container-fluid hidden">
                        <div class="close">
                            <span class="pficon pficon-close"></span>
                        </div>
                        <div class="row">

                            <div class="col-md-2">
                                <canvas id="chart${vendor_index}"></canvas>
                            </div>

                            <div class="col-md-10">
                                <dl class="dl-horizontal">
                                    <dt>Last Submission: </dt><dd>${vendor.lastUpdate}</dd>
                                    <dt>Tests: </dt><dd>${header.totalTests}</dd>
                                    <dt>Labels: </dt><dd>${header.totalLabels}</dd>
                                    <dt>Succeeded: </dt><dd>${vendor.succeeded}</dd>
                                    <dt>Failed: </dt><dd>${vendor.failed}</dd>
                                    <dt>Skipped: </dt><dd>${header.totalTests - vendor.succeeded - vendor.failed}</dd>
                                    <dt>Info: </dt><dd>${vendor.comment}</dd>
                        		<#if vendor.tckRunnerManualUrl?has_content>
									<dt>TCK Runner manual: </dt><dd>This vendor has supplied an instruction manual for user to reproduce TCK test results, available <a href="${vendor.tckRunnerManualUrl}" target="_blank">at this link <span class="fa fa-external-link"></span></a>.</dd>
								</#if>
                                </dl>
                            </div>
                        </div>
                    </div>
                </div>
            </#list>
            </div>
        </div>
    </div>

    <#include "footer.ftl">
</div>
<!--wrapper -->

</body>
</html>