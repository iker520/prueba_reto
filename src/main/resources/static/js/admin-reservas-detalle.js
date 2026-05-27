document.addEventListener('DOMContentLoaded', function () {
  // Mover todos los modales al <body> (evita problemas con overflow:auto)
  document.querySelectorAll('.modal').forEach(function (modal) {
    document.body.appendChild(modal);
  });

  // Si la URL tiene ?fechaInicioInstructor y ?openModalIndex, abrir el modal automáticamente
  const params = new URLSearchParams(window.location.search);
  if (params.has('fechaInicioInstructor') && params.has('openModalIndex')) {
    const modalIndex = params.get('openModalIndex');
    const modal = document.getElementById('modalAsignarInstructor-' + modalIndex);
    if (modal) {
      const bsModal = new bootstrap.Modal(modal);
      bsModal.show();
    }
  }

  // Al cambiar la fecha de inicio en el modal, recargar con la fecha como param y el index del modal
  document.querySelectorAll('.input-fecha-instructor').forEach(function (input) {
    input.addEventListener('change', function () {
      if (this.value) {
        const index = this.getAttribute('data-modal-index');
        const url = new URL(window.location.href);
        url.searchParams.set('fechaInicioInstructor', this.value);
        url.searchParams.set('openModalIndex', index);
        window.location.href = url.toString();
      }
    });
  });
});
