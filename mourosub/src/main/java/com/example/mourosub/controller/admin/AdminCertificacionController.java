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
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/certificaciones")
public class AdminCertificacionController {

    private final CertificacionService certificacionService;
    private final UsuarioService usuarioService;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    public AdminCertificacionController(CertificacionService certificacionService,
                                        UsuarioService usuarioService) {
        this.certificacionService = certificacionService;
        this.usuarioService = usuarioService;
    }

    // -------------------------------------------------------
    // Lista todas las certificaciones de usuarios (no Mouro)
    // -------------------------------------------------------
    @GetMapping
    public String lista(Model model) {
        List<Certificacion> pendientes = certificacionService.findPendientesRevision();
        List<Certificacion> revisadas  = certificacionService.findAll().stream()
                .filter(c -> Boolean.FALSE.equals(c.getExpedidaPorMourosub()) && Boolean.TRUE.equals(c.getRevisada()))
                .toList();

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("revisadas", revisadas);
        model.addAttribute("pageTitle", "Certificaciones de Usuarios");
        return "admin/certificaciones/lista";
    }

    // -------------------------------------------------------
    // Aprobar una certificación de usuario
    // -------------------------------------------------------
    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id,
                          @RequestParam(required = false) String notasAdmin,
                          RedirectAttributes redirectAttrs) {
        boolean ok = certificacionService.revisar(id, true, notasAdmin);
        if (ok) {
            redirectAttrs.addFlashAttribute("success", "Certificación aprobada correctamente.");
        } else {
            redirectAttrs.addFlashAttribute("error", "No se encontró la certificación.");
        }
        return "redirect:/admin/certificaciones";
    }

    // -------------------------------------------------------
    // Rechazar una certificación de usuario
    // -------------------------------------------------------
    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam(required = false) String notasAdmin,
                           RedirectAttributes redirectAttrs) {
        boolean ok = certificacionService.revisar(id, false, notasAdmin);
        if (ok) {
            redirectAttrs.addFlashAttribute("success", "Certificación rechazada.");
        } else {
            redirectAttrs.addFlashAttribute("error", "No se encontró la certificación.");
        }
        return "redirect:/admin/certificaciones";
    }

    // -------------------------------------------------------
    // Lista de certificaciones expedidas por MouroSub
    // -------------------------------------------------------
    @GetMapping("/mouro")
    public String listaMouro(Model model) {
        model.addAttribute("certs", certificacionService.findAllMouroSub());
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("pageTitle", "Certificaciones MouroSub");
        return "admin/certificaciones/mouro-lista";
    }

    // -------------------------------------------------------
    // Formulario nueva certificación MouroSub
    // -------------------------------------------------------
    @GetMapping("/mouro/nueva")
    public String nuevaMouroForm(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("pageTitle", "Nueva Certificación MouroSub");
        return "admin/certificaciones/mouro-form";
    }

    // -------------------------------------------------------
    // Guardar nueva certificación MouroSub
    // -------------------------------------------------------
    @PostMapping("/mouro/nueva")
    public String nuevaMouro(@RequestParam String dniUsuario,
                             @RequestParam String titulo,
                             @RequestParam(required = false) String entidadCertificadora,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                             @RequestParam(required = false) MultipartFile documento,
                             RedirectAttributes redirectAttrs) {

        Certificacion cert = new Certificacion();
        cert.setTitulo(titulo);
        cert.setEntidadCertificadora(entidadCertificadora != null ? entidadCertificadora : "MouroSub");
        cert.setFechaInicio(fechaInicio);
        cert.setFechaFin(fechaFin);

        if (documento != null && !documento.isEmpty()) {
            try {
                String docUrl = guardarFichero(documento, dniUsuario);
                cert.setDocumentoUrl(docUrl);
            } catch (IOException e) {
                redirectAttrs.addFlashAttribute("error", "Error guardando el documento: " + e.getMessage());
                return "redirect:/admin/certificaciones/mouro/nueva";
            }
        }

        certificacionService.crearCertificacionMouro(dniUsuario, cert);
        redirectAttrs.addFlashAttribute("success", "Certificación expedida correctamente al usuario.");
        return "redirect:/admin/certificaciones/mouro";
    }

    // -------------------------------------------------------
    // Eliminar una cert Mouro
    // -------------------------------------------------------
    @PostMapping("/mouro/{id}/eliminar")
    public String eliminarMouro(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        certificacionService.deleteById(id);
        redirectAttrs.addFlashAttribute("success", "Certificación eliminada.");
        return "redirect:/admin/certificaciones/mouro";
    }

    // -------------------------------------------------------
    // Aprobar / Rechazar seguro de un buceador
    // -------------------------------------------------------
    @PostMapping("/seguro/{dni}/aprobar")
    public String aprobarSeguro(@PathVariable String dni, RedirectAttributes redirectAttrs) {
        usuarioService.aprobarSeguro(dni);
        redirectAttrs.addFlashAttribute("success", "Seguro del usuario aprobado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/seguro/{dni}/rechazar")
    public String rechazarSeguro(@PathVariable String dni, RedirectAttributes redirectAttrs) {
        usuarioService.rechazarSeguro(dni);
        redirectAttrs.addFlashAttribute("success", "Seguro del usuario rechazado.");
        return "redirect:/admin/usuarios";
    }

    // -------------------------------------------------------
    // Helper: guardar fichero en local
    // -------------------------------------------------------
    private String guardarFichero(MultipartFile file, String dniUsuario) throws IOException {
        Path directorio = Paths.get(uploadsPath, "certificaciones");
        Files.createDirectories(directorio);

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        String nombreFichero = dniUsuario + "_mouro_" + UUID.randomUUID() + extension;
        Path destino = directorio.resolve(nombreFichero);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/certificaciones/" + nombreFichero;
    }
}
