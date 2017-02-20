<#-- @ftlvariable name="tIndLabels" type="java.util.Collection<org.omg.dmn.tck.Reporter.ReportTable>" -->
<#-- @ftlvariable name="tByLabels" type="org.omg.dmn.tck.Reporter.ReportTable" -->
<#-- @ftlvariable name="tAllTests" type="org.omg.dmn.tck.Reporter.ReportTable" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>DMN TCK Report</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        .report-header {
            padding-top: 60px;
            padding-bottom: 60px;
            font-size: 24px;
            text-align: left;
        }
        .report-header h1 {
            font-size: 60px;
            line-height: 1;
        }
        .icon-red {
            color: #c80f18;
        }
        .icon-yellow {
            color: #c87e18;
        }
        .icon-green {
            color: #0f820d;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="report-header ">
        <div class="container">
            <h1>DMN TCK Report Results</h1>
            <p>Results of the DMN TCK tests.</p>
        </div>
    </div>

    <div class="container">
        <h3>Test results by label</h3>
        <p>Results from all the tests sorted by label</p>

        <div class="table-responsive">
            <table class="table table-condensed table-bordered table-striped table-hover">
                <thead>
                <tr>
<#list tByLabels.headers as head>
                    <th> ${head} <br/>
                        <small>${tByLabels.headerDetails[head_index]}</small>
                    </th>
</#list>
                </tr>
                </thead>
                <tbody>
<#list tByLabels.rows as row>
                <tr>
                    <th class="text-nowrap" scope="row"><a href="#lbl${row_index}">${row.text[0]}</a></th>
                    <#list row.icons[1..] as icon>
                    <td align="center"><span class="glyphicon ${icon}" aria-hidden="true"><br/><small>${row.text[1..][icon_index]}</small></span></td>
                    </#list>
                </tr>
</#list>
                </tbody>
            </table>
        </div>
    </div>

    <div class="container">
        <h3>All tests in detail</h3>
        <p>Results from all the tests in the TCK</p>

        <div class="table-responsive">
            <table class="table table-condensed table-bordered table-striped table-hover">
                <thead>
                <tr>
                <#list tAllTests.headers as head>
                    <#if head_index != 1>
                    <th> ${head} <br/>
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
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[0]}">${row.text[0]}</th>
                    </#if>
                    <#if false && row.rowspan[1] gt 0 >
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[1]}">${row.text[1]}</th>
                    </#if>
                    <#if row.rowspan[2] gt 0 >
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[2]}">${row.text[2]}</th>
                    </#if>
                    <th class="text-nowrap" scope="row">${row.text[3]}</th>
                    <#list row.icons[4..] as icon>
                        <td align="center"><span class="glyphicon ${icon}" aria-hidden="true"></span></td>
                    </#list>
                </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>

    <#list tIndLabels as tlbl>
    <div class="container">
        <h3 id="lbl${tlbl_index}">All tests for label: ${tlbl.title}</h3>

        <div class="table-responsive">
            <table class="table table-condensed table-bordered table-striped table-hover">
                <thead>
                <tr>
                <#list tlbl.headers as head>
                    <#if head_index != 1>
                        <th> ${head} <br/>
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
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[0]}">${row.text[0]}</th>
                    </#if>
                    <#if false && row.rowspan[1] gt 0 >
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[1]}">${row.text[1]}</th>
                    </#if>
                    <#if row.rowspan[2] gt 0 >
                        <th class="text-nowrap" scope="row" rowspan="${row.rowspan[2]}">${row.text[2]}</th>
                    </#if>
                    <th class="text-nowrap" scope="row">${row.text[3]}</th>
                    <#list row.icons[4..] as icon>
                        <td align="center"><span class="glyphicon ${icon}" aria-hidden="true"></span></td>
                    </#list>
                </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
    </#list>

</div>
</body>
</html>