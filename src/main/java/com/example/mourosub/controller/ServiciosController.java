package com.example.mourosub.controller;

import com.example.mourosub.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/servicios")
public class ServiciosController {

    private final ActividadService actividadService;

    public ServiciosController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public String servicios(@RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String sub,
                            @RequestParam(required = false) String q,
                            Model model) {
        
        List<String> subcategorias = List.of();
        
        if (q != null && !q.isBlank()) {
            model.addAttribute("actividades", actividadService.searchActivas(q));
            model.addAttribute("tipoSeleccionado", "");
            model.addAttribute("searchQuery", q);
        } else if (tipo != null && !tipo.isBlank()) {
            String tipoLower = tipo.toLowerCase();
            String tipoUpper = tipo.toUpperCase(); // Para la vista (plantilla HTML)
            model.addAttribute("tipoSeleccionado", tipoUpper);
            model.addAttribute("searchQuery", "");
            
            // Lógica de subcategorías
            if ("curso".equals(tipoLower)) {
                subcategorias = List.of("Apnea", "Deportivos y Especialidad", "Formación Emergencia y Primeros Auxilios", "Técnico RX", "Profesionales");
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndSubtipo(tipoLower, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoLower));
                }
            } else if ("oferta".equals(tipoLower)) {
                subcategorias = List.of("Formación", "Actividades", "Material");
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndSubtipo(tipoLower, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoLower));
                }
            } else if ("inmersion".equals(tipoLower)) {
                subcategorias = List.of("Isla de Mouro", "El Palacio", "Isla de Santa Marina",
                        "Cabo Menor y Cabo Mayor", "Bajos y Cabezos", "Pecios");
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndSubtipo(tipoLower, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoLower));
                }
            } else {
                model.addAttribute("actividades", actividadService.findByTipo(tipoLower));
            }
        } else {
            model.addAttribute("actividades", actividadService.findAllActivas());
            model.addAttribute("tipoSeleccionado", "");
            model.addAttribute("searchQuery", "");
        }
        
        model.addAttribute("subSeleccionada", sub != null ? sub : "");
        model.addAttribute("subcategorias", subcategorias);
        
        model.addAttribute("cursos",      actividadService.findByTipo("curso"));
        model.addAttribute("inmersiones", actividadService.findByTipo("inmersion"));
        model.addAttribute("actividades2",actividadService.findByTipo("actividad"));
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
