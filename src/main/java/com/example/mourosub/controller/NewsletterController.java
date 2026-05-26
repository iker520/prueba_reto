package com.example.mourosub.controller;

import com.example.mourosub.model.Newsletter;
import com.example.mourosub.repository.NewsletterRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/newsletter")
public class NewsletterController {

    private final NewsletterRepository repository;

    public NewsletterController(NewsletterRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/suscribirse")
    public String suscribirse(@RequestParam String email) {

        if (!repository.existsByEmail(email)) {
            Newsletter newsletter = new Newsletter();
            newsletter.setEmail(email);
            repository.save(newsletter);
        }

        return "redirect:/noticias?success";
    }
}