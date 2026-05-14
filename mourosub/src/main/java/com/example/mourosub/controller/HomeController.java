package com.example.mourosub.controller;

import com.example.mourosub.service.ActividadService;
import com.example.mourosub.service.NoticiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ActividadService actividadService;
    private final NoticiaService noticiaService;

    public HomeController(ActividadService actividadService, NoticiaService noticiaService) {
        this.actividadService = actividadService;
        this.noticiaService = noticiaService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("actividadesDestacadas", actividadService.findDestacadas());
        model.addAttribute("ultimasNoticias", noticiaService.findUltimas3());
        model.addAttribute("pageTitle", "Inicio");
        return "public/index";
    }
}
