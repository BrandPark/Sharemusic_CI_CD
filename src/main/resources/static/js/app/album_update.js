let album_update = {
    init: function () {
        let _this = this;
        $('#btn-delete-album').on('click',function(){
            _this.delete();
        });
        $('#btn-update-album').on('click', function () {
            _this.update();
        });
    },
    delete: function () {
        let album_id = $('#album_id').val();

        $.ajax({
            type: 'delete',
            url: '/api/albums/' + album_id,
            contentType: 'application/json;charset=utf-8',
            dataType: 'json'
        }).done(function () {
            alert('삭제되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};
album_update.init();
