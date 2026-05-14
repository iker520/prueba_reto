package com.example.mourosub.controller;

import com.example.mourosub.service.InstructorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conocenos")
public class ConocenosController {

    private final InstructorService instructorService;

    public ConocenosController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public String conocenos(Model model) {
        model.addAttribute("instructores", instructorService.findAllActivos());
        model.addAttribute("pageTitle", "Conócenos");
        return "public/conocenos";
    }
}
