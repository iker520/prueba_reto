package com.example.mourosub.controller;

import com.example.mourosub.repository.UbicacionRepository;
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
    private final UbicacionRepository ubicacionRepository;

    public ServiciosController(ActividadService actividadService, UbicacionRepository ubicacionRepository) {
        this.actividadService = actividadService;
        this.ubicacionRepository = ubicacionRepository;
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
            String tipoUpper = tipo.toUpperCase();
            model.addAttribute("tipoSeleccionado", tipoUpper);
            model.addAttribute("searchQuery", "");
            
            // Lógica de subcategorías
            if ("CURSO".equals(tipoUpper)) {
                subcategorias = List.of("Apnea", "Deportivos y Especialidad", "Formación Emergencia y Primeros Auxilios", "Técnicos RX y Profesionales");
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndSubtipo(tipoUpper, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoUpper));
                }
            } else if ("OFERTA".equals(tipoUpper)) {
                subcategorias = List.of("Formación", "Actividades", "Material");
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndSubtipo(tipoUpper, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoUpper));
                }
            } else if ("INMERSION".equals(tipoUpper)) {
                var ubicaciones = ubicacionRepository.findUbicacionesByActividadTipo(tipoUpper);
                model.addAttribute("ubicaciones", ubicaciones);
                if (sub != null && !sub.isBlank()) {
                    model.addAttribute("actividades", actividadService.findByTipoAndUbicacion(tipoUpper, sub));
                } else {
                    model.addAttribute("actividades", actividadService.findByTipo(tipoUpper));
                }
            } else {
                model.addAttribute("actividades", actividadService.findByTipo(tipoUpper));
            }
        } else {
            model.addAttribute("actividades", actividadService.findAllActivas());
            model.addAttribute("tipoSeleccionado", "");
            model.addAttribute("searchQuery", "");
        }
        
        model.addAttribute("subSeleccionada", sub != null ? sub : "");
        model.addAttribute("subcategorias", subcategorias);
        
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
