/* Extracted from layout/base.html */
/* Scroll effect: glassmorphism al bajar */
    (function () {
        var nav = document.getElementById('mainNav');
        function onScroll() {
            if (window.scrollY > 40) { nav.classList.add('ms-navbar--scrolled'); }
            else                     { nav.classList.remove('ms-navbar--scrolled'); }
        }
        window.addEventListener('scroll', onScroll, { passive: true });
    })();

    /* SCROLL REVEAL GLOBAL (Intersection Observer) */
    (function () {
      var revealClasses = ['.cn-reveal', '.cn-reveal-left', '.cn-reveal-right'];
      var allReveal = document.querySelectorAll(revealClasses.join(','));

      if (allReveal.length > 0) {
          var revealObs = new IntersectionObserver(function (entries) {
            entries.forEach(function (e) {
              if (e.isIntersecting) {
                e.target.classList.add('cn-visible');
                revealObs.unobserve(e.target);
              }
            });
          }, { threshold: 0.12 });

          allReveal.forEach(function (el) { revealObs.observe(el); });
      }
    })();
