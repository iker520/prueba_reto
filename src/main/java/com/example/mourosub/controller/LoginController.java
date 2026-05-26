package com.example.mourosub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Iniciar Sesión");
        return "public/login";
    }

    @GetMapping("/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("pageTitle", "Acceso Denegado");
        return "error/403";
    }
}
