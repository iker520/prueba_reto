package com.example.mourosub.config;

import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Inyecta en TODAS las vistas admin el conteo de pendientes de validación
 * para mostrar el badge de la campanita en el topbar.
 */
@ControllerAdvice(basePackages = "com.example.mourosub.controller.admin")
public class AdminModelAdvice {

    private final CertificacionService certificacionService;
    private final UsuarioService usuarioService;

    public AdminModelAdvice(CertificacionService certificacionService,
                            UsuarioService usuarioService) {
        this.certificacionService = certificacionService;
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("pendientesValidacion")
    public long pendientesValidacion() {
        return certificacionService.countPendientesRevision()
                + usuarioService.countBuceadoresSeguroPendiente();
    }
}
