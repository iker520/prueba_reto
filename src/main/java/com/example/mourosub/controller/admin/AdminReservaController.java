package com.example.mourosub.controller.admin;

import com.example.mourosub.service.InstructorService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final InstructorService instructorService;

    public AdminReservaController(ReservaService reservaService,
                                  UsuarioService usuarioService,
                                  InstructorService instructorService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.instructorService = instructorService;
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
    public String detalle(@PathVariable Long id,
                          @RequestParam(required = false) String fechaInicioInstructor,
                          @RequestParam(required = false) String openModalIndex,
                          Model model) {
        var reserva = reservaService.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        model.addAttribute("todosUsuarios", usuarioService.findAll());
        model.addAttribute("todosInstructores", instructorService.findAll());
        model.addAttribute("openModalIndex", openModalIndex != null ? openModalIndex : "-1");

        // Si el admin ha introducido una fecha propuesta, calcular disponibilidad
        if (fechaInicioInstructor != null && !fechaInicioInstructor.isBlank()) {
            try {
                LocalDateTime fechaPropuesta = LocalDateTime.parse(fechaInicioInstructor);
                model.addAttribute("instructoresDisponibles",
                        reservaService.getInstructoresDisponibles(fechaPropuesta));
                model.addAttribute("fechaInicioInstructor", fechaInicioInstructor);
            } catch (Exception ignored) {
                model.addAttribute("instructoresDisponibles", instructorService.findAll());
            }
        } else {
            // Sin fecha propuesta → mostrar todos los instructores activos
            model.addAttribute("instructoresDisponibles", instructorService.findAllActivos());
        }

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
    // Asignar / Desasignar Instructor
    // ----------------------------------------------------------------
    @PostMapping("/{idReserva}/actividad/{idActividad}/asignar-instructor")
    public String asignarInstructor(@PathVariable Long idReserva,
                                    @PathVariable Long idActividad,
                                    @RequestParam String dniInstructor,
                                    @RequestParam String fechaInicio,
                                    RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            reservaService.asignarInstructor(idReserva, idActividad, dniInstructor, inicio);
            redirectAttributes.addFlashAttribute("success", "Instructor asignado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al asignar instructor: " + e.getMessage());
        }
        return "redirect:/admin/reservas/" + idReserva;
    }

    @PostMapping("/{idReserva}/actividad/{idActividad}/desasignar-instructor")
    public String desasignarInstructor(@PathVariable Long idReserva,
                                       @PathVariable Long idActividad,
                                       @RequestParam String dniInstructor,
                                       RedirectAttributes redirectAttributes) {
        try {
            reservaService.desasignarInstructor(idReserva, idActividad, dniInstructor);
            redirectAttributes.addFlashAttribute("success", "Instructor desasignado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
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
