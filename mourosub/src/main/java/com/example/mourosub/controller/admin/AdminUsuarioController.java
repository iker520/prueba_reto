package com.example.mourosub.controller.admin;

import com.example.mourosub.model.Usuario;
import com.example.mourosub.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
        return "admin/usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(required = false) String rawPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            if (rawPassword != null && !rawPassword.isBlank()) {
                usuarioService.create(usuario, rawPassword);
            } else {
                usuarioService.save(usuario);
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
}
