document.addEventListener('DOMContentLoaded', function() {
  const today = new Date().toISOString().split('T')[0];
  const fin = document.getElementById('fechaFinMouro');
  if (fin) fin.min = today;
});
