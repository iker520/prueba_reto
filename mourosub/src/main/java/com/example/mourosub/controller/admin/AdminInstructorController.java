package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Instructor;
import com.example.mourosub.service.InstructorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/instructores")
public class AdminInstructorController {

    private final InstructorService instructorService;

    public AdminInstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("instructores", instructorService.findAll());
        model.addAttribute("pageTitle", "Gestión de Instructores");
        return "admin/instructores/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("instructor", new Instructor());
        model.addAttribute("titulos", InstructorService.getTitulosDisponibles());
        model.addAttribute("pageTitle", "Nuevo Instructor");
        model.addAttribute("accion", "Crear");
        return "admin/instructores/form";
    }

    @GetMapping("/editar/{dni}")
    public String editarForm(@PathVariable String dni, Model model) {
        Instructor instructor = instructorService.findById(dni)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Instructor no encontrado"));
        model.addAttribute("instructor", instructor);
        model.addAttribute("titulos", InstructorService.getTitulosDisponibles());
        model.addAttribute("pageTitle", "Editar Instructor");
        model.addAttribute("accion", "Actualizar");
        return "admin/instructores/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Instructor instructor,
                          RedirectAttributes redirectAttributes) {
        try {
            instructorService.save(instructor);
            redirectAttributes.addFlashAttribute("success", "Instructor guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/instructores";
    }

    @PostMapping("/eliminar/{dni}")
    public String eliminar(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        try {
            instructorService.deleteById(dni);
            redirectAttributes.addFlashAttribute("success", "Instructor eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/instructores";
    }
}
