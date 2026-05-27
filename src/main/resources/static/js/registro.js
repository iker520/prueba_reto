// Toggle panel buceador
function toggleBuceador(radio) {
  const panel = document.getElementById('buceadorPanel');
  if (radio.value === 'true') {
    panel.classList.add('show');
  } else {
    panel.classList.remove('show');
    // limpiar campos de seguro y cert
    ['companiaSeguros','fechaVto','certTitulo','certEntidad','certFechaInicio','certFechaFin'].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.value = '';
    });
    document.getElementById('seguroDetalles')?.classList.remove('show');
  }
}

// Toggle sub-panel de seguro
function toggleSeguro(radio) {
  const panel = document.getElementById('seguroDetalles');
  if (radio.value === 'true') {
    panel.classList.add('show');
  } else {
    panel.classList.remove('show');
    const c = document.getElementById('companiaSeguros');
    const f = document.getElementById('fechaVto');
    if (c) c.value = '';
    if (f) f.value = '';
  }
}

// Inicializar validaciones y estado al cargar el DOM
document.addEventListener('DOMContentLoaded', function() {
  // Validación en tiempo real: email
  const emailInput = document.getElementById('email');
  const emailConf  = document.getElementById('emailConfirmacion');
  const emailHint  = document.getElementById('emailMismatchHint');
  
  if(emailInput && emailConf && emailHint) {
      function checkEmail() {
        if (emailConf.value && emailInput.value.toLowerCase() !== emailConf.value.toLowerCase()) {
          emailHint.classList.remove('d-none');
        } else { emailHint.classList.add('d-none'); }
      }
      emailInput.addEventListener('input', checkEmail);
      emailConf.addEventListener('input', checkEmail);
  }

  // Validación en tiempo real: password
  const pwInput = document.getElementById('password');
  const pwConf  = document.getElementById('passwordConfirmacion');
  const pwHint  = document.getElementById('pwMismatchHint');
  
  if(pwInput && pwConf && pwHint) {
      function checkPassword() {
        if (pwConf.value && pwInput.value !== pwConf.value) {
          pwHint.classList.remove('d-none');
        } else { pwHint.classList.add('d-none'); }
      }
      pwInput.addEventListener('input', checkPassword);
      pwConf.addEventListener('input', checkPassword);
  }

  // Inicializar paneles al cargar (en caso de error + repopulación)
  const buceadorSi = document.getElementById('buceadorSi');
  if (buceadorSi && buceadorSi.checked) {
    document.getElementById('buceadorPanel')?.classList.add('show');
  }
  const seguroSi = document.getElementById('seguroSi');
  if (seguroSi && seguroSi.checked) {
    document.getElementById('seguroDetalles')?.classList.add('show');
  }
});
