package com.example.mourosub.controller.admin;

import com.example.mourosub.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ActividadService actividadService;
    private final InstructorService instructorService;
    private final NoticiaService noticiaService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public AdminDashboardController(ActividadService actividadService,
                                    InstructorService instructorService,
                                    NoticiaService noticiaService,
                                    ReservaService reservaService,
                                    UsuarioService usuarioService) {
        this.actividadService = actividadService;
        this.instructorService = instructorService;
        this.noticiaService = noticiaService;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("totalActividades", actividadService.count());
        model.addAttribute("totalInstructores", instructorService.count());
        model.addAttribute("totalNoticias", noticiaService.count());
        model.addAttribute("totalReservas", reservaService.count());
        model.addAttribute("totalUsuarios", usuarioService.count());
        model.addAttribute("reservasPendientes", reservaService.countByEstado("PENDIENTE"));
        model.addAttribute("reservasConfirmadas", reservaService.countByEstado("CONFIRMADA"));
        model.addAttribute("ultimasReservas", reservaService.findAll().stream().limit(5).toList());
        model.addAttribute("pageTitle", "Panel de Administración");
        return "admin/dashboard";
    }
}
