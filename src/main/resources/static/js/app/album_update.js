let album_update = {
    init: function () {
        let _this = this;

        $('#btn-update').on('click', function () {
            _this.updateMode();
        });
        $('#btn-delete-album').on('click', function () {
            _this.delete();
        });
        $('#btn-update-album').on('click', function () {
            _this.update();
        });
        $('#btn-add-track-tb').on('click', function () {
            _this.addTrackTb();
        });
        $('input[name^="track_"]').on('change', function () {
            let parent = $(this).closest("tr");
            parent.find('input[name="track_state"]').val("U");
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
    updateMode: function(){
        $('input[name^="track"]').attr('disabled',false);
        $('input[name^="album"]').attr('disabled',false);
        $('#btn_remove_track').attr('disabled', false);
    },
    update: function () {
        let tracks = [];
        let rows = $('#tbody').children();
        for (let i = 0; i < rows.length; i++) {
            let cols = $(rows[i]).find('input');
            let track = {
                state: cols[0].value,
                id: cols[1].value,
                name: cols[2].value,
                artist: cols[3].value
            };
            tracks.push(track);
        }

        let album = {
            name: $('#album_name').val(),
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
            window.location.href = '/albums/' + albumId;
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    addTrackTb: function () {
        let addTagStr = `<tr>
                <td><input name="track_state" class="form-control" type="text" value="I"/></td>
                <td><input name="track_id" class="form-control" type="text"/></td>
                <td><input name="track_name" class="form-control" type="text"/></td>
                <td><input name="track_artist" class="form-control" type="text"/></td>
                <td><button class="btn btn-danger" role="button" onclick="album_update.removeTrack(this)">삭제</button></td>
            </tr>`;
        $('#tbody').prepend(addTagStr);
    },
    removeTrack: function (btn) {
        let row = $(btn).closest('tr');
        let state = $(row).find('input[name="track_state"]');
        if(state.val() == 'I')
            row.remove();
        else
            state.val("D");
    }
};
album_update.init();
