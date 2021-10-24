$('textarea.auto-size').on('keydown', function () {
    if ($(this).height() < 96) {
        $(this).height(1).height($(this).prop('scrollHeight') + 12);
    }
});