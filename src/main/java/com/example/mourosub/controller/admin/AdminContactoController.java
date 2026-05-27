package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Contacto;
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
        model.addAttribute("totalNuevos", contactoService.countNuevas());
        model.addAttribute("pageTitle", "Contactos");
        return "admin/contactos/list";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Contacto contacto = contactoService.findById(id).orElse(null);
        if (contacto == null) {
            redirectAttributes.addFlashAttribute("error", "Mensaje no encontrado.");
            return "redirect:/admin/contactos";
        }
        // Marcar automáticamente como LEIDA si estaba NUEVA
        if ("NUEVA".equals(contacto.getEstado())) {
            contactoService.cambiarEstado(id, "LEIDA");
            contacto.setEstado("LEIDA");
        }
        model.addAttribute("contacto", contacto);
        model.addAttribute("estados", ContactoService.getEstadosDisponibles());
        model.addAttribute("pageTitle", "Mensaje de " + contacto.getNombre());
        return "admin/contactos/detalle";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado,
                                @RequestParam(required = false) String origen,
                                RedirectAttributes redirectAttributes) {
        try {
            contactoService.cambiarEstado(id, estado.toUpperCase());
            redirectAttributes.addFlashAttribute("success", "Estado actualizado a " + estado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        // Si viene del detalle, volver al detalle; si no, al listado
        if ("detalle".equals(origen)) {
            return "redirect:/admin/contactos/" + id;
        }
        return "redirect:/admin/contactos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Mensaje eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/contactos";
    }
}
