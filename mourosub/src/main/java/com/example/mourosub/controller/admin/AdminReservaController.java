package com.example.mourosub.controller.admin;

import com.example.mourosub.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;

    public AdminReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isBlank()) {
            model.addAttribute("reservas", reservaService.findByEstado(estado.toUpperCase()));
            model.addAttribute("estadoFiltro", estado.toUpperCase());
        } else {
            model.addAttribute("reservas", reservaService.findAll());
            model.addAttribute("estadoFiltro", "");
        }
        model.addAttribute("estados", ReservaService.getEstadosDisponibles());
        model.addAttribute("pageTitle", "Gestión de Reservas");
        return "admin/reservas/list";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var reserva = reservaService.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        model.addAttribute("estados", ReservaService.getEstadosDisponibles());
        model.addAttribute("pageTitle", "Detalle Reserva #" + id);
        return "admin/reservas/detalle";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado,
                                RedirectAttributes redirectAttributes) {
        try {
            reservaService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado a " + estado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/reservas/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Reserva eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/reservas";
    }
}
