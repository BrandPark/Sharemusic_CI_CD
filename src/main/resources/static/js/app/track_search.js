let search = {
    init: function () {
        /* Create a cache object */
        const cache = new LastFMCache();

        /* Create a LastFM object */
        const lastfm = new LastFM({
            apiKey    : '4cee87c012bfa3c848cbb0ed9c29b57d',
            apiSecret : '42d79d86721c841df4c38a5f96d99349',
            cache     : cache
        });

        $('#search_artist').on('keyup input', function () {
            let word = $(this).val();
            let searchList = $('#search-list');

            lastfm.artist.getTopTracks({artist: word}, {success: function(data){
                    console.log(data);
                    /* Use data. */
                    let tracks = data['toptracks']['track'];

                    let len = tracks.length < 8 ? tracks.length : 8;

                    searchList.children().remove();

                    for (let i = 0; i < len; i++) {
                        searchList.append(`<div class="list-group-item list-group-item-action">${tracks[i].name}</div>`);
                    }
                }, error: function(code, message){
                    /* Show error message. */
                }});
        });
        $('#search_track').on('keyup input', function () {
            let word = $(this).val();
            let searchList = $('#search-list');

            lastfm.track.search({track: word}, {success: function(data){
                    /* Use data. */
                    console.log(data);
                    let tracks = data['results']['trackmatches']['track'];

                    let len = tracks.length < 8 ? tracks.length : 8;

                    searchList.children().remove();

                    for (let i = 0; i < len; i++) {
                        searchList.append(`<div class="list-group-item list-group-item-action">${tracks[i].name} <span style="font-size:0.8rem; margin-left:2px;">${tracks[i].artist}</span></div>`);
                    }
                }, error: function(code, message){
                    /* Show error message. */
                }});
        });
    }
};
search.init();
