package com.example.mourosub.controller;

import com.example.mourosub.service.NoticiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/noticias")
public class NoticiasController {

    private final NoticiaService noticiaService;

    public NoticiasController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping
    public String noticias(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String hashtag,
            Model model) {

        if (hashtag != null && !hashtag.isBlank()) {
            model.addAttribute("noticias",
                    noticiaService.findByHashtag(hashtag));
            model.addAttribute("filtroActivo", "#" + hashtag);

        } else if (categoria != null && !categoria.isBlank()) {
            model.addAttribute("noticias",
                    noticiaService.findByCategoria(categoria));
            model.addAttribute("categoriaSeleccionada", categoria);

        } else {
            model.addAttribute("noticias",
                    noticiaService.findAllPublicadas());
            model.addAttribute("categoriaSeleccionada", "");
        }

        model.addAttribute("categorias", NoticiaService.getCategoriasDisponibles());
        model.addAttribute("hashtagsPopulares", noticiaService.getHashtagsPopulares(10));
        model.addAttribute("pageTitle", "Noticias");

        return "public/noticias";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var noticia = noticiaService.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Noticia no encontrada"));

        if (!noticia.getPublicada()) {
            return "redirect:/noticias";
        }

        model.addAttribute("noticia", noticia);
        model.addAttribute("ultimasNoticias", noticiaService.findUltimas3());
        model.addAttribute("pageTitle", noticia.getTitulo());

        return "public/noticia-detalle";

    }
}