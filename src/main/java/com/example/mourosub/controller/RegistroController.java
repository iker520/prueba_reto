package com.example.mourosub.controller;

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
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    public RegistroController(UsuarioService usuarioService,
                              CertificacionService certificacionService) {
        this.usuarioService = usuarioService;
        this.certificacionService = certificacionService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("pageTitle", "Crear cuenta");
        return "public/registro";
    }

    @PostMapping
    public String procesarRegistro(
            // Datos personales
            @RequestParam String dniUsuario,
            @RequestParam String nombre,
            @RequestParam String apellido1,
            @RequestParam(required = false) String apellido2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNac,
            @RequestParam String telefono,
            @RequestParam String tfnoSos,
            @RequestParam String email,
            @RequestParam String emailConfirmacion,
            @RequestParam String password,
            @RequestParam String passwordConfirmacion,
            // ¿Es buceador?
            @RequestParam(defaultValue = "false") boolean esBuceador,
            // Seguro (solo si buceador)
            @RequestParam(defaultValue = "false") boolean seguroAccidentes,
            @RequestParam(required = false) String companiaSeguros,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVto,
            // Certificación propia (solo si buceador)
            @RequestParam(required = false) String certTitulo,
            @RequestParam(required = false) String certEntidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaFin,
            @RequestParam(required = false) MultipartFile certDocumento,
            // Notificaciones
            @RequestParam(defaultValue = "false") boolean notifEmails,
            @RequestParam(defaultValue = "false") boolean notifWhatsapp,
            RedirectAttributes redirectAttrs,
            Model model) {

        boolean hasErrors = false;

        if (!email.equalsIgnoreCase(emailConfirmacion)) {
            model.addAttribute("errorEmail", "Los correos electrónicos no coinciden.");
            hasErrors = true;
        }
        if (!password.equals(passwordConfirmacion)) {
            model.addAttribute("errorPassword", "Las contraseñas no coinciden.");
            hasErrors = true;
        } else if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$")) {
            model.addAttribute("errorPassword", "La contraseña no cumple con los requisitos de seguridad.");
            hasErrors = true;
        }
        if (usuarioService.existsByEmail(email)) {
            model.addAttribute("errorEmailDuplicado", "Ya existe una cuenta con ese correo electrónico.");
            hasErrors = true;
        }
        if (usuarioService.findById(dniUsuario).isPresent()) {
            model.addAttribute("errorDni", "Ya existe un usuario registrado con ese DNI.");
            hasErrors = true;
        }

        if (hasErrors) {
            repopulate(model, dniUsuario, nombre, apellido1, apellido2, fechaNac,
                    telefono, tfnoSos, email, esBuceador, seguroAccidentes,
                    companiaSeguros, fechaVto, certTitulo, certEntidad,
                    certFechaInicio, certFechaFin, notifEmails, notifWhatsapp);
            return "public/registro";
        }

        // --- Crear usuario ---
        Usuario usuario = new Usuario();
        usuario.setDniUsuario(dniUsuario);
        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setFechaNac(fechaNac);
        usuario.setTelefono(telefono);
        usuario.setTfnoSos(tfnoSos);
        usuario.setEmail(email.toLowerCase().trim());
        usuario.setNotifEmails(notifEmails);
        usuario.setNotifWhatsapp(notifWhatsapp);
        usuario.setRol("ROLE_USER");
        usuario.setEsBuceador(esBuceador);

        if (esBuceador && seguroAccidentes) {
            usuario.setSeguroAccidentes(true);
            usuario.setCompaniaSeguros(companiaSeguros);
            usuario.setFechaVto(fechaVto);
            usuario.setEstadoSeguro("PENDIENTE"); // queda pendiente de revisión admin
        }

        usuarioService.create(usuario, password);

        // --- Certificación aportada por el buceador ---
        if (esBuceador && certTitulo != null && !certTitulo.isBlank()) {
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

            // Guardar el fichero adjunto si se adjuntó
            if (certDocumento != null && !certDocumento.isEmpty()) {
                try {
                    String docUrl = guardarFichero(certDocumento, dniUsuario);
                    cert.setDocumentoUrl(docUrl);
                } catch (IOException e) {
                    // no bloqueamos el registro si falla el fichero
                    System.err.println("⚠️ No se pudo guardar el fichero de certificación: " + e.getMessage());
                }
            }

            certificacionService.save(cert);
        }

        redirectAttrs.addFlashAttribute("registroExitoso",
                "¡Cuenta creada correctamente! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    // --- Helpers ---

    private String guardarFichero(MultipartFile file, String dniUsuario) throws IOException {
        Path directorio = Paths.get(uploadsPath, "certificaciones");
        Files.createDirectories(directorio);

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        String nombreFichero = dniUsuario + "_" + UUID.randomUUID() + extension;
        Path destino = directorio.resolve(nombreFichero);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/certificaciones/" + nombreFichero;
    }

    private void repopulate(Model model, String dniUsuario, String nombre, String apellido1,
                            String apellido2, LocalDate fechaNac, String telefono, String tfnoSos,
                            String email, boolean esBuceador, boolean seguroAccidentes,
                            String companiaSeguros, LocalDate fechaVto,
                            String certTitulo, String certEntidad,
                            LocalDate certFechaInicio, LocalDate certFechaFin,
                            boolean notifEmails, boolean notifWhatsapp) {
        model.addAttribute("pageTitle", "Crear cuenta");
        model.addAttribute("dniUsuario", dniUsuario);
        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido1", apellido1);
        model.addAttribute("apellido2", apellido2);
        model.addAttribute("fechaNac", fechaNac);
        model.addAttribute("telefono", telefono);
        model.addAttribute("tfnoSos", tfnoSos);
        model.addAttribute("email", email);
        model.addAttribute("esBuceador", esBuceador);
        model.addAttribute("seguroAccidentes", seguroAccidentes);
        model.addAttribute("companiaSeguros", companiaSeguros);
        model.addAttribute("fechaVto", fechaVto);
        model.addAttribute("certTitulo", certTitulo);
        model.addAttribute("certEntidad", certEntidad);
        model.addAttribute("certFechaInicio", certFechaInicio);
        model.addAttribute("certFechaFin", certFechaFin);
        model.addAttribute("notifEmails", notifEmails);
        model.addAttribute("notifWhatsapp", notifWhatsapp);
    }
}
