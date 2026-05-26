package com.example.mourosub.controller;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.model.Contacto;
import com.example.mourosub.service.ActividadService;
import com.example.mourosub.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;
    private final ActividadService actividadService;

    public ContactoController(ContactoService contactoService,
                              ActividadService actividadService) {
        this.contactoService = contactoService;
        this.actividadService = actividadService;
    }

    // GET /contacto → muestra el formulario
    @GetMapping
    public String contacto(Model model) {
        model.addAttribute("pageTitle", "Contacto");

        // Actividades activas desde el servicio
        List<Actividad> actividades = actividadService.findAllActivas();

        // Sacamos solo tipos únicos
        List<String> tiposUnicos = actividades.stream()
                .map(Actividad::getTipo)
                .distinct()
                .toList();

        model.addAttribute("tipos", tiposUnicos);

        return "public/contacto";
    }

    // POST /contacto → procesa el formulario
    @PostMapping
    public String enviarMensaje(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String interes,
            @RequestParam String mensaje,
            RedirectAttributes redirectAttributes) {

        Contacto contacto = new Contacto();
        contacto.setNombre(nombre);
        contacto.setEmail(email);
        contacto.setInteres(interes);
        contacto.setMensaje(mensaje);

        contactoService.guardar(contacto);

        redirectAttributes.addFlashAttribute("mensajeOk",
                "¡Gracias " + nombre + "! Tu mensaje ha sido recibido. Te contactaremos pronto.");

        return "redirect:/contacto";
    }
}
