(function () {
  // Usar selectores por 'name' porque th:field sobreescribe el atributo 'id'
  const tipoSel    = document.querySelector('select[name="tipo"]');
  const subtipoSel = document.querySelector('select[name="subtipo"]');
  if (!tipoSel || !subtipoSel) return;

  function actualizarSubtipo() {
    const tipoVal = tipoSel.value.toUpperCase();
    document.querySelectorAll('.subtipo-grupo').forEach(og => {
      og.style.display = 'none';
      og.querySelectorAll('option').forEach(o => o.disabled = true);
    });
    if (tipoVal) {
      const grupo = document.querySelector('.subtipo-' + tipoVal);
      if (grupo) {
        grupo.style.display = '';
        grupo.querySelectorAll('option').forEach(o => o.disabled = false);
      }
    }
  }

  tipoSel.addEventListener('change', function () {
    subtipoSel.value = '';
    actualizarSubtipo();
  });

  // Al cargar la página (modo edición), mostrar el grupo correcto
  actualizarSubtipo();
})();
