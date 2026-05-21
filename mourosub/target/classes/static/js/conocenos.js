/* Extracted from public/conocenos.html */
(function () {
                  var el  = document.getElementById('masInstructores');
                  var btn = document.getElementById('btnVerMasTexto');
                  if (el && btn) {
                    var textoOriginal = btn.innerHTML;
                    el.addEventListener('show.bs.collapse', function () { btn.innerHTML = 'Ver menos'; });
                    el.addEventListener('hide.bs.collapse', function () { btn.innerHTML = textoOriginal; });
                  }
                })();


/* Extracted from public/conocenos.html */
(function () {
      'use strict';



      /* ── 2. CONTADORES ANIMADOS ─────────────────────────────── */
      function formatNum(val, fmt) {
        if (fmt === 'thousands') {
          return '+' + (val >= 1000 ? Math.floor(val/1000) + '.000' : val);
        }
        return val;
      }

      var statEls = document.querySelectorAll('.cn-stat-number[data-target]');
      var statsTriggered = false;

      function animateStats() {
        if (statsTriggered) return;
        statsTriggered = true;
        statEls.forEach(function (el) {
          var target   = parseInt(el.getAttribute('data-target'), 10);
          var prefix   = el.getAttribute('data-prefix')  || '';
          var suffix   = el.getAttribute('data-suffix')  || '';
          var fmt      = el.getAttribute('data-format')  || '';
          var duration = 1800;
          var start    = null;

          function step(ts) {
            if (!start) start = ts;
            var progress = Math.min((ts - start) / duration, 1);
            var ease     = 1 - Math.pow(1 - progress, 3); /* easeOutCubic */
            var current  = Math.floor(ease * target);
            if (fmt === 'thousands') {
              el.textContent = '+' + (current >= 1000 ? Math.floor(current/1000) + '.000' : current) + suffix;
            } else {
              el.textContent = prefix + current + suffix;
            }
            if (progress < 1) requestAnimationFrame(step);
          }
          requestAnimationFrame(step);
        });
      }

      var statsSection = document.querySelector('.cn-stats');
      if (statsSection) {
        var statsObs = new IntersectionObserver(function (entries) {
          if (entries[0].isIntersecting) {
            animateStats();
            statsObs.disconnect();
          }
        }, { threshold: 0.3 });
        statsObs.observe(statsSection);
      }

      /* ── 3. BARRAS DE PROGRESO ANIMADAS ────────────────────── */
      var progressBars = document.querySelectorAll('.cn-progress-bar[data-width]');

      var progressObs = new IntersectionObserver(function (entries) {
        entries.forEach(function (e) {
          if (e.isIntersecting) {
            e.target.style.width = e.target.getAttribute('data-width') + '%';
            progressObs.unobserve(e.target);
          }
        });
      }, { threshold: 0.4 });

      progressBars.forEach(function (bar) { progressObs.observe(bar); });

      /* ── 4. INSTRUCTOR BTN TEXTO ────────────────────────────── */
      var elInstr  = document.getElementById('masInstructores');
      var btnInstr = document.getElementById('btnVerMasTexto');
      if (elInstr && btnInstr) {
        var textoOriginal = btnInstr.innerHTML;
        elInstr.addEventListener('show.bs.collapse', function () { btnInstr.innerHTML = 'Ver menos'; });
        elInstr.addEventListener('hide.bs.collapse', function () { btnInstr.innerHTML = textoOriginal; });
      }

    })();
