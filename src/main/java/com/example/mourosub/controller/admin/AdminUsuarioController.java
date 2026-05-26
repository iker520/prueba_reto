package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    public AdminUsuarioController(UsuarioService usuarioService,
                                  CertificacionService certificacionService) {
        this.usuarioService = usuarioService;
        this.certificacionService = certificacionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        return "admin/usuarios/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", java.util.List.of("ROLE_USER", "ROLE_ADMIN"));
        model.addAttribute("niveles", com.example.mourosub.service.ActividadService.getNivelesDisponibles());
        model.addAttribute("pageTitle", "Nuevo Usuario");
        model.addAttribute("accion", "Crear");
        model.addAttribute("esNuevo", true);
        return "admin/usuarios/form";
    }

    @GetMapping("/editar/{dni}")
    public String editarForm(@PathVariable String dni, Model model) {
        Usuario usuario = usuarioService.findById(dni)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", java.util.List.of("ROLE_USER", "ROLE_ADMIN"));
        model.addAttribute("niveles", com.example.mourosub.service.ActividadService.getNivelesDisponibles());
        model.addAttribute("pageTitle", "Editar Usuario");
        model.addAttribute("accion", "Actualizar");
        model.addAttribute("esNuevo", false);
        model.addAttribute("certsPropias", certificacionService.findPropiasByUsuario(dni));
        return "admin/usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String rawPassword,
            // Certificación inicial (solo en creación con esBuceador)
            @RequestParam(required = false) String certTitulo,
            @RequestParam(required = false) String certEntidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaFin,
            @RequestParam(required = false) MultipartFile certDocumento,
            RedirectAttributes redirectAttributes) {
        try {
            if (rawPassword != null && !rawPassword.isBlank()) {
                usuarioService.create(usuario, rawPassword);
            } else {
                usuarioService.save(usuario);
            }

            // Si buceador y se aportó certificación inicial
            if (Boolean.TRUE.equals(usuario.getEsBuceador())
                    && certTitulo != null && !certTitulo.isBlank()) {
                Certificacion cert = new Certificacion();
                cert.setUsuario(usuario);
                cert.setTitulo(certTitulo);
                cert.setEntidadCertificadora(certEntidad);
                cert.setFechaInicio(certFechaInicio);
                cert.setFechaFin(certFechaFin);
                cert.setExpedidaPorMourosub(false);
                cert.setRevisada(false);
                cert.setValidada(false);
                cert.setFechaRegistro(LocalDateTime.now());
                if (certDocumento != null && !certDocumento.isEmpty()) {
                    try {
                        cert.setDocumentoUrl(guardarFichero(certDocumento, usuario.getDniUsuario()));
                    } catch (IOException e) {
                        System.err.println("⚠️ No se pudo guardar doc de cert: " + e.getMessage());
                    }
                }
                certificacionService.save(cert);
            }

            redirectAttributes.addFlashAttribute("success", "Usuario guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar/{dni}")
    public String eliminar(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(dni);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{dni}/aprobar-seguro")
    public String aprobarSeguro(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.aprobarSeguro(dni);
            redirectAttributes.addFlashAttribute("success", "Seguro de accidentes aprobado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{dni}/rechazar-seguro")
    public String rechazarSeguro(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.rechazarSeguro(dni);
            redirectAttributes.addFlashAttribute("success", "Seguro de accidentes rechazado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // --- Helper ---
    private String guardarFichero(MultipartFile file, String dniUsuario) throws IOException {
        Path directorio = Paths.get(uploadsPath, "certificaciones");
        Files.createDirectories(directorio);
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String nombreFichero = dniUsuario + "_cert_" + UUID.randomUUID() + extension;
        Path destino = directorio.resolve(nombreFichero);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/certificaciones/" + nombreFichero;
    }
}
