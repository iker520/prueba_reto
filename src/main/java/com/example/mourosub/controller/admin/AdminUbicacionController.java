package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Ubicacion;
import com.example.mourosub.service.UbicacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/ubicaciones")
public class AdminUbicacionController {

    private final UbicacionService ubicacionService;

    public AdminUbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        model.addAttribute("pageTitle", "Gestión de Ubicaciones");
        return "admin/ubicaciones/list";
    }

    @GetMapping("/nueva")
    public String nuevaForm(Model model) {
        model.addAttribute("ubicacion", new Ubicacion());
        model.addAttribute("pageTitle", "Nueva Ubicación");
        model.addAttribute("accion", "Crear");
        return "admin/ubicaciones/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Ubicacion ubicacion = ubicacionService.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Ubicación no encontrada"));
        model.addAttribute("ubicacion", ubicacion);
        model.addAttribute("pageTitle", "Editar Ubicación");
        model.addAttribute("accion", "Actualizar");
        return "admin/ubicaciones/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Ubicacion ubicacion,
                          RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.save(ubicacion);
            redirectAttributes.addFlashAttribute("success", "Ubicación guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/ubicaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ubicacionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Ubicación eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/ubicaciones";
    }
}
