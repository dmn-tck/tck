$(document).ready(function () {
    var label_id = getUrlParameter('label_id');
    var breadcrumb_label = getUrlParameter('breadcrumb_label');
    filterResults(label_id, breadcrumb_label);
    $("#label-filter").val(label_id);

    $('#label-filter').on('change', function() {
        var text = $("#label-filter option:selected").text();
        filterResults(this.value, text);

    })


    //para cada tabela de resultados, vc deve inicializa-la
    $('#all-tests-table').DataTable( {
        //edson tem mais opcoes aqui https://datatables.net/reference/option/
        //remova estes falses pra liberar o search
        searching: false,
        paging: false,
        bInfo:false
    });

    MergeGridCells("#all-tests-table");

});
function filterResults(label_id, breadcrumb_label) {

    $('.breadcrumbs #breadcrumb-value').text(breadcrumb_label)

    $("#results > *").css('display', 'none');
    $("#results > #" + label_id).css('display', '');

}

function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function MergeGridCells(tableSelector) {
    var dimension_cells = new Array();
    var dimension_col = null;
    //hack to just merge first column
    //var columnCount = $(tableSelector+" tr:first th").length;
    var columnCount = 2;
    for (dimension_col = 0; dimension_col < columnCount; dimension_col++) {
        // first_instance holds the first instance of identical td
        var first_instance = null;
        var rowspan = 1;
        // iterate through rows
        $(tableSelector).find('tr').each(function () {

            // find the td of the correct column (determined by the dimension_col set above)
            var dimension_td = $(this).find('td:nth-child(' + dimension_col + ')');

            if (first_instance == null) {
                // must be the first row
                first_instance = dimension_td;
            } else if (dimension_td.text() == first_instance.text()) {
                // the current td is identical to the previous
                // remove the current td
                dimension_td.remove();
                ++rowspan;
                // increment the rowspan attribute of the first instance
                first_instance.attr('rowspan', rowspan);
            } else {
                // this cell is different from the last
                first_instance = dimension_td;
                rowspan = 1;
            }
        });
    }
}