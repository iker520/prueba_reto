package com.example.mourosub.config;

import com.example.mourosub.model.*;
import com.example.mourosub.repository.*;
import com.example.mourosub.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Inicializa datos de prueba en la base de datos si está vacía.
 * Se ejecuta una sola vez al arrancar la aplicación.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final InstructorRepository instructorRepository;
    private final ActividadRepository actividadRepository;
    private final NoticiaRepository noticiaRepository;
    private final UbicacionRepository ubicacionRepository;

    public DataInitializer(UsuarioService usuarioService,
                           InstructorRepository instructorRepository,
                           ActividadRepository actividadRepository,
                           NoticiaRepository noticiaRepository,
                           UbicacionRepository ubicacionRepository) {
        this.usuarioService      = usuarioService;
        this.instructorRepository = instructorRepository;
        this.actividadRepository  = actividadRepository;
        this.noticiaRepository    = noticiaRepository;
        this.ubicacionRepository  = ubicacionRepository;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        initUbicaciones();
        initInstructores();
        initActividades();
        initNoticias();
    }

    // ---------------------------------------------------------------
    // Admin User
    // ---------------------------------------------------------------
    private void initAdmin() {
        if (usuarioService.existsByEmail("admin@mourosub.com")) return;

        Usuario admin = new Usuario();
        admin.setDniUsuario("00000000A");
        admin.setNombre("Administrador");
        admin.setApellido1("MouroSub");
        admin.setEmail("admin@mourosub.com");
        admin.setRol("ROLE_ADMIN");
        admin.setFechaRegistro(LocalDate.now().atStartOfDay());
        usuarioService.create(admin, "admin123");
        System.out.println("✅ Usuario admin creado: admin@mourosub.com / admin123");
    }

    // ---------------------------------------------------------------
    // Ubicaciones
    // ---------------------------------------------------------------
    private void initUbicaciones() {
        if (ubicacionRepository.count() > 0) return;

        Ubicacion u1 = new Ubicacion();
        u1.setNombre("Isla de Mouro");
        u1.setTipoFondo("Roca y arenisco");
        u1.setProfundidadMax(28);
        u1.setNivelBuceo("Advanced");
        ubicacionRepository.save(u1);

        Ubicacion u2 = new Ubicacion();
        u2.setNombre("Punta del Dichoso");
        u2.setTipoFondo("Arena y posidonia");
        u2.setProfundidadMax(12);
        u2.setNivelBuceo("Open Water");
        ubicacionRepository.save(u2);

        Ubicacion u3 = new Ubicacion();
        u3.setNombre("Costa Cantábrica Norte");
        u3.setTipoFondo("Pared vertical");
        u3.setProfundidadMax(40);
        u3.setNivelBuceo("Técnico");
        ubicacionRepository.save(u3);

        System.out.println("✅ Ubicaciones inicializadas");
    }

    // ---------------------------------------------------------------
    // Instructores
    // ---------------------------------------------------------------
    private void initInstructores() {
        if (instructorRepository.count() > 0) return;

        Instructor i1 = new Instructor();
        i1.setDniInstructor("12345678B");
        i1.setNombre("Carlos");
        i1.setApellido1("Ruiz");
        i1.setApellido2("Marina");
        i1.setEmail("carlos@mourosub.com");
        i1.setTelefono("+34 612 345 678");
        i1.setTituloPadi("PADI Course Director");
        i1.setEspecialidad("Buceo técnico y cuevas");
        i1.setExperiencia("Más de 20 años de experiencia en buceo técnico. Ha guiado expediciones en el Mar Cantábrico, Islas Canarias y el Mar Mediterráneo. Apasionado de la fotografía submarina.");
        i1.setFotoUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80");
        i1.setActivo(true);
        instructorRepository.save(i1);

        Instructor i2 = new Instructor();
        i2.setDniInstructor("23456789C");
        i2.setNombre("Laura");
        i2.setApellido1("González");
        i2.setApellido2("Pérez");
        i2.setEmail("laura@mourosub.com");
        i2.setTelefono("+34 623 456 789");
        i2.setTituloPadi("Master Scuba Diver Trainer");
        i2.setEspecialidad("Biología marina y Nitrox");
        i2.setExperiencia("Bióloga marina certificada PADI MSDT. Especialista en identificación de fauna del Cantábrico. Coordinadora del programa de conservación de arrecifes de MouroSub.");
        i2.setFotoUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80");
        i2.setActivo(true);
        instructorRepository.save(i2);

        Instructor i3 = new Instructor();
        i3.setDniInstructor("34567890D");
        i3.setNombre("Marcos");
        i3.setApellido1("Fernández");
        i3.setEmail("marcos@mourosub.com");
        i3.setTituloPadi("Open Water Instructor");
        i3.setEspecialidad("Bautismos y cursos para principiantes");
        i3.setExperiencia("Instructor joven con gran vocación pedagógica. Especializado en primera experiencia de buceo para adultos y niños a partir de 10 años.");
        i3.setFotoUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80");
        i3.setActivo(true);
        instructorRepository.save(i3);

        System.out.println("✅ Instructores inicializados");
    }

    // ---------------------------------------------------------------
    // Actividades
    // ---------------------------------------------------------------
    private void initActividades() {
        if (actividadRepository.count() > 0) return;

        Actividad a1 = new Actividad();
        a1.setNombre("Bautismo de Buceo");
        a1.setDescripcion("Tu primera inmersión guiada en aguas confinadas. La puerta de entrada al mundo submarino.");
        a1.setPrecio(Double.valueOf("45.00"));
        a1.setTipo("ACTIVIDAD");
        a1.setNivel("Sin experiencia");
        a1.setDuracion("3 horas");
        a1.setPlazasMaximas(6);
        a1.setImagenUrl("https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800&q=70");
        a1.setDestacada(true);
        a1.setActiva(true);
        actividadRepository.save(a1);

        Actividad a2 = new Actividad();
        a2.setNombre("Curso Open Water Diver");
        a2.setDescripcion("La certificación internacional PADI más reconocida del mundo. Aprende a bucear de forma autónoma hasta 18 metros.");
        a2.setDescripcionHtml("<h2>¿Qué incluye el curso?</h2><p>El curso Open Water Diver es el punto de partida de toda aventura submarina. Comprende:</p><ul><li>5 sesiones teóricas con materiales PADI</li><li>5 inmersiones en piscina (aguas confinadas)</li><li>4 inmersiones en mar abierto</li></ul><p>Al finalizar obtendrás tu certificación internacional válida de por vida.</p>");
        a2.setPrecio(Double.valueOf("320.00"));
        a2.setTipo("CURSO");
        a2.setNivel("Sin experiencia");
        a2.setDuracion("4-5 días");
        a2.setPlazasMaximas(8);
        a2.setImagenUrl("https://images.unsplash.com/photo-1559827291-72ee739d0d9a?w=800&q=70");
        a2.setDestacada(true);
        a2.setActiva(true);
        actividadRepository.save(a2);

        Actividad a3 = new Actividad();
        a3.setNombre("Advanced Open Water Diver");
        a3.setDescripcion("Amplía tus horizontes: buceo profundo, navegación, pez-piloto... El siguiente paso lógico tras el Open Water.");
        a3.setPrecio(Double.valueOf("280.00"));
        a3.setTipo("CURSO");
        a3.setNivel("Open Water");
        a3.setDuracion("2-3 días");
        a3.setPlazasMaximas(6);
        a3.setImagenUrl("https://images.unsplash.com/photo-1518020382113-a7e8fc38eac9?w=800&q=70");
        a3.setActiva(true);
        actividadRepository.save(a3);

        Actividad a4 = new Actividad();
        a4.setNombre("Inmersión en la Isla de Mouro");
        a4.setDescripcion("Una de las joyas del Cantábrico. Paredes verticales cubiertas de anémonas, bogavantes y meros de gran tamaño.");
        a4.setPrecio(Double.valueOf("55.00"));
        a4.setTipo("INMERSION");
        a4.setNivel("Open Water");
        a4.setDuracion("1 día (2 inmersiones)");
        a4.setPlazasMaximas(10);
        a4.setImagenUrl("https://images.unsplash.com/photo-1516426122078-c23e76319801?w=800&q=70");
        a4.setDestacada(true);
        a4.setActiva(true);
        actividadRepository.save(a4);

        Actividad a5 = new Actividad();
        a5.setNombre("Inmersión Nocturna");
        a5.setDescripcion("El mar de noche es un universo diferente. Pulpos cazando, estrellas de mar activas y la bioluminiscencia del plancton.");
        a5.setPrecio(Double.valueOf("65.00"));
        a5.setTipo("INMERSION");
        a5.setNivel("Advanced");
        a5.setDuracion("1 noche");
        a5.setPlazasMaximas(6);
        a5.setActiva(true);
        actividadRepository.save(a5);

        Actividad a6 = new Actividad();
        a6.setNombre("Pack Verano: Open Water + 2 Inmersiones");
        a6.setDescripcion("Oferta especial de temporada: certifícate con Open Water e incluye 2 inmersiones guiadas en los mejores puntos del Cantábrico.");
        a6.setPrecio(Double.valueOf("350.00"));
        a6.setTipo("OFERTA");
        a6.setNivel("Sin experiencia");
        a6.setDuracion("1 semana");
        a6.setActiva(true);
        actividadRepository.save(a6);

        System.out.println("✅ Actividades inicializadas");
    }

    // ---------------------------------------------------------------
    // Noticias
    // ---------------------------------------------------------------
    private void initNoticias() {
        if (noticiaRepository.count() > 0) return;

        Noticia n1 = new Noticia();
        n1.setTitulo("Expedición científica en el Cantábrico: documentamos nuevas especies");
        n1.setResumen("Nuestro equipo de biología marina ha finalizado una expedición de dos semanas catalogando la biodiversidad de los fondos cantábricos. Los resultados son sorprendentes.");
        n1.setCuerpoHtml("<p>Durante las últimas dos semanas, el equipo de MouroSub ha colaborado con investigadores de la Universidad de Cantabria en una expedición sin precedentes...</p><h2>Especies catalogadas</h2><p>Se han documentado más de 40 especies de peces y 120 especies de invertebrados en la zona de Cabo Mayor.</p><blockquote>\"Este nivel de biodiversidad nos recuerda la urgencia de proteger estos ecosistemas.\" — Laura González, bióloga marina</blockquote>");
        n1.setCategoria("EXPEDICION");
        n1.setImagenUrl("https://images.unsplash.com/photo-1565118531796-763e5082d113?w=900&q=70");
        n1.setFechaPublicacion(LocalDate.now().minusDays(5));
        n1.setPublicada(true);
        noticiaRepository.save(n1);

        Noticia n2 = new Noticia();
        n2.setTitulo("Nueva certificación Rescue Diver: abierta la inscripción");
        n2.setResumen("MouroSub lanza el curso Rescue Diver, el más valorado por los buceadores experimentados. Aprende a prevenir y gestionar situaciones de emergencia.");
        n2.setCuerpoHtml("<p>El curso Rescue Diver es considerado el punto de inflexión en la formación de cualquier buceador serio. A partir de septiembre, MouroSub ofrecerá esta formación en formato intensivo de fin de semana.</p><h2>¿Qué aprenderás?</h2><ul><li>Auto-rescate y primeros auxilios</li><li>Gestión del pánico en el buceador</li><li>Rescate en superficie y bajo el agua</li><li>Coordinación con equipos de emergencia</li></ul>");
        n2.setCategoria("FORMACION");
        n2.setImagenUrl("https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?w=900&q=70");
        n2.setFechaPublicacion(LocalDate.now().minusDays(12));
        n2.setPublicada(true);
        noticiaRepository.save(n2);

        Noticia n3 = new Noticia();
        n3.setTitulo("Limpieza de fondos marinos: únete a la iniciativa");
        n3.setResumen("El próximo 21 de septiembre, Día Mundial del Mar, organizamos una jornada de limpieza submarina en la Bahía de Santander. Plazas limitadas.");
        n3.setCuerpoHtml("<p>Como escuela comprometida con el ecosistema, MouroSub organiza cada año su jornada de limpieza submarina. Este año coincidirá con el Día Mundial del Mar.</p><p>Participar es gratuito para todos los alumnos certificados con nosotros. Los materiales de recogida y el aire estarán incluidos.</p>");
        n3.setCategoria("ECOSISTEMA");
        n3.setImagenUrl("https://images.unsplash.com/photo-1542601098-8fc114e148e2?w=900&q=70");
        n3.setFechaPublicacion(LocalDate.now().minusDays(20));
        n3.setPublicada(true);
        noticiaRepository.save(n3);

        Noticia n4 = new Noticia();
        n4.setTitulo("Nuevo equipo de fotografía submarina disponible en alquiler");
        n4.setResumen("MouroSub incorpora al servicio de alquiler cámaras GoPro Hero 13 y housing para mirrorless hasta 60m de profundidad. Inmortaliza cada inmersión.");
        n4.setCuerpoHtml("<p>Sabemos que el recuerdo de una inmersión vale más que mil palabras. Por eso hemos renovado nuestro parque de cámaras de acción para que puedas llevarte un pedazo del Cantábrico a casa.</p>");
        n4.setCategoria("TECNOLOGIA");
        n4.setImagenUrl("https://images.unsplash.com/photo-1516426122078-c23e76319801?w=900&q=70");
        n4.setFechaPublicacion(LocalDate.now().minusDays(30));
        n4.setPublicada(true);
        noticiaRepository.save(n4);

        System.out.println("✅ Noticias inicializadas");
    }
}
