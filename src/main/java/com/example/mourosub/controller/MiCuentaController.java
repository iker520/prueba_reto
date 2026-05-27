package com.example.mourosub.controller;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Reserva;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.CertificacionService;
import com.example.mourosub.service.ReservaService;
import com.example.mourosub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
@RequestMapping("/mi-cuenta")
public class MiCuentaController {

    private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";

    private final UsuarioService usuarioService;
    private final CertificacionService certificacionService;
    private final ReservaService reservaService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.uploads.path}")
    private String uploadsPath;

    public MiCuentaController(UsuarioService usuarioService,
                              CertificacionService certificacionService,
                              ReservaService reservaService,
                              BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.certificacionService = certificacionService;
        this.reservaService = reservaService;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Helper para obtener el usuario autenticado ─────────────────────────────
    private Usuario getUsuarioAutenticado(UserDetails userDetails) {
        return usuarioService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ── GET: pantalla principal ────────────────────────────────────────────────
    @GetMapping
    public String miCuenta(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = getUsuarioAutenticado(userDetails);

        List<Certificacion> certsPropia = certificacionService.findPropiasByUsuario(usuario.getDniUsuario());
        List<Certificacion> certsMouro  = certificacionService.findMouroSubByUsuario(usuario.getDniUsuario());
        List<Reserva>       misReservas = reservaService.findByDniUsuario(usuario.getDniUsuario());

        model.addAttribute("usuario",     usuario);
        model.addAttribute("certsPropia", certsPropia);
        model.addAttribute("certsMouro",  certsMouro);
        model.addAttribute("misReservas", misReservas);
        model.addAttribute("pageTitle",   "Mi Cuenta");
        return "public/mi-cuenta";
    }

    // ── POST: editar datos personales ──────────────────────────────────────────
    @PostMapping("/editar-perfil")
    public String editarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nombre,
            @RequestParam(required = false) String apellido1,
            @RequestParam(required = false) String apellido2,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNac,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String tfnoSos,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String codigoPostal,
            @RequestParam(required = false) String pais,
            @RequestParam(defaultValue = "false") boolean notifEmails,
            @RequestParam(defaultValue = "false") boolean notifWhatsapp,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = getUsuarioAutenticado(userDetails);

        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setFechaNac(fechaNac);
        usuario.setTelefono(telefono);
        usuario.setTfnoSos(tfnoSos);
        usuario.setDireccion(direccion);
        usuario.setCiudad(ciudad);
        usuario.setProvincia(provincia);
        usuario.setCodigoPostal(codigoPostal);
        usuario.setPais(pais);
        usuario.setNotifEmails(notifEmails);
        usuario.setNotifWhatsapp(notifWhatsapp);

        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("success", "Datos personales actualizados correctamente.");
        return "redirect:/mi-cuenta";
    }

    // ── POST: cambiar contraseña ───────────────────────────────────────────────
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordNuevaConfirmacion,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = getUsuarioAutenticado(userDetails);

        // 1. Verificar que la contraseña actual es correcta
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("errorPassword", "La contraseña actual no es correcta.");
            return "redirect:/mi-cuenta";
        }
        // 2. Verificar que la nueva contraseña y su confirmación coinciden
        if (!passwordNueva.equals(passwordNuevaConfirmacion)) {
            redirectAttributes.addFlashAttribute("errorPassword", "Las nuevas contraseñas no coinciden.");
            return "redirect:/mi-cuenta";
        }
        // 3. Validar requisitos de seguridad de la nueva contraseña
        if (!passwordNueva.matches(PASSWORD_PATTERN)) {
            redirectAttributes.addFlashAttribute("errorPassword",
                    "La nueva contraseña debe tener mínimo 8 caracteres, mayúsculas, minúsculas, número y símbolo especial.");
            return "redirect:/mi-cuenta";
        }

        usuarioService.updatePassword(usuario.getDniUsuario(), passwordNueva);
        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        return "redirect:/mi-cuenta";
    }

    // ── POST: gestionar perfil de buceador ────────────────────────────────────
    @PostMapping("/perfil-buceador")
    public String perfilBuceador(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "false") boolean esBuceador,
            @RequestParam(required = false) String nivelBuceo,
            @RequestParam(required = false) Integer numInmersiones,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaUltimaInmersion,
            @RequestParam(defaultValue = "false") boolean seguroAccidentes,
            @RequestParam(required = false) String companiaSeguros,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVto,
            @RequestParam(required = false) String certTitulo,
            @RequestParam(required = false) String certEntidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certFechaFin,
            @RequestParam(required = false) MultipartFile certDocumento,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = getUsuarioAutenticado(userDetails);

        boolean eraBuceador = Boolean.TRUE.equals(usuario.getEsBuceador());

        usuario.setEsBuceador(esBuceador);

        if (esBuceador) {
            usuario.setNivelBuceo(nivelBuceo);
            usuario.setNumInmersiones(numInmersiones != null ? numInmersiones : 0);
            usuario.setFechaUltimaInmersion(fechaUltimaInmersion);
            
            if (seguroAccidentes) {
                usuario.setSeguroAccidentes(true);
                usuario.setCompaniaSeguros(companiaSeguros);
                usuario.setFechaVto(fechaVto);
                // Si es la primera vez que declara seguro, ponerlo en PENDIENTE
                if (usuario.getEstadoSeguro() == null || (!eraBuceador)) {
                    usuario.setEstadoSeguro("PENDIENTE");
                }
            } else {
                usuario.setSeguroAccidentes(false);
                usuario.setCompaniaSeguros(null);
                usuario.setFechaVto(null);
            }
        } else {
            // Si NO es buceador, limpiar todos los datos de buceo para evitar que se guarden por error
            usuario.setNivelBuceo(null);
            usuario.setNumInmersiones(0);
            usuario.setFechaUltimaInmersion(null);
            usuario.setSeguroAccidentes(false);
            usuario.setCompaniaSeguros(null);
            usuario.setFechaVto(null);
        }

        usuarioService.save(usuario);
        
        // Procesar la certificación opcional si se sube un documento
        if (esBuceador && certDocumento != null && !certDocumento.isEmpty()) {
            try {
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

                Path directorio = Paths.get(uploadsPath, "certificaciones");
                Files.createDirectories(directorio);
                String extension = "";
                String originalName = certDocumento.getOriginalFilename();
                if (originalName != null && originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf('.'));
                }
                String nombreFichero = usuario.getDniUsuario() + "_cert_" + UUID.randomUUID() + extension;
                Path destino = directorio.resolve(nombreFichero);
                Files.copy(certDocumento.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                cert.setDocumentoUrl("/uploads/certificaciones/" + nombreFichero);
                
                certificacionService.save(cert);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Perfil guardado, pero falló la subida de la certificación: " + e.getMessage());
                return "redirect:/mi-cuenta";
            }
        }

        redirectAttributes.addFlashAttribute("success",
                esBuceador ? "Perfil de buceador actualizado correctamente." : "Perfil actualizado.");
        return "redirect:/mi-cuenta";
    }

    // ── POST: subir certificación propia ──────────────────────────────────────
    @PostMapping("/certificacion/subir")
    public String subirCertificacion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String titulo,
            @RequestParam String entidad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam MultipartFile documento,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = getUsuarioAutenticado(userDetails);

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

    // ── GET/POST: Gestión de Reservas PENDIENTES ──────────────────────────────
    @GetMapping("/reserva/{id}/editar")
    public String editarReserva(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            jakarta.servlet.http.HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuario = getUsuarioAutenticado(userDetails);
        
        return reservaService.findById(id).map(r -> {
            // Verificar pertenencia y estado
            if (!r.getUsuarios().stream().anyMatch(ur -> ur.getDniUsuario().equals(usuario.getDniUsuario()))) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta reserva.");
                return "redirect:/mi-cuenta";
            }
            if (!"PENDIENTE".equals(r.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "Solo puedes editar reservas PENDIENTES.");
                return "redirect:/mi-cuenta";
            }
            
            // Pasar actividades al carrito
            List<Long> carrito = new java.util.ArrayList<>();
            r.getActividades().forEach(ar -> carrito.add(ar.getIdActividad()));
            
            session.setAttribute("carritoActividades", carrito);
            session.setAttribute("reservaEnEdicion", id); // Para que ReservaController actualice en vez de crear
            
            redirectAttributes.addFlashAttribute("success", "Estás editando la reserva #" + id + ". Haz los cambios que necesites y pulsa Confirmar.");
            return "redirect:/reservar/carrito";
            
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Reserva no encontrada.");
            return "redirect:/mi-cuenta";
        });
    }

    @PostMapping("/reserva/{id}/cancelar")
    public String cancelarReserva(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuario = getUsuarioAutenticado(userDetails);
        
        reservaService.findById(id).ifPresent(r -> {
            if (r.getUsuarios().stream().anyMatch(ur -> ur.getDniUsuario().equals(usuario.getDniUsuario()))) {
                if ("PENDIENTE".equals(r.getEstado())) {
                    reservaService.cambiarEstado(id, "CANCELADA");
                    redirectAttributes.addFlashAttribute("success", "Reserva #" + id + " cancelada correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "No se puede cancelar una reserva que no está PENDIENTE.");
                }
            }
        });
        
        return "redirect:/mi-cuenta";
    }
}
