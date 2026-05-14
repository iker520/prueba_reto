package com.example.mourosub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    @GetMapping
    public String contacto(Model model) {
        model.addAttribute("pageTitle", "Contacto");
        return "public/contacto";
    }

    @PostMapping
    public String enviarMensaje(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String interes,
            @RequestParam String mensaje,
            RedirectAttributes redirectAttributes) {
        // Aquí se podría integrar un servicio de email (JavaMailSender)
        // Por ahora registramos la recepción y redirigimos con mensaje de éxito
        redirectAttributes.addFlashAttribute("mensajeOk",
            "¡Gracias " + nombre + "! Tu mensaje ha sido recibido. Te contactaremos pronto.");
        return "redirect:/contacto";
    }
}
