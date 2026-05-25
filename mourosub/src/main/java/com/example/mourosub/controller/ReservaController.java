package com.example.mourosub.controller;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.ActividadService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el flujo público de reserva de actividades.
 * Requiere autenticación — Spring Security redirige automáticamente al login.
 */
@Controller
@RequestMapping("/reservar")
public class ReservaController {

    private final ActividadService actividadService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public ReservaController(ActividadService actividadService,
                             ReservaService reservaService,
                             UsuarioService usuarioService) {
        this.actividadService = actividadService;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /reservar/{idActividad}/add
     * Añade una actividad al carrito de la sesión y redirige al carrito.
     */
    @GetMapping("/{idActividad}/add")
    public String añadirAlCarrito(@PathVariable Long idActividad, jakarta.servlet.http.HttpSession session) {
        List<Long> carrito = (List<Long>) session.getAttribute("carritoActividades");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        if (!carrito.contains(idActividad)) {
            carrito.add(idActividad);
        }
        session.setAttribute("carritoActividades", carrito);
        return "redirect:/reservar/carrito";
    }

    /**
     * GET /reservar/{idActividad}/remove
     * Elimina una actividad del carrito.
     */
    @GetMapping("/{idActividad}/remove")
    public String quitarDelCarrito(@PathVariable Long idActividad, jakarta.servlet.http.HttpSession session) {
        List<Long> carrito = (List<Long>) session.getAttribute("carritoActividades");
        if (carrito != null) {
            carrito.remove(idActividad);
            session.setAttribute("carritoActividades", carrito);
        }
        return "redirect:/reservar/carrito";
    }

    /**
     * GET /reservar/carrito
     * Muestra las actividades actualmente en el carrito.
     */
    @GetMapping("/carrito")
    public String verCarrito(jakarta.servlet.http.HttpSession session,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        List<Long> carrito = (List<Long>) session.getAttribute("carritoActividades");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        List<Actividad> actividadesCarrito = new ArrayList<>();
        for (Long id : carrito) {
            actividadService.findById(id).ifPresent(actividadesCarrito::add);
        }

        Usuario usuario = resolverUsuario(userDetails);

        model.addAttribute("actividadesCarrito", actividadesCarrito);
        model.addAttribute("usuario", usuario);
        model.addAttribute("pageTitle", "Mi Reserva");
        return "public/reservar";
    }

    /**
     * POST /reservar/carrito
     * Confirma la reserva con todas las actividades del carrito.
     */
    @PostMapping("/carrito")
    public String procesarReservaCarrito(@RequestParam(required = false) String notas,
                                         jakarta.servlet.http.HttpSession session,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         RedirectAttributes redirectAttributes) {

        try {
            List<Long> carrito = (List<Long>) session.getAttribute("carritoActividades");
            if (carrito == null || carrito.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No hay actividades en tu reserva.");
                return "redirect:/servicios";
            }

            Usuario usuario = resolverUsuario(userDetails);

            List<Actividad> actividades = new ArrayList<>();
            for (Long id : carrito) {
                actividadService.findById(id).ifPresent(actividades::add);
            }

            reservaService.crearReserva(usuario, actividades, notas);

            // Vaciar carrito
            session.removeAttribute("carritoActividades");

            redirectAttributes.addFlashAttribute("success",
                    "¡Solicitud de reserva enviada! El equipo de MouroSub se pondrá en contacto contigo para confirmar la programación.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo procesar la reserva: " + e.getMessage());
            return "redirect:/reservar/carrito";
        }

        return "redirect:/mi-cuenta#mis-reservas";
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------
    private Usuario resolverUsuario(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return usuarioService.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para email: " + email));
    }
}
