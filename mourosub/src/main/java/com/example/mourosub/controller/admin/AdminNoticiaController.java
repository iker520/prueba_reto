package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Noticia;
import com.example.mourosub.service.NoticiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/noticias")
public class AdminNoticiaController {

    private final NoticiaService noticiaService;

    public AdminNoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("noticias", noticiaService.findAll());
        model.addAttribute("pageTitle", "Gestión de Noticias");
        return "admin/noticias/list";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("noticia", new Noticia());
        model.addAttribute("categorias", NoticiaService.getCategoriasDisponibles());
        model.addAttribute("pageTitle", "Nueva Noticia");
        model.addAttribute("accion", "Publicar");
        return "admin/noticias/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Noticia noticia = noticiaService.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Noticia no encontrada"));
        model.addAttribute("noticia", noticia);
        model.addAttribute("categorias", NoticiaService.getCategoriasDisponibles());
        model.addAttribute("pageTitle", "Editar Noticia");
        model.addAttribute("accion", "Actualizar");
        return "admin/noticias/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Noticia noticia,
                          RedirectAttributes redirectAttributes) {
        try {
            noticiaService.save(noticia);
            redirectAttributes.addFlashAttribute("success", "Noticia guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/noticias";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticiaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Noticia eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/noticias";
    }
}
