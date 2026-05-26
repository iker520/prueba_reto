package com.example.mourosub.config;

import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Inyecta en TODAS las vistas admin los conteos de pendientes para
 * mostrar los badges de la campanita en el topbar.
 */
@ControllerAdvice(basePackages = "com.example.mourosub.controller.admin")
public class AdminModelAdvice {

    private final CertificacionService certificacionService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;

    public AdminModelAdvice(CertificacionService certificacionService,
                            UsuarioService usuarioService,
                            ReservaService reservaService) {
        this.certificacionService = certificacionService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
    }

    @ModelAttribute("certsPendientes")
    public long certsPendientes() {
        return certificacionService.countPendientesRevision();
    }

    @ModelAttribute("segurosPendientes")
    public long segurosPendientes() {
        return usuarioService.countBuceadoresSeguroPendiente();
    }

    /** Total de pendientes de validación (certs + seguros) para la campanita principal */
    @ModelAttribute("pendientesValidacion")
    public long pendientesValidacion() {
        return certsPendientes() + segurosPendientes();
    }

    /** Reservas nuevas en estado PENDIENTE esperando gestión del admin */
    @ModelAttribute("reservasPendientesNuevas")
    public long reservasPendientesNuevas() {
        return reservaService.countByEstado("PENDIENTE");
    }
}
