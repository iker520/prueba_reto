/* Extracted from public/index.html */
(function () {
            /* ── 1. Scroll-driven sequential reveal ── */
            var revealEls = document.querySelectorAll('.ms-cn-reveal');
            var revealObs = new IntersectionObserver(function(entries) {
                entries.forEach(function(entry) {
                    if (entry.isIntersecting) {
                        entry.target.classList.add('ms-cn-visible');
                        revealObs.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.15 });
            revealEls.forEach(function(el) { revealObs.observe(el); });

            /* ── 2. Contador animado ── */
            function formatNum(val, thousands, suffix) {
                var s = Math.round(val).toString();
                if (thousands) {
                    s = s.replace(/\B(?=(\d{3})+(?!\d))/g, thousands);
                }
                return s + (suffix || '');
            }

            function runCounters() {
                var counters = document.querySelectorAll('.ms-cn-counter');
                counters.forEach(function(el) {
                    var target   = parseInt(el.dataset.target, 10);
                    var prefix   = el.dataset.prefix  || '';
                    var suffix   = el.dataset.suffix  || '';
                    var thousands= el.dataset.thousands|| '';
                    var duration = 1600; // ms
                    var start    = performance.now();

                    function step(now) {
                        var elapsed  = now - start;
                        var progress = Math.min(elapsed / duration, 1);
                        // ease-out cubic
                        var ease = 1 - Math.pow(1 - progress, 3);
                        el.textContent = prefix + formatNum(ease * target, thousands, suffix);
                        if (progress < 1) requestAnimationFrame(step);
                    }
                    requestAnimationFrame(step);
                });
            }

            /* Lanza contadores cuando el bloque de stats entra en pantalla */
            var statsBlock = document.getElementById('msCnStats');
            var counted = false;
            var statsObs = new IntersectionObserver(function(entries) {
                if (entries[0].isIntersecting && !counted) {
                    counted = true;
                    runCounters();
                    statsObs.disconnect();
                }
            }, { threshold: 0.4 });
            if (statsBlock) statsObs.observe(statsBlock);
        })();


/* Extracted from public/index.html */
(function() {
            function initBalls() {
                var section = document.getElementById('msCta');
                var canvas  = document.getElementById('msCtaCanvas');
                if (!section || !canvas) return;

                var ctx = canvas.getContext('2d');
                var bubbles = [];
                var mouseX = -9999, mouseY = -9999;

                function setSize() {
                    var r = section.getBoundingClientRect();
                    canvas.width  = r.width  || section.offsetWidth;
                    canvas.height = r.height || section.offsetHeight;
                }
                setSize();
                window.addEventListener('resize', function() { setSize(); createBubbles(); });

                function Bubble() {
                    this.ox  = Math.random() * canvas.width;
                    this.oy  = Math.random() * canvas.height;
                    this.x   = this.ox;
                    this.y   = this.oy;
                    this.vx  = 0;
                    this.vy  = 0;
                    this.r   = 6 + Math.random() * 22;
                    /* deriva ascendente natural de las burbujas */
                    this.driftY  = -(0.08 + Math.random() * 0.18);
                    this.driftX  = (Math.random() - 0.5) * 0.06;
                    /* opacidad aleatoria para profundidad visual */
                    this.opacity = 0.15 + Math.random() * 0.25;
                }

                Bubble.prototype.update = function() {
                    /* Repulsión del ratón */
                    var dx = this.x - mouseX;
                    var dy = this.y - mouseY;
                    var d  = Math.sqrt(dx*dx + dy*dy) || 1;
                    if (d < 120) {
                        var f = (120 - d) / 120 * 9;
                        this.vx += (dx / d) * f;
                        this.vy += (dy / d) * f;
                    }

                    /* Retorno a posición original */
                    this.vx += (this.ox - this.x) * 0.04;
                    this.vy += (this.oy - this.y) * 0.04;

                    /* Deriva marina suave (burbujea hacia arriba) */
                    this.vx += this.driftX;
                    this.vy += this.driftY;

                    /* Fricción */
                    this.vx *= 0.87;
                    this.vy *= 0.87;

                    this.x += this.vx;
                    this.y += this.vy;

                    /* Wrap vertical: si sube demasiado reaparece abajo */
                    if (this.y + this.r < 0) {
                        this.y  = canvas.height + this.r;
                        this.oy = this.y;
                        this.ox = Math.random() * canvas.width;
                        this.x  = this.ox;
                    }
                };

                Bubble.prototype.draw = function() {
                    ctx.save();

                    /* Burbuja principal: círculo con borde translúcido */
                    ctx.beginPath();
                    ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2);
                    ctx.strokeStyle = 'rgba(180,230,255,' + (this.opacity + 0.3) + ')';
                    ctx.lineWidth = 1.2;
                    ctx.stroke();

                    /* Relleno interior muy sutil */
                    var grad = ctx.createRadialGradient(
                        this.x - this.r * 0.3, this.y - this.r * 0.35, this.r * 0.05,
                        this.x, this.y, this.r
                    );
                    grad.addColorStop(0,   'rgba(220,245,255,' + (this.opacity + 0.2) + ')');
                    grad.addColorStop(0.5, 'rgba(150,210,240,' + this.opacity + ')');
                    grad.addColorStop(1,   'rgba(100,180,220,0)');
                    ctx.fillStyle = grad;
                    ctx.fill();

                    /* Reflejo de luz (pequeño arco blanco arriba-izquierda) */
                    ctx.beginPath();
                    ctx.arc(
                        this.x - this.r * 0.28,
                        this.y - this.r * 0.28,
                        this.r * 0.22,
                        0, Math.PI * 2
                    );
                    ctx.fillStyle = 'rgba(255,255,255,' + (this.opacity * 1.8) + ')';
                    ctx.fill();

                    ctx.restore();
                };

                function createBubbles() {
                    bubbles = [];
                    var count = Math.floor((canvas.width * canvas.height) / 9000);
                    count = Math.max(25, Math.min(count, 70));
                    for (var i = 0; i < count; i++) {
                        var b = new Bubble();
                        /* distribuir en alturas variadas desde el inicio */
                        b.y  = Math.random() * canvas.height;
                        b.oy = b.y;
                        bubbles.push(b);
                    }
                }
                createBubbles();

                function frame() {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    for (var i = 0; i < bubbles.length; i++) {
                        bubbles[i].update();
                        bubbles[i].draw();
                    }
                    requestAnimationFrame(frame);
                }
                frame();

                section.addEventListener('mousemove', function(e) {
                    var r = section.getBoundingClientRect();
                    mouseX = e.clientX - r.left;
                    mouseY = e.clientY - r.top;
                });
                section.addEventListener('mouseleave', function() {
                    mouseX = -9999; mouseY = -9999;
                });

                /* Touch */
                section.addEventListener('touchmove', function(e) {
                    var r = section.getBoundingClientRect();
                    mouseX = e.touches[0].clientX - r.left;
                    mouseY = e.touches[0].clientY - r.top;
                }, { passive: true });
                section.addEventListener('touchend', function() {
                    mouseX = -9999; mouseY = -9999;
                });
            }

            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', initBalls);
            } else {
                initBalls();
            }
        })();


/* Extracted from public/index.html */
(function () {
        /**
         * Carrusel infinito con animación CSS (marquee-style).
         * - Auto-scroll suave vía @keyframes: velocidad proporcional al nº de tarjetas.
         * - Pausa al hacer hover y al pulsar los botones manuales.
         * - Botones prev/next: desplazan una tarjeta y reanudan el auto-scroll.
         * - Touch swipe compatible.
         */
        function initCarousel(trackId, prevId, nextId) {
            const track = document.getElementById(trackId);
            if (!track) return;

            const CARD_W = 320;   // debe coincidir con el CSS
            const GAP    = 24;
            const STEP   = CARD_W + GAP;

            // Mitad del total de tarjetas = conjunto original
            const totalCards = track.children.length;
            const half       = totalCards / 2;

            if (half < 1) return;

            // ── Animación CSS automática ──────────────────────────────────────
            // Velocidad: ~4 s por tarjeta, con un mínimo razonable
            const duration = Math.max(half * 4, 16);   // segundos
            const halfPx   = half * STEP;               // px que recorre un ciclo

            // Inyectamos el @keyframes dinámicamente (valor exacto en px)
            const styleId = 'ms-carousel-kf-' + trackId;
            if (!document.getElementById(styleId)) {
                const s = document.createElement('style');
                s.id = styleId;
                s.textContent =
                    '@keyframes ms-scroll-' + trackId + '{' +
                    '  from { transform: translateX(0); }' +
                    '  to   { transform: translateX(-' + halfPx + 'px); }' +
                    '}';
                document.head.appendChild(s);
            }

            // Aplicamos la animación
            function startCssAnim() {
                track.style.transition = 'none';
                track.style.animation  =
                    'ms-scroll-' + trackId + ' ' + duration + 's linear infinite';
            }

            function pauseCssAnim()  { track.style.animationPlayState = 'paused'; }
            function resumeCssAnim() { track.style.animationPlayState = 'running'; }

            startCssAnim();

            // ── Pausa al hover ────────────────────────────────────────────────
            const wrapper = track.closest('.ms-carousel-wrapper');
            wrapper.addEventListener('mouseenter', pauseCssAnim);
            wrapper.addEventListener('mouseleave', resumeCssAnim);

            // ── Botones manuales ──────────────────────────────────────────────
            // Al pulsar un botón, capturamos la posición actual, desactivamos la
            // animación CSS, hacemos el desplazamiento manual con transition y
            // luego reanudamos la animación CSS desde esa posición.
            function getCurrentX() {
                const mat = getComputedStyle(track).transform;
                if (!mat || mat === 'none') return 0;
                const m = new DOMMatrix(mat);
                return m.m41;   // translateX actual
            }

            function jumpBy(delta) {
                pauseCssAnim();

                const cur = getCurrentX();
                // Detenemos la anim CSS y fijamos posición actual
                track.style.animation  = 'none';
                track.style.transform  = 'translateX(' + cur + 'px)';

                // Forzamos reflow para que la transition arranque
                void track.offsetWidth;

                let target = cur - delta;
                // Wrap: si salimos del rango [-halfPx, 0] reubicamos
                if (target > 0)          target -= halfPx;
                if (target < -halfPx)    target += halfPx;

                track.style.transition = 'transform 0.45s cubic-bezier(0.25,0.46,0.45,0.94)';
                track.style.transform  = 'translateX(' + target + 'px)';

                // Tras la transición, reanudamos la animación CSS desde el punto correcto
                track.addEventListener('transitionend', function resume() {
                    track.removeEventListener('transitionend', resume);
                    // Calcular el porcentaje de progreso para que la animación
                    // no salte: delay negativo equivale a "ya lleva X segundos"
                    const pct      = Math.abs(target) / halfPx;   // 0-1
                    const delay    = -(pct * duration);
                    track.style.transition = 'none';
                    track.style.transform  = '';
                    track.style.animation  =
                        'ms-scroll-' + trackId + ' ' + duration + 's linear ' + delay + 's infinite';
                    track.style.animationPlayState = 'running';
                }, { once: true });
            }

            const prevBtn = document.getElementById(prevId);
            const nextBtn = document.getElementById(nextId);
            if (nextBtn) nextBtn.addEventListener('click', function () { jumpBy(STEP); });
            if (prevBtn) prevBtn.addEventListener('click', function () { jumpBy(-STEP); });

            // ── Touch swipe ───────────────────────────────────────────────────
            let touchStartX = 0;
            track.addEventListener('touchstart', function (e) {
                touchStartX = e.touches[0].clientX;
                pauseCssAnim();
            }, { passive: true });
            track.addEventListener('touchend', function (e) {
                const dx = touchStartX - e.changedTouches[0].clientX;
                if (Math.abs(dx) > 50) {
                    jumpBy(dx > 0 ? STEP : -STEP);
                } else {
                    resumeCssAnim();
                }
            }, { passive: true });
        }

        document.addEventListener('DOMContentLoaded', function () {
            initCarousel('carouselTrack',         'carouselPrev',         'carouselNext');
            initCarousel('carouselTrackFallback', 'carouselPrevFallback', 'carouselNextFallback');
        });
    })();
    document.addEventListener("DOMContentLoaded", () => {

    const counters = document.querySelectorAll(".ms-counter");
    let activated = false;

    // Formato con sufijos
    const formatNumber = (value, target) => {
        if (target >= 1000) {
            return Math.round(value / 100) / 10 + "k+";
        }
        return value + "+";
    };

    // Velocidad SUPER lenta
    const getSpeed = (target) => {
        if (target <= 20) return 0.2;     // 15 → extremadamente lento
        if (target <= 100) return 0.8;    // 50 → lento
        return 10;                        // 2000 → rápido pero visible
    };

    const animateCounters = () => {
        counters.forEach(counter => {

            const label = counter.getAttribute("data-label");
            if (label) {
                counter.textContent = label;
                return;
            }

            const target = +counter.getAttribute("data-target");
            let current = 0;
            const speed = getSpeed(target);

            const update = () => {
                current += speed;
                if (current >= target) current = target;

                counter.textContent = formatNumber(Math.floor(current), target);

                if (current < target) {
                    requestAnimationFrame(update);
                }
            };

            update();
        });
    };

    // Activar animación JUSTO cuando la sección entra en pantalla
    const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && !activated) {
            activated = true;
            animateCounters();
        }
    }, { threshold: 0.6 });

    observer.observe(document.querySelector("#ms-stats-section"));
});
