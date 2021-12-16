$(function () {
    $("textarea.auto-size").each(function () {
        this.setAttribute("style", "height:" + (this.scrollHeight) + "px;");
    }).on("input", function () {
        if (this.scrollHeight < 120) {
            this.style.height = "auto";
            this.style.height = (this.scrollHeight) + "px";
        }
    });

    viewController.init();
    search.init();
    wave.init();
    notification.init();
    dateConverter.formRelativeTime();
});

const util = {
    toBrTag: function (selector) {
        let $selector = $(selector);
        let val = $selector.val();
        val = val.replace(/\n/gm, "<br>");
        $selector.val(val);
    },
    toNewLine: function (selector) {
        let $selector = $(selector);
        let val = $selector.val();
        val = val.replace(/<br>/gm, "\n");
        $selector.val(val);
    }
}

const viewController = {
    init: function () {
        this.btnInit();
        this.inputInit();
    },
    btnInit: function () {
        this._followBtnInit();
    },
    inputInit: function () {
        $('input[type=text], textarea').attr("spellcheck", false);
    },
    _followBtnInit: function () {
        let _this = this;
        $('body').on('click', '.btn-follow, #btn-follow', function () {
            let $this = $(this);
            $this.removeClass("btn-follow");
            $this.addClass("btn-unfollow");
            $this.text("언 팔로우");

            let targetId = $this.attr('data-index');
            _this._follow(targetId, function(){
                if ($this.hasClass('btn-profile')) {
                    let followerCnt = parseInt($('#follower').text())
                    if (!isNaN(followerCnt)) {
                        $('#follower').text(followerCnt + 1);
                    }
                }
            });
        });
        $('body').on('click', '.btn-unfollow', function () {
            let $this = $(this);
            $this.removeClass("btn-unfollow");
            $this.addClass("btn-follow");
            $this.text("팔로우");

            let targetId = $this.attr('data-index');
            _this._unfollow(targetId, function () {
                if ($this.hasClass('btn-profile')) {
                    let followerCnt = parseInt($('#follower').text())
                    if (!isNaN(followerCnt)) {
                        $('#follower').text(followerCnt - 1);
                    }
                }
            });
        });
    },
    _follow: function (targetId, callback) {
        let _callback = callback;

        $.ajax({
            url: "/api/v1/accounts/" + targetId + "/follow",
            method: 'post',
        }).done(function (data) {
            if (data) {
                _callback();
            }
        }).fail(function () {
            alert("팔로우를 실패하였습니다.");
        });
    },
    _unfollow: function (targetId, callback) {
        let _callback = callback;

        $.ajax({
            url: "/api/v1/accounts/" + targetId + "/unfollow",
            method: 'post',
        }).done(function (data) {
            if (data) {
                _callback();
            }
        }).fail(function () {
            alert("언팔로우를 실패하였습니다.");
        });
    },
}

const search = {
    init: function () {
        $('#search-input').on('keydown', function (event) {
            if (event.key === "Enter") {
                const type = $(this).attr("search-type");
                const q = $(this).val();

                window.location.href = "/search?q=" + encodeURIComponent(q) + "&type=" + encodeURIComponent(type);
            }
        });
        $('#search-box .dropdown-item').on('click', function () {
            const selectType = $(this).attr('value');
            const selectTypeName = $(this).text() + ' 검색';
            $('#search-input').attr('placeholder', selectTypeName);
            $('#search-input').attr('search-type', selectType);
        });
    }
}
const notification = {
    init: function () {
        const _this = this;

        $('.notification, #notification-list').on('click', '.notification-item', function () {
            const notificationId = $(this).find('.notification-link')[0].getAttribute("data-index");
            const link = $(this).find('.notification-link')[0].getAttribute('link');
            _this.checkNotification(notificationId, link);
        });
    },
    checkNotification: function(notificationId, link) {
        $.ajax({
            url: "/api/v1/notifications/" + notificationId,
            type: 'PUT'
        }).done(function(){
            window.location.href = link;
        }).fail(function(error){
            console.log(error);
            alert(error.responseJSON.errorMessage);
        });
    }
}
const dateConverter = {
    formRelativeTime: function (bound) {
        moment.locale('ko');
        if (bound == null) {
            $(".relative-time").text(function (index, dateTime) {
                return moment(dateTime, "YYYY-MM-DD`T`hh:mm").fromNow();
            });
        } else {
            $(bound + " .relative-time").text(function (index, dateTime) {
                return moment(dateTime, "YYYY-MM-DD`T`hh:mm").fromNow();
            });
        }
    }
}
const wave = {
    init: function () {
        $("body").prepend("<canvas style='position:absolute;'></canvas>");

        // const gui = new dat.GUI(),
        guiSet = {
            frequency: 10,
            reset: () => {
                main.reset();
            } };


        // gui.add(guiSet, 'frequency').min(10).max(50);
        // gui.add(guiSet, 'reset');

        const main = {};

        /*========================================
        Initialize
        ========================================*/

        main.init = () => {
            main.c = document.querySelector('canvas');
            main.ctx = main.c.getContext('2d');
            main.simplex = new SimplexNoise();
            main.events();
            main.reset();
            main.loop();
        };

        /*========================================
        Reset
        ========================================*/

        main.reset = () => {
            main.w = window.innerWidth;
            main.h = window.innerHeight;
            main.cx = main.w / 2;
            main.cy = main.h / 2;
            main.c.width = main.w;
            main.c.height = main.h;
            main.count = Math.floor(main.w / guiSet.frequency); // Wave frequency
            main.xoff = 0;
            main.xinc = 0.05;
            main.yoff = 0;
            main.yinc = 0.01; // Speed
            main.goff = 0;
            main.ginc = 0;
            main.y = main.h * 0.65;
            main.length = main.w + 0;
            main.amp = 15; // Wave height
        };

        /*========================================
        Event
        ========================================*/

        main.events = () => {
            window.addEventListener('resize', main.reset.bind(undefined));
        };

        /*========================================
        Wave
        ========================================*/

        main.wave = (color, amp, height, comp) => {
            main.ctx.beginPath();

            const sway = main.simplex.noise2D(main.goff, 0) * amp;

            for (let i = 0; i <= main.count; i++) {
                main.xoff += main.xinc;

                const x = main.cx - main.length / 2 + main.length / main.count * i,
                    y = height + main.simplex.noise2D(main.xoff, main.yoff) * amp + sway;

                main.ctx[i === 0 ? 'moveTo' : 'lineTo'](x, y);
            }

            main.ctx.lineTo(main.w, -main.h); // -main.h - Vertically reflection
            main.ctx.lineTo(0, -main.h); // -main.h - Vertically reflection
            main.ctx.closePath();
            main.ctx.fillStyle = color;

            if (comp) {
                main.ctx.globalCompositeOperation = comp;
            }

            main.ctx.fill();
        };

        /*========================================
        Loop
        ========================================*/

        main.loop = () => {
            requestAnimationFrame(main.loop);

            main.ctx.clearRect(0, 0, main.w, main.h);
            main.xoff = 0;

            main.ctx.fillStyle = "#182645";
            main.ctx.fillRect(0, 0, main.w, main.h);

            main.wave("#fb0000", 20, main.h * .5, "screen");
            main.wave("#00ff8e", 20, main.h * .5, "screen");
            main.wave("#6F33FF", 20, main.h * .5, "screen");

            main.ctx.fillStyle = "#f1dfdd";
            main.ctx.globalCompositeOperation = "darken";
            main.ctx.fillRect(0, 0, main.w, main.h);

            main.yoff += main.yinc;
            main.goff += main.ginc;
        };

        /*========================================
        Start
        ========================================*/

        main.init();
    }
}