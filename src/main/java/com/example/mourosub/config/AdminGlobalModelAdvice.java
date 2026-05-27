package com.example.mourosub.config;

import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.ContactoService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Inyecta automáticamente en todos los modelos de /admin/** los datos
 * necesarios para el sidebar y el topbar (campanita de notificaciones).
 */
@ControllerAdvice(basePackages = "com.example.mourosub.controller.admin")
public class AdminGlobalModelAdvice {

    private final CertificacionService certificacionService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;
    private final ContactoService contactoService;

    public AdminGlobalModelAdvice(CertificacionService certificacionService,
                                  UsuarioService usuarioService,
                                  ReservaService reservaService,
                                  ContactoService contactoService) {
        this.certificacionService = certificacionService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
        this.contactoService = contactoService;
    }

    @ModelAttribute("pendientesValidacion")
    public long pendientesValidacion() {
        return certificacionService.countPendientesRevision()
                + usuarioService.countBuceadoresSeguroPendiente();
    }

    @ModelAttribute("certsPendientes")
    public long certsPendientes() {
        return certificacionService.countPendientesRevision();
    }

    @ModelAttribute("segurosPendientes")
    public long segurosPendientes() {
        return usuarioService.countBuceadoresSeguroPendiente();
    }

    @ModelAttribute("reservasPendientesNuevas")
    public long reservasPendientesNuevas() {
        return reservaService.countByEstado("PENDIENTE");
    }

    @ModelAttribute("contactosNuevos")
    public long contactosNuevos() {
        return contactoService.countNuevas();
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
