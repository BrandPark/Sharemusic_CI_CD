let save_album = {
    init: function () {
        let _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
    },
    save: function () {
        let tracks = [];

        for (let i = 0; i < 2; i++) {
            let track = {
                name: $('#track' + i + '_name').val(),
                artist: $('#track' + i + '_artist').val(),
            };
            tracks.push(track);
            // tracks.push(track);
        }

        let data = {
            name: $('#album_name').val(),
            tracks: tracks
        }

        $.ajax({
            type: 'POST',
            url: '/api/albums',
            dataType: 'json',
            contentType: 'application/json;charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert("글 등록 성공");
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};
save_album.init();
