package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.model.Ubicacion;
import com.example.mourosub.service.ActividadService;
import com.example.mourosub.service.UbicacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/actividades")
public class AdminActividadController {

    private final ActividadService actividadService;
    private final UbicacionService ubicacionService;

    public AdminActividadController(ActividadService actividadService,
                                    UbicacionService ubicacionService) {
        this.actividadService = actividadService;
        this.ubicacionService = ubicacionService;
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
        model.addAttribute("todasUbicaciones", ubicacionService.findAll());
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
        model.addAttribute("todasUbicaciones", ubicacionService.findAll());
        model.addAttribute("pageTitle", "Editar Actividad");
        model.addAttribute("accion", "Actualizar");
        return "admin/actividades/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Actividad actividad,
                          @RequestParam(value = "ubicacionIds", required = false) List<Long> ubicacionIds,
                          RedirectAttributes redirectAttributes) {
        try {
            // Asignar las ubicaciones seleccionadas
            if (ubicacionIds != null && !ubicacionIds.isEmpty()) {
                Set<Ubicacion> ubicaciones = new HashSet<>(ubicacionService.findAllById(ubicacionIds));
                actividad.setUbicaciones(ubicaciones);
            } else {
                actividad.setUbicaciones(Collections.emptySet());
            }
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
