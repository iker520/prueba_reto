package com.example.mourosub.controller;

import com.example.mourosub.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/servicios")
public class ServiciosController {

    private final ActividadService actividadService;

    public ServiciosController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public String servicios(@RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String q,
                            Model model) {
        if (q != null && !q.isBlank()) {
            model.addAttribute("actividades", actividadService.searchActivas(q));
            model.addAttribute("tipoSeleccionado", "");
            model.addAttribute("searchQuery", q);
        } else if (tipo != null && !tipo.isBlank()) {
            model.addAttribute("actividades", actividadService.findByTipo(tipo.toUpperCase()));
            model.addAttribute("tipoSeleccionado", tipo.toUpperCase());
            model.addAttribute("searchQuery", "");
        } else {
            model.addAttribute("actividades", actividadService.findAllActivas());
            model.addAttribute("tipoSeleccionado", "");
            model.addAttribute("searchQuery", "");
        }
        model.addAttribute("cursos",      actividadService.findByTipo("CURSO"));
        model.addAttribute("inmersiones", actividadService.findByTipo("INMERSION"));
        model.addAttribute("actividades2",actividadService.findByTipo("ACTIVIDAD"));
        model.addAttribute("pageTitle", "Servicios");
        return "public/servicios";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var actividad = actividadService.findById(id)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Actividad no encontrada"));
        model.addAttribute("actividad", actividad);
        model.addAttribute("pageTitle", actividad.getNombre());
        return "public/servicio-detalle";
    }
}
