let album_update = {
    init: function () {
        let _this = this;
        $('#btn-delete-album').on('click', function () {
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
    },
    update: function () {
        let trackCount = document.getElementById("trackCount").value;
        let tracks = [];
        let entries = document.getElementsByName("track");
        for (let i = 0; i < trackCount; i++) {
            let track = {
                id: entries[i * 3].value,
                name: entries[i * 3 + 1].value,
                artist: entries[i * 3 + 2].value
            };
            tracks.push(track);
        }

        let album = {
            name: $('#album_name').val(),
            trackCount: $('#trackCount').val(),
            tracks: tracks
        }

        let albumId = $('#album_id').val();
        $.ajax({
            type: 'PUT',
            url: '/api/albums/' + albumId,
            contentType: 'application/json;charset=utf-8',
            dataType: 'json',
            data: JSON.stringify(album)
        }).done(function () {
            alert("수정되었습니다.");
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
};
album_update.init();
