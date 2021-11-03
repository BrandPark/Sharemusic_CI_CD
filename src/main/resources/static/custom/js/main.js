$(function () {
    $("body").prepend("<canvas style='position:absolute;'></canvas>");
    $('textarea.auto-size').on('keydown', function () {
        if ($(this).height() < 96) {
            $(this).height(1).height($(this).prop('scrollHeight') + 12);
        }
    });

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
});