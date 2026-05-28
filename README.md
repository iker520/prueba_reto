# MouroSub - Plataforma Web para Escuela de Buceo

Aplicación web completa para la gestión de una escuela de buceo. Permite a los usuarios consultar servicios, realizar reservas, leer noticias y gestionar su cuenta. Incluye un panel de administración completo para la gestión interna del negocio.

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración del entorno](#configuración-del-entorno)
- [Ejecución en local](#ejecución-en-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Funcionalidades](#funcionalidades)
- [Credenciales por defecto](#credenciales-por-defecto)
- [Variables de entorno](#variables-de-entorno)
- [Base de Datos](#base-de-datos)
- [Health check](#health-check)
- [Pruebas](#pruebas)
- [Contribución](#contribución)
- [Contexto Académico](#contexto-académico)

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 25 · Spring Boot 3.5.0 |
| Seguridad | Spring Security 6.5 · BCrypt |
| Persistencia | Spring Data JPA · Hibernate 6 · MariaDB |
| Plantillas | Thymeleaf 3.1 + Spring Security extras |
| Frontend | HTML/CSS/JS · TipTap (editor rich text) |
| Validación | Jakarta Validation · Hibernate Validator |
| Infraestructura | Docker · Docker Compose · Nginx · FTP |
| Utilidades | Lombok · Apache POI · Spring Boot Actuator |

---

## Requisitos previos

- **Java 25** (JDK)
- **Maven 3.9+** (o usar el wrapper `./mvnw` incluido)
- **MariaDB 10.11+** (local o vía Docker)
- **Docker y Docker Compose** (solo para despliegue con contenedores)

---

## Configuración del entorno

La aplicación carga su configuración desde un archivo `.env` en la raíz del proyecto.

1. Copia el archivo de ejemplo y rellena los valores:

```bash
cp .env.example .env
```

2. Edita `.env` con tus datos:

```properties
# Puerto del servidor
APP_PORT=8080

# Base de datos
DB_ROOT_PASSWORD=cambia_esta_password_root
DB_NAME=mourosub
DB_URL=jdbc:mariadb://db:3306/mourosub
DB_USERNAME=usuario_db
DB_PASSWORD=cambia_esta_password_db

# JPA: update | validate | create | none
JPA_DDL_AUTO=update

# Debug SQL (true solo en desarrollo)
SPRING_JPA_SHOW_SQL=false

# Thymeleaf cache (false en dev, true en producción)
THYMELEAF_CACHE=false

# Ruta de subida de archivos
UPLOADS_PATH=/ruta/a/mourosub-uploads_cambiar_por_ftp

# Seed de datos demo (true para generar datos de prueba)
APP_SEED_ENABLED=false

# FTP
FTP_USER=mourosub
FTP_PASSWORD=contraseña_segura
FTP_ADDRESS=192.168.35.167

# Nginx
NGINX_SERVER_NAME=192.168.35.167
```

> **Nota:** Si ejecutas en local sin Docker, asegúrate de que `DB_URL` apunte a `localhost` en lugar de `db`.

---

## Ejecución en local

```bash
# Clona el repositorio
git clone <url-del-repositorio>
cd prueba_reto

# Configura el entorno
cp .env.example .env
# (edita .env con tus valores)

# Compila y arranca
./mvnw spring-boot:run

# O en Windows:
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://[IP_ADDRESS]`
O en local en: `http://localhost:8080`

---

## Ejecución con Docker

El proyecto incluye un `docker-compose.yml` que levanta cuatro contenedores de forma automática. Al utilizar esta opción, te conectarás gracias al proxy inverso, por lo que la aplicación principal estará disponible directamente en: `http://192.168.35.167`

| Servicio | Imagen | Puerto |
|---|---|---|
| `db` | `mariadb:11` | 3306 |
| `ftp` | `delfer/alpine-ftp-server` | 21 |
| `app` | `eclipse-temurin:25-jdk-alpine` | 8080 (interno) |
| `proxy` | `nginx:alpine` | 80 / 443 |

```bash
# Configura el entorno
cp .env.example .env
# (edita .env con tus valores, incluyendo FTP_ADDRESS y NGINX_SERVER_NAME)

# Levanta todos los servicios
docker compose up -d

# Ver logs
docker compose logs -f app

# Parar los servicios
docker compose down
```

> Para producción, coloca tus certificados SSL en la carpeta `./certs/` y ajusta `NGINX_SERVER_NAME` en el `.env`.

---

## Estructura del proyecto

```
prueba_reto/
├── src/
│   └── main/
│       ├── java/com/example/mourosub/
│       │   ├── config/          # Seguridad, inicialización de datos, handlers
│       │   ├── controller/      # Controladores públicos (web)
│       │   │   └── admin/       # Controladores del panel de administración
│       │   ├── model/           # Entidades JPA
│       │   ├── repository/      # Repositorios Spring Data JPA
│       │   └── service/         # Lógica de negocio
│       └── resources/
│           ├── templates/       # Plantillas Thymeleaf
│           │   ├── admin/       # Vistas del panel admin
│           │   ├── public/      # Vistas públicas
│           │   ├── layout/      # Layouts base
│           │   ├── policies/    # Páginas legales
│           │   └── error/       # Páginas de error (403, 404, 500)
│           ├── static/
│           │   ├── css/         # Estilos (mourosub.css)
│           │   └── js/          # Scripts (TipTap, base, etc.)
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
├── nginx.conf
├── .env.example
└── pom.xml
```

### Árbol detallado de clases

```
prueba_reto/
├── src/
│   └── main/
│       ├── java/com/example/mourosub/
│       │   │
│       │   ├── MourousubApplication.java               ← Clase principal (main)
│       │   │
│       │   ├── config/
│       │   │   ├── AdminModelAdvice.java                ← Inyecta datos globales en vistas admin
│       │   │   ├── CustomLoginSuccessHandler.java       ← Redirección tras login según rol
│       │   │   ├── DataInitializer.java                 ← Seed de datos iniciales (admin, etc.)
│       │   │   ├── GlobalExceptionHandler.java          ← Manejo global de excepciones
│       │   │   ├── PasswordEncoderConfig.java           ← Bean BCryptPasswordEncoder
│       │   │   ├── SecurityConfig.java                  ← Configuración Spring Security
│       │   │   └── WebConfig.java                       ← Configuración MVC (recursos estáticos, etc.)
│       │   │
│       │   ├── controller/
│       │   │   ├── ConocenosController.java             ← GET /conocenos
│       │   │   ├── ContactoController.java              ← GET/POST /contacto
│       │   │   ├── HomeController.java                  ← GET /
│       │   │   ├── LegalController.java                 ← Páginas legales
│       │   │   ├── LoginController.java                 ← GET /login
│       │   │   ├── MiCuentaController.java              ← GET /mi-cuenta
│       │   │   ├── NewsletterController.java            ← POST /newsletter
│       │   │   ├── NoticiasController.java              ← GET /noticias
│       │   │   ├── RegistroController.java              ← GET/POST /registro
│       │   │   ├── ReservaController.java               ← GET/POST /reservar
│       │   │   ├── ServiciosController.java             ← GET /servicios
│       │   │   │
│       │   │   └── admin/
│       │   │       ├── AdminActividadController.java    ← CRUD actividades
│       │   │       ├── AdminCertificacionController.java← Gestión certificaciones
│       │   │       ├── AdminContactoController.java     ← Bandeja de contactos
│       │   │       ├── AdminDashboardController.java    ← Panel principal /admin
│       │   │       ├── AdminInstructorController.java   ← CRUD instructores
│       │   │       ├── AdminNewsletterController.java   ← Gestión suscriptores
│       │   │       ├── AdminNoticiaController.java      ← CRUD noticias (TipTap)
│       │   │       ├── AdminReservaController.java      ← Gestión reservas
│       │   │       └── AdminUsuarioController.java      ← CRUD usuarios
│       │   │
│       │   ├── model/
│       │   │   ├── Actividad.java                       ← Entidad actividad de buceo
│       │   │   ├── ActividadReserva.java                ← Relación actividad ↔ reserva
│       │   │   ├── ActividadReservaId.java              ← PK compuesta (id_actividad, id_reserva)
│       │   │   ├── ActividadReservaUbicacion.java       ← Programación de actividad en ubicación
│       │   │   ├── Certificacion.java                   ← Certificación de usuario
│       │   │   ├── Contacto.java                        ← Mensaje de contacto
│       │   │   ├── Instructor.java                      ← Instructor de buceo
│       │   │   ├── InstructorReserva.java               ← Relación instructor ↔ reserva
│       │   │   ├── InstructorReservaId.java             ← PK compuesta (dni_instructor, id_reserva)
│       │   │   ├── Newsletter.java                      ← Suscriptor newsletter
│       │   │   ├── Noticia.java                         ← Noticia del blog
│       │   │   ├── Reserva.java                         ← Reserva de actividad
│       │   │   ├── Ubicacion.java                       ← Ubicación de buceo
│       │   │   ├── Usuario.java                         ← Usuario (cliente o admin)
│       │   │   ├── UsuarioReserva.java                  ← Relación usuario ↔ reserva
│       │   │   └── UsuarioReservaId.java                ← PK compuesta (dni_usuario, id_reserva)
│       │   │
│       │   ├── repository/
│       │   │   ├── ActividadRepository.java
│       │   │   ├── ActividadReservaRepository.java
│       │   │   ├── ActividadReservaUbicacionRepository.java
│       │   │   ├── CertificacionRepository.java
│       │   │   ├── ContactoRepository.java
│       │   │   ├── InstructorRepository.java
│       │   │   ├── NewsletterRepository.java
│       │   │   ├── NoticiaRepository.java
│       │   │   ├── ReservaRepository.java
│       │   │   ├── UbicacionRepository.java
│       │   │   ├── UsuarioRepository.java
│       │   │   └── UsuarioReservaRepository.java
│       │   │
│       │   └── service/
│       │       ├── ActividadService.java
│       │       ├── CertificacionService.java
│       │       ├── ContactoService.java
│       │       ├── InstructorService.java
│       │       ├── NoticiaService.java
│       │       ├── ReservaService.java
│       │       └── UsuarioService.java
│       │
│       └── resources/
│           ├── application.properties
│           │
│           ├── static/
│           │   ├── css/
│           │   │   └── mourosub.css
│           │   └── js/
│           │       ├── admin-tiptap-init.js
│           │       ├── admin-tiptap.js
│           │       ├── base.js
│           │       ├── conocenos.js
│           │       └── index.js
│           │
│           └── templates/
│               ├── layout/
│               │   ├── base.html                        ← Layout público
│               │   └── admin-base.html                  ← Layout admin
│               ├── public/
│               │   ├── index.html
│               │   ├── servicios.html
│               │   ├── servicio-detalle.html
│               │   ├── noticias.html
│               │   ├── noticia-detalle.html
│               │   ├── conocenos.html
│               │   ├── contacto.html
│               │   ├── login.html
│               │   ├── registro.html
│               │   ├── reservar.html
│               │   └── mi-cuenta.html
│               ├── admin/
│               │   ├── dashboard.html
│               │   ├── actividades/      (form, list)
│               │   ├── certificaciones/  (lista, mouro-form, mouro-lista, usuario-docs)
│               │   ├── instructores/     (form, list)
│               │   ├── noticias/         (form, list)
│               │   ├── reservas/         (detalle, list)
│               │   └── usuarios/         (form, list)
│               ├── policies/
│               │   ├── aviso-legal.html
│               │   ├── condiciones-venta.html
│               │   ├── devoluciones.html
│               │   └── politica-privacidad.html
│               └── error/
│                   ├── 403.html
│                   ├── 404.html
│                   └── 500.html
```

> El proyecto cuenta con **62 clases Java** organizadas en 4 capas (config, controller, model, repository + service) y **29 plantillas Thymeleaf**.

---

## Funcionalidades

### Zona pública
- **Inicio** — presentación de la escuela y servicios destacados
- **Servicios** — catálogo de actividades de buceo con detalle
- **Noticias** — blog de noticias con detalle por entrada
- **Conócenos** — información sobre instructores y certificaciones
- **Contacto** — formulario de contacto
- **Newsletter** — suscripción a boletín
- **Reservas** — sistema de reserva de actividades para usuarios registrados
- **Mi cuenta** — gestión del perfil y reservas del usuario
- **Páginas legales** — aviso legal, política de privacidad, condiciones de venta y devoluciones

### Panel de administración (`/admin`)
- **Dashboard** — resumen general
- **Usuarios** — listado, creación y edición
- **Reservas** — gestión y detalle de reservas
- **Actividades** — CRUD completo de actividades
- **Noticias** — editor rich text (TipTap) para publicar noticias
- **Instructores** — gestión del equipo
- **Certificaciones** — gestión de certificaciones Mouro y documentos de usuarios
- **Contacto** — bandeja de mensajes recibidos
- **Newsletter** — gestión de suscriptores

### Seguridad
- Autenticación con formulario y sesión HTTP
- Roles `ROLE_USER` y `ROLE_ADMIN`
- Protección CSRF con cookie
- Páginas de error personalizadas (403, 404, 500)
- Contraseñas cifradas con BCrypt

---

## Credenciales por defecto

Al arrancar la aplicación por primera vez se crea automáticamente un usuario administrador:

| Campo | Valor |
|---|---|
| Email | `admin@mourosub.com` |
| Contraseña | `admin123` |

> **Importante:** Cambia esta contraseña inmediatamente en un entorno de producción desde el panel de administración.

---

## Variables de entorno

Referencia completa de todas las variables disponibles en `.env.example`:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `APP_PORT` | Puerto HTTP de la aplicación | `8080` |
| `DB_ROOT_PASSWORD` | Contraseña root de MariaDB | `secret` |
| `DB_NAME` | Nombre de la base de datos | `mourosub` |
| `DB_URL` | URL JDBC de conexión | `jdbc:mariadb://db:3306/mourosub` |
| `DB_USERNAME` | Usuario de base de datos | `mourosub_user` |
| `DB_PASSWORD` | Contraseña de base de datos | `secret` |
| `JPA_DDL_AUTO` | Estrategia DDL de Hibernate | `update` |
| `SPRING_JPA_SHOW_SQL` | Mostrar SQL en consola | `false` |
| `THYMELEAF_CACHE` | Caché de plantillas | `false` |
| `UPLOADS_PATH` | Ruta de almacenamiento de archivos | `/uploads` |
| `APP_SEED_ENABLED` | Activar datos de demo al arrancar | `false` |
| `FTP_USER` | Usuario del servidor FTP | `mourosub` |
| `FTP_PASSWORD` | Contraseña del servidor FTP | `secret` |
| `FTP_ADDRESS` | IP pública del servidor FTP | `1.2.3.4` |
| `NGINX_SERVER_NAME` | Dominio del servidor Nginx | `tudominio.com` |

---

## Base de Datos

La aplicación utiliza MariaDB como motor de base de datos relacional. El modelo de datos está gestionado mediante JPA (Hibernate) que mapea las entidades Java a las tablas correspondientes.
El comportamiento de creación de tablas puede ser modificado mediante la variable `JPA_DDL_AUTO`.

Las principales entidades del sistema son:
- **Usuario**: Almacena clientes y administradores.
- **Actividad**: Define los cursos y servicios ofertados.
- **Reserva**: Registra la inscripción de un usuario en una actividad.
- **Instructor**: Personal que imparte las actividades.
- **Certificacion**: Titulaciones de los usuarios.

---

## Health check

El endpoint de salud de Actuator está disponible públicamente:

```
GET /actuator/health
```

Útil para monitorización y comprobaciones de estado en Docker.

---

## Pruebas

Para ejecutar las pruebas unitarias y de integración del proyecto, se utiliza Maven:

```bash
./mvnw test
```

Asegúrate de que la base de datos de pruebas esté accesible y correctamente configurada en `application-test.properties` en caso de requerirse variables específicas para los entornos de prueba.

---

## Contribución

1. Haz un fork del repositorio.
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m 'Añade nueva funcionalidad'`).
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

Asegúrate de seguir los estándares de código y proporcionar pruebas para cualquier nueva funcionalidad.

---

## Contexto Académico

Este proyecto ha sido desarrollado como parte del proyecto de clase del ciclo de **Desarrollo de Aplicaciones Web (DAW)** en el **Instituto Augusto González Linares**. No posee una licencia de software y su propósito es puramente educativo y académico.
