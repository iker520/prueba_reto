package com.example.mourosub.controller;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Reserva;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/mi-cuenta")
public class MiCuentaController {

    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;
    private final ReservaService reservaService;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    public MiCuentaController(UsuarioService usuarioService,
                              CertificacionService certificacionService,
                              ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.certificacionService = certificacionService;
        this.reservaService = reservaService;
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

        // Reservas del usuario
        List<Reserva> misReservas = reservaService.findByDniUsuario(usuario.getDniUsuario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("certsPropia", certsPropia);
        model.addAttribute("certsMouro", certsMouro);
        model.addAttribute("misReservas", misReservas);
        model.addAttribute("pageTitle", "Mi Cuenta");
        return "public/mi-cuenta";
    }

    @PostMapping("/certificacion/subir")
    public String subirCertificacion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String titulo,
            @RequestParam String entidad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam MultipartFile documento,
            RedirectAttributes redirectAttributes) {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            Certificacion cert = new Certificacion();
            cert.setUsuario(usuario);
            cert.setTitulo(titulo);
            cert.setEntidadCertificadora(entidad);
            cert.setFechaInicio(fechaInicio);
            cert.setFechaFin(fechaFin);
            cert.setExpedidaPorMourosub(false);
            cert.setRevisada(false);
            cert.setValidada(false);
            cert.setFechaRegistro(LocalDateTime.now());

            if (documento != null && !documento.isEmpty()) {
                Path directorio = Paths.get(uploadsPath, "certificaciones");
                Files.createDirectories(directorio);
                String extension = "";
                String originalName = documento.getOriginalFilename();
                if (originalName != null && originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf('.'));
                }
                String nombreFichero = usuario.getDniUsuario() + "_cert_" + UUID.randomUUID() + extension;
                Path destino = directorio.resolve(nombreFichero);
                Files.copy(documento.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                cert.setDocumentoUrl("/uploads/certificaciones/" + nombreFichero);
            }

            certificacionService.save(cert);
            redirectAttributes.addFlashAttribute("success", "Certificación añadida y pendiente de validación.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el documento adjunto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la certificación: " + e.getMessage());
        }

        return "redirect:/mi-cuenta";
    }
}
