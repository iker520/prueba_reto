// Limitar fecha de fin de cert a partir de hoy
document.addEventListener('DOMContentLoaded', function() {
  const today = new Date().toISOString().split('T')[0];
  const certFin = document.getElementById('certFechaFinPublic');
  if (certFin) certFin.min = today;
});
