let album_save = {
    init: function () {
        let index = 0;
        let _this = this;
        $('#btn-save').on('click', function () {
            _this.save(index);
            index = 0;
        });
        $('#btn-add-track').on('click', function () {
            _this.addTrack(++index);
        });
    },
    save: function (index) {
        let tracks = [];

        for (let i = 0; i <= index; i++) {
            let track = {
                name: $('#track' + i + '_name').val(),
                artist: $('#track' + i + '_artist').val(),
            };
            tracks.push(track);
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
    },
    addTrack: function(index) {
        let trackListDiv = document.querySelector("#trackList");

        let trackDiv = document.createElement("div");

        trackDiv.innerHTML += `<div class="form-inline mb-sm-2">
                <label for="track${index}_name" class="mr-sm-2">트랙${index} 이름</label>
                <input name="track" type="text" class="mr-sm-2 form-control " id="track${index}_name"/>
                <label for="track${index}_artist" class="mr-sm-2">트랙${index} artist</label>
                <input name="track" type="text" class="mr-sm-2 form-control" id="track${index}_artist"/>
            </div>`;
        trackListDiv.appendChild(trackDiv);
    }
};
album_save.init();
