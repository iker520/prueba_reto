package com.example.mourosub.controller.admin;

import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public AdminReservaController(ReservaService reservaService,
                                  UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    // ----------------------------------------------------------------
    // Lista de reservas con filtro por estado
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // Detalle de una reserva
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var reserva = reservaService.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        model.addAttribute("todosUsuarios", usuarioService.findAll());
        model.addAttribute("pageTitle", "Detalle Reserva #" + id);
        return "admin/reservas/detalle";
    }

    // ----------------------------------------------------------------
    // Botones de cambio de estado (uno por estado, sin desplegable)
    // ----------------------------------------------------------------
    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id, RedirectAttributes ra) {
        return cambiar(id, "CONFIRMADA", ra);
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes ra) {
        return cambiar(id, "CANCELADA", ra);
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id, RedirectAttributes ra) {
        return cambiar(id, "COMPLETADA", ra);
    }

    @PostMapping("/{id}/pendiente")
    public String pendiente(@PathVariable Long id, RedirectAttributes ra) {
        return cambiar(id, "PENDIENTE", ra);
    }

    // ----------------------------------------------------------------
    // POST /admin/reservas/{id}/realizar
    // ----------------------------------------------------------------
    @PostMapping("/{id}/realizar")
    public String realizar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.cambiarEstado(id, "REALIZADA");
            redirectAttributes.addFlashAttribute("success", "Reserva marcada como REALIZADA.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservas/" + id;
    }

    // ----------------------------------------------------------------
    // Programar Actividad (Ubicación y Fechas)
    // ----------------------------------------------------------------
    @PostMapping("/{idReserva}/actividad/{idActividad}/programar")
    public String programarActividad(@PathVariable Long idReserva,
                                     @PathVariable Long idActividad,
                                     @RequestParam Long idUbicacion,
                                     @RequestParam String fechaInicio,
                                     RedirectAttributes redirectAttributes) {
        try {
            reservaService.programarActividad(idReserva, idActividad, idUbicacion, java.time.LocalDateTime.parse(fechaInicio));
            redirectAttributes.addFlashAttribute("success", "Actividad programada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservas/" + idReserva;
    }

    // ----------------------------------------------------------------
    // Eliminar Programación
    // ----------------------------------------------------------------
    @PostMapping("/{idReserva}/programacion/{idProgramacion}/eliminar")
    public String eliminarProgramacion(@PathVariable Long idReserva,
                                       @PathVariable Long idProgramacion,
                                       RedirectAttributes redirectAttributes) {
        try {
            reservaService.eliminarProgramacion(idProgramacion);
            redirectAttributes.addFlashAttribute("success", "Programación eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservas/" + idReserva;
    }

    // ----------------------------------------------------------------
    // Cambiar usuario de una reserva
    // ----------------------------------------------------------------
    @PostMapping("/{idReserva}/cambiar-usuario")
    public String cambiarUsuario(@PathVariable Long idReserva,
                                 @RequestParam String dniActual,
                                 @RequestParam String dniNuevo,
                                 RedirectAttributes redirectAttributes) {
        try {
            reservaService.cambiarUsuarioReserva(idReserva, dniActual, dniNuevo);
            redirectAttributes.addFlashAttribute("success", "Usuario de la reserva actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar usuario: " + e.getMessage());
        }
        return "redirect:/admin/reservas/" + idReserva;
    }

    // ----------------------------------------------------------------
    // Eliminar
    // ----------------------------------------------------------------
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reservaService.deleteById(id);
            ra.addFlashAttribute("success", "Reserva eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/reservas";
    }

    // ----------------------------------------------------------------
    // Helper interno
    // ----------------------------------------------------------------
    private String cambiar(Long id, String estado, RedirectAttributes ra) {
        try {
            reservaService.cambiarEstado(id, estado);
            ra.addFlashAttribute("success", "Estado cambiado a " + estado + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/reservas/" + id;
    }
}
