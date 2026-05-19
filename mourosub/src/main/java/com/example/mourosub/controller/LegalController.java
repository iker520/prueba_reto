package com.example.mourosub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/condiciones-venta")
    public String condicionesVenta(Model model) {
        model.addAttribute("pageTitle", "Condiciones de Venta");
        return "policies/condiciones-venta";
    }

    @GetMapping("/devoluciones")
    public String devoluciones(Model model) {
        model.addAttribute("pageTitle", "Devoluciones");
        return "policies/devoluciones";
    }

    @GetMapping("/aviso-legal")
    public String avisoLegal(Model model) {
        model.addAttribute("pageTitle", "Aviso Legal");
        return "policies/aviso-legal";
    }

    @GetMapping("/politica-privacidad")
    public String politicaPrivacidad(Model model) {
        model.addAttribute("pageTitle", "Política de Privacidad");
        return "policies/politica-privacidad";
    }
}