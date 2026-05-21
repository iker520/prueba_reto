package com.example.mourosub.controller;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mi-cuenta")
public class MiCuentaController {

    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;

    public MiCuentaController(UsuarioService usuarioService,
                              CertificacionService certificacionService) {
        this.usuarioService = usuarioService;
        this.certificacionService = certificacionService;
    }

    @GetMapping
    public String miCuenta(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();

        // Buscar el usuario por su email (que es el username en Spring Security)
        Usuario usuario = usuarioService.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Certificaciones propias (aportadas por el usuario)
        List<Certificacion> certsPropia = certificacionService.findPropiasByUsuario(usuario.getDniUsuario());

        // Certificaciones emitidas por MouroSub
        List<Certificacion> certsMouro = certificacionService.findMouroSubByUsuario(usuario.getDniUsuario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("certsPropia", certsPropia);
        model.addAttribute("certsMouro", certsMouro);
        model.addAttribute("pageTitle", "Mi Cuenta");
        return "public/mi-cuenta";
    }
}
