package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/actividades")
public class AdminActividadController {

    private final ActividadService actividadService;

    public AdminActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("actividades", actividadService.findAll());
        model.addAttribute("pageTitle", "Gestión de Actividades");
        return "admin/actividades/list";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("actividad", new Actividad());
        model.addAttribute("tipos", ActividadService.getTiposDisponibles());
        model.addAttribute("niveles", ActividadService.getNivelesDisponibles());
        model.addAttribute("pageTitle", "Nueva Actividad");
        model.addAttribute("accion", "Crear");
        return "admin/actividades/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Actividad actividad = actividadService.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Actividad no encontrada"));
        model.addAttribute("actividad", actividad);
        model.addAttribute("tipos", ActividadService.getTiposDisponibles());
        model.addAttribute("niveles", ActividadService.getNivelesDisponibles());
        model.addAttribute("pageTitle", "Editar Actividad");
        model.addAttribute("accion", "Actualizar");
        return "admin/actividades/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Actividad actividad,
                          RedirectAttributes redirectAttributes) {
        try {
            actividadService.save(actividad);
            redirectAttributes.addFlashAttribute("success", "Actividad guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/actividades";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            actividadService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Actividad eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/actividades";
    }
}
