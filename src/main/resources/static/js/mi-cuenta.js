// Limitar fecha de fin de cert a partir de hoy
document.addEventListener('DOMContentLoaded', function() {
  const today = new Date().toISOString().split('T')[0];
  const certFin = document.getElementById('certFechaFinPublic');
  if (certFin) certFin.min = today;

  // --- Validación en tiempo real: nueva contraseña ---
  const pwNueva = document.getElementById('passwordNueva');
  const pwConf  = document.getElementById('passwordNuevaConf');
  const pwHint  = document.getElementById('pwNuevaHint');
  const pwMatchHint = document.getElementById('pwMatchHint');

  const PATRON = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}$/;

  if (pwNueva && pwHint) {
    pwNueva.addEventListener('input', function() {
      if (this.value && !PATRON.test(this.value)) {
        pwHint.classList.remove('d-none');
      } else {
        pwHint.classList.add('d-none');
      }
      checkMatch();
    });
  }

  if (pwConf) {
    pwConf.addEventListener('input', checkMatch);
  }

  function checkMatch() {
    if (!pwConf || !pwMatchHint) return;
    if (pwConf.value && pwNueva && pwConf.value !== pwNueva.value) {
      pwMatchHint.classList.remove('d-none');
    } else {
      pwMatchHint.classList.add('d-none');
    }
  }

  // --- Toggle seguro en modal buceador ---
  const segSi = document.getElementById('modalSeguroSi');
  const segNo = document.getElementById('modalSeguroNo');
  const segPanel = document.getElementById('modalSeguroPanel');

  function toggleModalSeguro() {
    if (!segPanel) return;
    if (segSi && segSi.checked) {
      segPanel.classList.add('show');
    } else {
      segPanel.classList.remove('show');
    }
  }
  if (segSi) segSi.addEventListener('change', toggleModalSeguro);
  if (segNo) segNo.addEventListener('change', toggleModalSeguro);
  toggleModalSeguro();

  // Abrir modal contraseña si hay error de contraseña
  const errorPw = document.getElementById('errorPasswordFlag');
  if (errorPw) {
    const modal = document.getElementById('modalCambiarPassword');
    if (modal) new bootstrap.Modal(modal).show();
  }

  // Abrir modal perfil si hay success y venía del modal buceador
  // (no es necesario, redirect limpia el estado)

  // --- Validación del Formulario de Perfil Buceador ---
  const formBuceador = document.getElementById('formPerfilBuceador');
  const checkBuceador = document.getElementById('modalEsBuceador');
  
  if (formBuceador && checkBuceador) {
    formBuceador.addEventListener('submit', function(e) {
      if (!checkBuceador.checked) {
        // Verificar si hay campos rellenados que implican que sí es buceador
        const nivel = document.querySelector('select[name="nivelBuceo"]').value;
        const inmersiones = document.querySelector('input[name="numInmersiones"]').value;
        const ultimaInm = document.querySelector('input[name="fechaUltimaInmersion"]').value;
        const segChecked = document.getElementById('modalSeguroSi') && document.getElementById('modalSeguroSi').checked;
        const certTitulo = document.querySelector('input[name="certTitulo"]').value;
        const errorMsg = document.getElementById('errorBuceadorMsg');

        if (nivel !== '' || parseInt(inmersiones) > 0 || ultimaInm !== '' || segChecked || certTitulo !== '') {
          e.preventDefault();
          e.stopPropagation();
          if (errorMsg) {
            errorMsg.classList.remove('d-none');
            // Scroll to top of modal to see the message
            errorMsg.scrollIntoView({ behavior: 'smooth', block: 'start' });
          }
          return false;
        } else {
          if (errorMsg) errorMsg.classList.add('d-none');
        }
      }
    });
  }
});
