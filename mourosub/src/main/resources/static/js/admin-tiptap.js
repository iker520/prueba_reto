/**
 * MouroSub — Admin TipTap Editor
 * Integra TipTap vía CDN para campos de texto largo en el panel admin.
 * El contenido HTML se sincroniza con un <input type="hidden"> antes del submit.
 */

document.addEventListener('DOMContentLoaded', function () {

  // Inicializar todos los editores TipTap en la página
  document.querySelectorAll('[data-tiptap-target]').forEach(function (wrapper) {
    const targetId = wrapper.getAttribute('data-tiptap-target');
    const hiddenInput = document.getElementById(targetId);
    if (!hiddenInput) return;

    // Toolbar
    const toolbar = wrapper.querySelector('.tiptap-toolbar');
    const editorEl = wrapper.querySelector('#tiptap-editor, .tiptap-editor-area');

    if (!editorEl) return;

    // Inicializar editor con @tiptap/core desde CDN
    const { Editor } = window.tiptap || {};
    const { StarterKit } = window.StarterKit || {};

    if (!Editor || !StarterKit) {
      console.warn('TipTap no cargado. Asegúrate de incluir los scripts CDN.');
      return;
    }

    const editor = new Editor({
      element: editorEl,
      extensions: [StarterKit],
      content: hiddenInput.value || '',
      onUpdate({ editor }) {
        hiddenInput.value = editor.getHTML();
      },
    });

    // Conectar botones de la toolbar
    if (toolbar) {
      toolbar.querySelectorAll('button[data-action]').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
          e.preventDefault();
          const action = btn.getAttribute('data-action');
          switch (action) {
            case 'bold':       editor.chain().focus().toggleBold().run(); break;
            case 'italic':     editor.chain().focus().toggleItalic().run(); break;
            case 'h2':         editor.chain().focus().toggleHeading({ level: 2 }).run(); break;
            case 'h3':         editor.chain().focus().toggleHeading({ level: 3 }).run(); break;
            case 'bullet':     editor.chain().focus().toggleBulletList().run(); break;
            case 'ordered':    editor.chain().focus().toggleOrderedList().run(); break;
            case 'blockquote': editor.chain().focus().toggleBlockquote().run(); break;
            case 'undo':       editor.chain().focus().undo().run(); break;
            case 'redo':       editor.chain().focus().redo().run(); break;
          }
          // Actualizar estado activo de los botones
          updateToolbarState(editor, toolbar);
        });
      });

      editor.on('selectionUpdate', () => updateToolbarState(editor, toolbar));
      editor.on('update', () => updateToolbarState(editor, toolbar));
    }
  });

  function updateToolbarState(editor, toolbar) {
    toolbar.querySelectorAll('button[data-action]').forEach(function (btn) {
      const action = btn.getAttribute('data-action');
      let isActive = false;
      switch (action) {
        case 'bold':       isActive = editor.isActive('bold'); break;
        case 'italic':     isActive = editor.isActive('italic'); break;
        case 'h2':         isActive = editor.isActive('heading', { level: 2 }); break;
        case 'h3':         isActive = editor.isActive('heading', { level: 3 }); break;
        case 'bullet':     isActive = editor.isActive('bulletList'); break;
        case 'ordered':    isActive = editor.isActive('orderedList'); break;
        case 'blockquote': isActive = editor.isActive('blockquote'); break;
      }
      btn.classList.toggle('is-active', isActive);
    });
  }
});
