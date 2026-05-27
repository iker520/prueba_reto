function toggleBuceadorAdmin(cb) {
  const panel = document.getElementById('buceadorAdminPanel');
  if (cb.checked) {
    panel.classList.add('show');
  } else {
    panel.classList.remove('show');
    document.getElementById('seguroAdminPanel')?.classList.remove('show');
  }
}
function toggleSeguroAdmin(cb) {
  const panel = document.getElementById('seguroAdminPanel');
  if (cb.checked) { panel.classList.add('show'); }
  else { panel.classList.remove('show'); }
}
// Fecha mínima hoy para campos de vencimiento
document.addEventListener('DOMContentLoaded', function() {
  const cb = document.getElementById('esBuceador');
  if (cb && cb.checked) document.getElementById('buceadorAdminPanel')?.classList.add('show');
  const seg = document.getElementById('seguroAcc');
  if (seg && seg.checked) document.getElementById('seguroAdminPanel')?.classList.add('show');

  // Limitar fechas de vencimiento a partir de hoy
  const today = new Date().toISOString().split('T')[0];
  const vto = document.getElementById('fechaVtoAdmin');
  if (vto && !vto.value) vto.min = today;
  const certFin = document.getElementById('certFechaFinAdmin');
  if (certFin) certFin.min = today;
});
