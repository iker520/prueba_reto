package com.example.mourosub.controller.admin;

import com.example.mourosub.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/contactos")
public class AdminContactoController {

    private final ContactoService contactoService;

    public AdminContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isBlank()) {
            model.addAttribute("contactos", contactoService.findByEstado(estado.toUpperCase()));
            model.addAttribute("estadoFiltro", estado.toUpperCase());
        } else {
            model.addAttribute("contactos", contactoService.findAll());
            model.addAttribute("estadoFiltro", "");
        }
        model.addAttribute("estados", ContactoService.getEstadosDisponibles());
        model.addAttribute("pageTitle", "Contactos de Contacto");
        return "admin/contactos/list";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado,
                                RedirectAttributes redirectAttributes) {
        try {
            contactoService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado a " + estado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/contactos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Contacto eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/contactos";
    }
}
