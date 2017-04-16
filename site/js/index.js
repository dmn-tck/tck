$(document).ready(function () {
    // Row Checkbox Selection
    $("input[type='checkbox']").change(function (e) {
        if ($(this).is(":checked")) {
            $(this).closest('.list-group-item').addClass("active");
        } else {
            $(this).closest('.list-group-item').removeClass("active");
        }
    });
    // toggle dropdown menu
    $('.list-view-pf-actions').on('show.bs.dropdown', function () {
        var $this = $(this);
        var $dropdown = $this.find('.dropdown');
        var space = $(window).height() - $dropdown[0].getBoundingClientRect().top - $this.find('.dropdown-menu').outerHeight(true);
        $dropdown.toggleClass('dropup', space < 10);
    });

    // click the list-view heading then expand a row
    $(".list-group-item-header").click(function (event) {
        if (!$(event.target).is("button, a, input, .fa-ellipsis-v")) {
            $(this).find(".fa-angle-right").toggleClass("fa-angle-down")
                .end().parent().toggleClass("list-view-pf-expand-active")
                .find(".list-group-item-container").toggleClass("hidden");
        } else {
        }
    })

    // click the close button, hide the expand row and remove the active status
    $(".list-group-item-container .close").on("click", function () {
        $(this).parent().addClass("hidden")
            .parent().removeClass("list-view-pf-expand-active")
            .find(".fa-angle-right").removeClass("fa-angle-down");
    })

});