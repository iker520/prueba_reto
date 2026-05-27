-- Archivo generado con la estructura y seed inicial de la BBDD
USE pruebas_mourosub;

CREATE TABLE IF NOT EXISTS usuarios (
    dni_usuario VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_1 VARCHAR(100),
    apellido_2 VARCHAR(100),
    fecha_nac DATE,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    codigo_postal VARCHAR(10),
    pais VARCHAR(100),
    tfno_sos VARCHAR(20),
    notif_emails BOOLEAN DEFAULT 0,
    notif_whatsapp BOOLEAN DEFAULT 0,
    fecha_registro DATETIME,
    nivel_buceo VARCHAR(100),
    num_inmersiones INT DEFAULT 0,
    fecha_ultima_inmersion DATE,
    seguro_accidentes BOOLEAN DEFAULT 0,
    compania_seguros VARCHAR(150),
    fecha_vto DATE,
    es_buceador BOOLEAN DEFAULT 0,
    estado_seguro VARCHAR(20),
    comprobant_seguro_url VARCHAR(500),
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER'
);

CREATE TABLE IF NOT EXISTS actividades (
    id_actividad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DOUBLE,
    tipo VARCHAR(50),
    subtipo VARCHAR(100),
    nivel VARCHAR(100),
    duracion_minutos INT,
    plazas_maximas INT,
    destacada BOOLEAN DEFAULT 0,
    activa BOOLEAN DEFAULT 1,
    descripcion_html LONGTEXT,
    imagen_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS reservas (
    id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva DATETIME NOT NULL,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    total DOUBLE,
    notas TEXT
);

CREATE TABLE IF NOT EXISTS certificaciones (
    num_certificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni_usuario VARCHAR(20) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    entidad_certificadora VARCHAR(150),
    fecha_inicio DATE,
    fecha_fin DATE,
    revisada BOOLEAN DEFAULT 0,
    validada BOOLEAN DEFAULT 0,
    expedida_por_mourosub BOOLEAN DEFAULT 0,
    documento_url VARCHAR(500),
    documento_hash VARCHAR(100),
    notas_admin VARCHAR(1000),
    fecha_registro DATETIME,
    CONSTRAINT fk_cert_usuario FOREIGN KEY (dni_usuario) REFERENCES usuarios (dni_usuario)
);

CREATE TABLE IF NOT EXISTS ubicaciones (
    id_ubicacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200),
    tipo_fondo VARCHAR(100),
    profundidad_max INT,
    nivel_buceo VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS actividades_ubicaciones (
    id_actividad BIGINT NOT NULL,
    id_ubicacion BIGINT NOT NULL,
    PRIMARY KEY (id_actividad, id_ubicacion),
    CONSTRAINT fk_au_actividad FOREIGN KEY (id_actividad) REFERENCES actividades (id_actividad),
    CONSTRAINT fk_au_ubicacion FOREIGN KEY (id_ubicacion) REFERENCES ubicaciones (id_ubicacion)
);

CREATE TABLE IF NOT EXISTS instructores (
    dni_instructor VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_1 VARCHAR(100),
    apellido_2 VARCHAR(100),
    fecha_nac DATE,
    email VARCHAR(150) UNIQUE,
    telefono VARCHAR(20),
    experiencia TEXT,
    titulo_padi VARCHAR(200),
    especialidad VARCHAR(200),
    activo BOOLEAN DEFAULT 1,
    foto_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS actividades_reservas (
    id_reserva BIGINT NOT NULL,
    id_actividad BIGINT NOT NULL,
    precio DOUBLE,
    PRIMARY KEY (id_reserva, id_actividad),
    CONSTRAINT fk_ar_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva),
    CONSTRAINT fk_ar_actividad FOREIGN KEY (id_actividad) REFERENCES actividades (id_actividad)
);

CREATE TABLE IF NOT EXISTS instructores_reservas (
    id_reserva BIGINT NOT NULL,
    id_actividad BIGINT NOT NULL,
    dni_instructor VARCHAR(20) NOT NULL,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    PRIMARY KEY (
        id_reserva,
        id_actividad,
        dni_instructor
    ),
    CONSTRAINT fk_ir_ar FOREIGN KEY (id_reserva, id_actividad) REFERENCES actividades_reservas (id_reserva, id_actividad),
    CONSTRAINT fk_ir_instructor FOREIGN KEY (dni_instructor) REFERENCES instructores (dni_instructor)
);

CREATE TABLE IF NOT EXISTS usuarios_reservas (
    id_reserva BIGINT NOT NULL,
    dni_usuario VARCHAR(20) NOT NULL,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    cantidad INT DEFAULT 1,
    es_buceador BOOLEAN DEFAULT 0,
    PRIMARY KEY (id_reserva, dni_usuario),
    CONSTRAINT fk_ur_reserva FOREIGN KEY (id_reserva) REFERENCES reservas (id_reserva),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (dni_usuario) REFERENCES usuarios (dni_usuario)
);

CREATE TABLE IF NOT EXISTS actividades_reservas_ubicaciones (
    id_programacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL,
    id_actividad BIGINT NOT NULL,
    id_ubicacion BIGINT NOT NULL,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    CONSTRAINT fk_aru_ar FOREIGN KEY (id_reserva, id_actividad) REFERENCES actividades_reservas (id_reserva, id_actividad),
    CONSTRAINT fk_aru_ubicacion FOREIGN KEY (id_ubicacion) REFERENCES ubicaciones (id_ubicacion)
);

CREATE TABLE IF NOT EXISTS noticias (
    id_noticia BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(300) NOT NULL,
    resumen TEXT,
    imagen_url VARCHAR(500),
    cuerpo_html LONGTEXT,
    categoria VARCHAR(100),
    hashtags VARCHAR(500),
    fecha_publicacion DATE,
    publicada BOOLEAN DEFAULT 0
);

CREATE TABLE IF NOT EXISTS contactos (
    id_contacto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL,
    interes VARCHAR(200),
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL,
    estado VARCHAR(50) DEFAULT 'NUEVA'
);

CREATE TABLE IF NOT EXISTS newsletter (
    id_newsletter BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE
);

-- =========================================================
-- ESPACIO PARA TUS INSERTS:
-- =========================================================

-- Inserciones para la tabla UBICACIONES (usamos INSERT IGNORE e IDs fijos para evitar duplicados al reiniciar)
INSERT IGNORE INTO
    ubicaciones (
        id_ubicacion,
        nivel_buceo,
        nombre,
        profundidad_max,
        tipo_fondo
    )
VALUES (
        1,
        NULL,
        'Snorkel en Isla de Mouro',
        4,
        'Fondo de arena y roca'
    ),
    (
        2,
        'Básico',
        'La Cala',
        8,
        'Fondo de arena y roca'
    ),
    (
        3,
        'Experto',
        'El Bálamo en Cabos y Cabezos',
        60,
        'Fondo de arena y roca.'
    ),
    (
        4,
        'Experto',
        'La Lengüeta en Cabos y Cabezos',
        70,
        'Fondo de arena y roca'
    ),
    (
        5,
        'Medio',
        'Cabo Menor',
        22,
        'Fondo de arena y roca'
    ),
    (
        6,
        'Básico',
        'El Peñón de Mataleñas',
        15,
        'Fondo de arena y roca'
    ),
    (
        7,
        'Básico',
        'Faro de la Cerda',
        10,
        'Fondo de arena y roca'
    ),
    (
        8,
        'Básico',
        'Las Lastras del Palacio',
        14,
        'Fondo de roca'
    ),
    (
        9,
        NULL,
        'Zona de Snorkel en Isla de Santa Marina',
        6,
        'Fondo de arena y roca'
    ),
    (
        10,
        'Básico',
        'El Bull',
        10,
        'Fondo de roca'
    ),
    (
        11,
        'Básico',
        'Antártico',
        10,
        'Fondo de arena'
    ),
    (
        12,
        'Básico',
        'Lolo Nin',
        10,
        'Fondo de roca'
    ),
    (
        13,
        NULL,
        'Piscinas Instalaciones',
        2,
        NULL
    ),
    (
        14,
        NULL,
        'Instalaciones Mourosub',
        NULL,
        NULL
    );

-- Inserciones para la tabla NOTICIAS
INSERT IGNORE INTO
    noticias (
        `id_noticia`,
        `categoria`,
        `cuerpo_html`,
        `fecha_publicacion`,
        `hashtags`,
        `imagen_url`,
        `publicada`,
        `resumen`,
        `titulo`
    )
VALUES (
        1,
        'EXPEDICION',
        '<p>Durante las últimas dos semanas, el equipo de MouroSub ha colaborado con investigadores de la Universidad de Cantabria en una expedición sin precedentes...</p><h2>Especies catalogadas</h2><p>Se han documentado más de 40 especies de peces y 120 especies de invertebrados en la zona de Cabo Mayor.</p><blockquote>"Este nivel de biodiversidad nos recuerda la urgencia de proteger estos ecosistemas." — Laura González, bióloga marina</blockquote>',
        '2026-05-19',
        '#Expedición #Biodiversidad #Cantábrico',
        '/img/noticias/noticia1.jpeg',
        b'1',
        'Nuestro equipo de biología marina ha finalizado una expedición de dos semanas catalogando la biodiversidad de los fondos cantábricos. Los resultados son sorprendentes.',
        'Expedición científica en el Cantábrico: documentamos nuevas especies'
    ),
    (
        2,
        'FORMACION',
        '<p>El curso Rescue Diver es considerado el punto de inflexión en la formación de cualquier buceador serio. A partir de septiembre, MouroSub ofrecerá esta formación en formato intensivo de fin de semana.</p><h2>¿Qué aprenderás?</h2><ul><li>Auto-rescate y primeros auxilios</li><li>Gestión del pánico en el buceador</li><li>Rescate en superficie y bajo el agua</li><li>Coordinación con equipos de emergencia</li></ul>',
        '2026-05-12',
        '#RescueDiver #Formación #BuceoSeguro',
        '/img/noticias/noticia2.jpg',
        b'1',
        'MouroSub lanza el curso Rescue Diver, el más valorado por los buceadores experimentados. Aprende a prevenir y gestionar situaciones de emergencia.',
        'Nueva certificación Rescue Diver: abierta la inscripción'
    ),
    (
        3,
        'ECOSISTEMA',
        '<p>Como escuela comprometida con el ecosistema, MouroSub organiza cada año su jornada de limpieza submarina. Este año coincidirá con el Día Mundial del Mar.</p><p>Participar es gratuito para todos los alumnos certificados con nosotros. Los materiales de recogida y el aire estarán incluidos.</p>',
        '2026-05-04',
        '#MedioAmbiente #LimpiezaSubmarina #DíaMundialDelMar',
        '/img/noticias/noticia3.jpeg',
        b'1',
        'El próximo 21 de septiembre, Día Mundial del Mar, organizamos una jornada de limpieza submarina en la Bahía de Santander. Plazas limitadas.',
        'Limpieza de fondos marinos: únete a la iniciativa'
    ),
    (
        4,
        'TECNOLOGIA',
        '<p>Sabemos que el recuerdo de una inmersión vale más que mil palabras. Por eso hemos renovado nuestro parque de cámaras de acción para que puedas llevarte un pedazo del Cantábrico a casa.</p>',
        '2026-04-24',
        '#FotografíaSubmarina #GoPro #Tecnología',
        '/img/noticias/noticia4.jpeg',
        b'1',
        'MouroSub incorpora al servicio de alquiler cámaras GoPro Hero 13 y housing para mirrorless hasta 60m de profundidad. Inmortaliza cada inmersión.',
        'Nuevo equipo de fotografía submarina disponible en alquiler'
    );

-- Inserciones para la tabla INSTRUCTORES
INSERT IGNORE INTO
    instructores (
        `dni_instructor`,
        `activo`,
        `apellido_1`,
        `apellido_2`,
        `email`,
        `especialidad`,
        `experiencia`,
        `fecha_nac`,
        `foto_url`,
        `nombre`,
        `telefono`,
        `titulo_padi`
    )
VALUES (
        'INS001',
        b'1',
        'del Rincón',
        NULL,
        'leo@mourosub.com',
        'SSI / ESA / FEDAS / CMAS / REBREATHER T.I.R. 40 · Instructor de Buceo Técnico SSI XR · Instructor PADI / DAN / EFR · Técnico en Reparación y Mto. de Equipos · Patrón de Embarcación · Técnico Deportivo',
        'Instructor Trainer SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/leo-mourosub.jpg',
        'Leo',
        '621330839',
        'Buceo Técnico XR · Rebreather'
    ),
    (
        'INS002',
        b'1',
        'Ruiz',
        NULL,
        'carmen@mourosub.com',
        'Master Instructor SSI · Instructor ESA / CMAS / DAN · Técnico en Reparación y Mto. de Equipos · Patrón de Embarcación · Técnico Deportivo',
        'Master Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/carmen.jpg',
        'Carmen',
        '621330839',
        'Formación y Técnico de Equipos'
    ),
    (
        'INS003',
        b'1',
        'Azcuenaga',
        NULL,
        'juanjo@mourosub.com',
        'Instructor SSI / ESA / CMAS · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/Juanjo-Azcuenaga-300x150.jpg',
        'Juanjo',
        '621330839',
        'Inmersiones Deportivas'
    ),
    (
        'INS004',
        b'1',
        'Fuentes',
        NULL,
        'oscar@mourosub.com',
        'Instructor SSI / ESA / CMAS / DAN · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI / DAN',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/ocar_definitiu-300x150.jpg',
        'Oscar',
        '621330839',
        'Seguridad y Rescate'
    ),
    (
        'INS005',
        b'1',
        'Rodriguez',
        NULL,
        'luis@mourosub.com',
        'Instructor SSI · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2023/02/Luis-Rodriguez.jpg',
        'Luis',
        '621330839',
        'Buceo Deportivo'
    ),
    (
        'INS006',
        b'1',
        'Gerez',
        NULL,
        'alejandro@mourosub.com',
        'Instructor SSI / DAN · Técnico en Reparación y Mto. de Equipos · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI / DAN',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/alejandro-gerez-300x150.png',
        'Alejandro',
        '621330839',
        'Técnico de Equipos'
    ),
    (
        'INS007',
        b'1',
        'Reven',
        NULL,
        'jose.reven@mourosub.com',
        'Instructor SSI · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2023/02/Jose-Reven.jpg',
        'Jose',
        '621330839',
        'Buceo Deportivo'
    ),
    (
        'INS008',
        b'1',
        'Balbas',
        NULL,
        'meritxell@mourosub.com',
        'Instructor SSI · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/meritxell-300x150.png',
        'Meritxell',
        '621330839',
        'Buceo Deportivo'
    ),
    (
        'INS009',
        b'1',
        'Sáinz',
        NULL,
        'joseluis@mourosub.com',
        'Instructor SSI / ESA / CMAS / DAN · Técnico en Reparación y Mto. de Equipos · Técnico Deportivo',
        'Instructor SSI / DAN',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/joseluis-300x150.jpg',
        'Jose Luis',
        '621330839',
        'Técnico de Equipos'
    ),
    (
        'INS010',
        b'1',
        'Arnáiz',
        NULL,
        'miguel@mourosub.com',
        'Instructor SSI / ESA / CMAS · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/miguelon-300x150.jpg',
        'Miguel',
        '621330839',
        'Buceo Deportivo'
    ),
    (
        'INS011',
        b'1',
        'Alonso',
        NULL,
        'vicky@mourosub.com',
        'Instructor SSI · Técnico Deportivo',
        'Instructor SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2018/02/m-300x150.png',
        'Vicky',
        '621330839',
        'Buceo Deportivo'
    ),
    (
        'INS012',
        b'1',
        'Puente',
        NULL,
        'alberto@mourosub.com',
        'Dive Master SSI · Técnico Deportivo',
        'Dive Master SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2023/02/alberto-puente.png',
        'Alberto',
        '621330839',
        'Asistente de Buceo'
    ),
    (
        'INS013',
        b'1',
        'Gasco',
        NULL,
        'jlgasco@mourosub.com',
        'Dive Master SSI · Técnico Deportivo',
        'Dive Master SSI',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/Foto-web-miguelon-02-300x233.jpg',
        'Jose Luis',
        '621330839',
        'Asistente de Buceo'
    ),
    (
        'INS014',
        b'1',
        'Galán',
        NULL,
        'santiago@mourosub.com',
        'Instructor SSI Freediving · Patrón de Embarcación · Técnico Deportivo',
        'Instructor SSI Freediving',
        NULL,
        'https://www.mourosub.com/wp-content/uploads/2015/02/santiago-galan-300x150.jpg',
        'Santiago',
        '621330839',
        'Apnea y Freediving'
    );

-- Inserciones para la tabla ACTIVIDADES
INSERT IGNORE INTO
    actividades (
        `id_actividad`,
        `nombre`,
        `descripcion`,
        `precio`,
        `tipo`,
        `subtipo`,
        `nivel`,
        `duracion_minutos`,
        `plazas_maximas`,
        `destacada`,
        `activa`,
        `imagen_url`,
        `descripcion_html`
    )
VALUES (
        1,
        'Bautismo de Buceo',
        'Tu primera inmersión guiada en aguas confinadas. La puerta de entrada al mundo submarino.',
        50,
        'actividad',
        NULL,
        'Sin experiencia',
        180,
        6,
        1,
        1,
        '/img/servicios/actividades/bautismo.png',
        NULL
    ),
    (
        2,
        'Snorkel en Isla de Mouro',
        'Esta zona está totalmente protegida de los envites del mar, con una profundidad max. de 4 metros, hacen que la visibilidad sea perfecta para la practica de snorkel y bautismos de buceo.\n\nSu fondo es de arena y piedra con multitud de colores por la abundante población de algas. Nos podemos encontrar infinidad de especies, como erizos de múltiples colores, estrellas con diferentes formas y colores, cardúmenes de jargos, julias, porredanos, bogas, mules y chicharros, también veremos cefalópodos como pulpos y sepias, en los fondos arenosos lenguados, rodaballos y posiblemente alguna raya.',
        NULL,
        'inmersion',
        'Isla de Mouro',
        'Snorkel/FreeDive',
        60,
        6,
        1,
        1,
        '/img/servicios/inmersiones/isla_mouro_snorkel.jpg',
        NULL
    ),
    (
        3,
        'La Cala',
        'Zona de fondeo de los barcos de recreo y buceo, relativamente protegida del viento y el oleaje, con cotas entre los 3 y 8 metros de profundidad y donde podremos encontrar una abundante micro fauna  que se protege entre las algas pardas( mayoritarias en esta zona) y las praderas de “caloca” otra alga rojiza de menor porte que la anterior.\n\nDestacaremos los moluscos, anélidos, asteroideos, equinodermos, cefalópodos y crustáceos, como los más comunes. Pero los briozoos, ascinias, pequeños corales y anémonas son también habituales. Multitud de peces: bogas, mules, lubinas, julias, sargos, salmonetes…etc. compiten por encontrar el alimento que estas aguas bien oxigenadas les proporciona.\n\nEs una inmersión ideal para los aficionados a la macrofotografía subacuática.',
        NULL,
        'inmersion',
        'Isla de Mouro',
        'Bautismo',
        60,
        8,
        1,
        1,
        '/img/servicios/inmersiones/isla_mouro_lacala.jpg',
        NULL
    ),
    (
        4,
        'El Bálamo en Cabos y Cabezos',
        '“Banco de pesca”, normalmente de sardinas… este es el significado de Bálamo, nombre dado a esta cordillera subacuática de varios kilómetros de extensión por los pescadores Cántabros.\nEs sin duda, una de las inmersiones más espectaculares de las costas Cántabras, con profundidades que oscilan entre los 22 y los 60 metros.\n\nBuceo exigente, normalmente con fuertes corrientes y descompresión asegurada, solo recomendable para buceadores con experiencia.\n\nEsta es zona de pesca, así que hay que tener especial cuidado con las redes rotas que se diseminan por todo su fondo, lo mejor es mantenerse cerca del veril, observando las enormes cavidades que estas paredes ofrecen.\n\nAquí podremos ver pelágicos como los túnidos, cardúmenes de lubinas, grandes bancos de chicharro y sardina, con un poco de suerte, incluso algún delfín. Pero los congrios, centollos, langostas y bogavantes son los moradores por excelencia de estas aguas. También las gorgonias blancas completarán esta inmersión de libro.',
        NULL,
        'inmersion',
        'Cabos y Cabezos',
        'Course OWD',
        60,
        6,
        1,
        1,
        '/img/servicios/inmersiones/cabos_cabezos_balamo.jpg',
        NULL
    ),
    (
        5,
        'La Lengüeta en Cabos y Cabezos',
        'Navegando por El Bálamo con rumbo N, encontraremos un cabo submarino, la profundidad que encontraremos en su parte más seca ronda los 24 metros, con un veril de caída prácticamente vertical que llega hasta los -70m.\n\nLas condiciones en esta inmersión son similares a las del Bálamo, pero la profundidad máxima el ligeramente superior y las corrientes suelen ser más fuertes. También hay que prestar especial atención a las frecuentes nieblas que se forman en esta zona.',
        NULL,
        'inmersion',
        'Cabos y Cabezos',
        'Open Water',
        180,
        10,
        1,
        1,
        '/img/servicios/inmersiones/cabos_cabezos_la_lengueta.jpg',
        NULL
    ),
    (
        6,
        'Cabo Menor',
        'Cabo Menor y Cabo Mayor son dos salientes de tierra frente al Mar Cantábrico, formado por calizas del Cretácico Inferior, dando origen a la ensenada y playa de Mataleñas. Está situado al norte del municipio de Santander, en la localidad de Cueto, el faro de Cabo Mayor, emerge desde un mirador donde el visitante tiene unas imponentes vistas del mar y todo el acantilado. La meseta de Cabo Menor se convirtió hace unos años en campo de golf del Municipio de Santander.\n\nEn 1778 hubo un primer intento para edificar dicho faro, proyectado por el ingeniero de marina Joaquín de Ibarguen. Pero es en 1833 cuando se aprueba la construcción del faro, con un diseño inicial del capitán de navío Felipe Bauzá y versión definitiva de Domingo Rojí. Su precio ascendió a 460.000 reales que fueron costeados por la Junta de Comercio de la Provincia estableciendo un arbitrio de un real por tonelada a los barcos españoles y dos a los extranjeros que entrasen en los puertos de Santander y Bilbao. El nuevo faro se encendió por primera vez la noche del 15 de agosto de 1839, elevándose en el lugar conocido como atalayón de Cabo Mayor donde, desde tiempos remotos, se hacían señales a los barcos, con banderas por el día y grandes fuegos por la noche. La incorporación de grupos electrógenos, sirena de niebla, y otros equipos técnicos, redujo sensiblemente el espacio habitacional existente en la base de la torre, por lo que en 1935 fue levantado el edificio anejo para vivienda de los fareros.',
        NULL,
        'inmersion',
        'Cabo Menor y Cabo Mayor',
        'Advanced Open Water',
        60,
        6,
        1,
        1,
        '/img/servicios/inmersiones/cabo_menor.jpg',
        NULL
    ),
    (
        7,
        'El Peñón de Mataleñas',
        'Desde la punta del cabo hacia la ensenada de Mataleñas encontraremos un peñón que aflora a superficie, si la mar está en calma se puede pasar entre este y el cantil del cabo, encontrando una serie de pasadizos y gateras donde suele encontrase abundante vida.\n\nContinuando por el cantil observaremos una serie de lajas formando varias viseras, refugio perfecto de congrios, pulpos, centollos, y peces de buen tamaño. Hay que prestar atención al estado de la mar, ya que en esta zona la batiente puede ser fuerte.',
        NULL,
        'inmersion',
        'Cabo Menor y Cabo Mayor',
        'Stress & Rescue',
        60,
        10,
        1,
        1,
        '/img/servicios/inmersiones/cabo_penon_matalena.jpg',
        NULL
    ),
    (
        8,
        'Faro de la Cerda',
        'Con días de buena visibilidad esta inmersión nos sorprenderá gratamente, sus numerosas gateras esconden infinidad de vida, haciendo el deleite de los aficionados al fotosub. La variedad de colorido y los contraluces nos permitirán captar fotos de gran calidad.\n\nEncontraremos pulpos, anémonas, estrellas, erizos, jargos, dentones, lubinas, doradas, durdos, cardúmenes de julias, bogas y chicharros, salmonetes, rayas, lenguados y rodaballos. Atención a las corrientes, siempre comenzar la inmersión en dirección W paralelos al cantil, así evitaremos salir en zona de paso de embarcaciones.',
        NULL,
        'inmersion',
        'El Palacio',
        'Divemaster/DiveGuide',
        90,
        8,
        1,
        1,
        '/img/servicios/inmersiones/el_palacio_faro_cerda.jpg',
        NULL
    ),
    (
        9,
        'Las Lastras del Palacio',
        'En las inmediaciones de los cantiles del Palacio encontraremos una zona de lastras y gateras que nos llevas a una serie de pasadizos de enorme belleza. Poblados por gran variedad de algas hacen que gran cantidad de especies convivan en esta zona, centollos, nécoras, pulpos, bogavantes, lubinas, cabrachos, ballestas, etc.\n\nSi nos adentramos en alguna de las cuevas nos podemos encontrar con alguna corvina de gran tamaño. Prestar atención a las corrientes para evitar salir en zona de paso de embarcaciones. Muy recomendable llevar linterna, carrete y boya para alertar a las embarcaciones de nuestra situación.',
        NULL,
        'inmersion',
        'El Palacio',
        'Snorkel/FreeDive',
        60,
        9,
        1,
        1,
        '/img/servicios/inmersiones/el_palacio_lastras.jpg',
        NULL
    ),
    (
        10,
        'Zona de Snorkel en Isla de Santa Marina',
        'Santa Marina tiene una pequeña playa en la zona sur, en el entorno de esta, con una profundidad max. de 6 metros, hacen que la visibilidad sea perfecta para la practica de snorkel y bautismos de buceo. Su fondo es de arena y piedra, con unas canales longitudinales y paralelas a la costa, permite la proliferación de algas multicolor.\n\nQue dan cobijo a infinidad de especies, como erizos de múltiples colores, estrellas con diferentes formas, cardúmenes de jargos, julias, porredanos, bogas, mules y chicharros, también veremos cefalópodos como pulpos y sepias, en los fondos arenosos lenguados, rodaballos y posiblemente alguna raya.',
        NULL,
        'inmersion',
        'Isla de Santa Marina',
        'Bautismo',
        60,
        10,
        1,
        1,
        '/img/servicios/inmersiones/sta_marina_snorkel.jpg',
        NULL
    ),
    (
        11,
        'El Bull',
        'Esta inmersión se encuentra en la zona SW de la isla, fondo rocoso en forma de lajas, lo que permite la formación de numerosas viseras y pequeñas gateras, aquí la población de algas es muy abundante, así que ofrece una protección perfecta para todas las especies que allí habitan, como maragotas, durdos, julias, porredanos, momas, jargos, pulpos, centollos y nécoras.\n\nSi llevamos linterna y observamos los fondos de las gateras podemos encontrar algún congrio. Esta inmersión es perfecta para iniciarse en el video y la fotografía submarina.',
        NULL,
        'inmersion',
        'Isla de Santa Marina',
        'Course OWD',
        60,
        6,
        1,
        1,
        '/img/servicios/inmersiones/sta_marina_elbull.jpg',
        NULL
    ),
    (
        12,
        'Antártico',
        'La inmersión en este pecio es apta para todos los niveles, ya que sus restos se encuentran a escasa profundidad, en bajamares muy pronunciadas llega a asomar a la superficie parte de su estructura. Sus restos se encuentran esparcidos en línea paralela a las Quebrantas del Puntal, prácticamente enterrado en el fondo arenoso, apenas son visibles los restos de los mamparos y la cubierta alta, pero ofrece cantidad de recovecos entre sus restos, sirviendo de arrecife y protección a la multitudinaria vida marina que allí habita.\n\nNos encontraremos con centollos, nécoras, sepias, pulpos, cabrachos y cardúmenes de jargos, bogas y chicharro. Zona habitual de caza para lubinas, dentones y doradas, también nos encontrarnos con pequeñas familias de ballestas durante los meses de julio y agosto.\n\nTan solo prestar especial atención al salir a superficie, ya que esta zona está muy cerca del paso de buques hacia el puerto de Santander, por lo que es muy importante no perder la orientación realizando el',
        NULL,
        'inmersion',
        'Pecio',
        'Open Water',
        90,
        6,
        1,
        1,
        '/img/servicios/inmersiones/pecio_antartico.jpg',
        NULL
    ),
    (
        13,
        'Lolo Nin',
        'Pequeño pesquero de unos 20 metros de eslora y casco de madera, pertenecía a la flota de bajura con sede en el puerto del Barrio Pesquero de Santander. Se desconocen a ciencia cierta las causas del naufragio del Lolo Nin, hace aproximadamente veinte años, una vía de agua le envió a los fondos arenosos que se encuentran a media milla al norte de Cabo Menor. Sus restos reposan a unos 30 metros de profundidad, esparcido en tres pedazos por un área de unos 40 metros.\n\nInmersión:\n\nNormalmente siempre cae el arpeo en una zona próxima al pecio, ya que sus restos se encuentran en zona de corrientes y apenas se elevan unos dos metros del fondo, es difícil de localizar, por lo que hay que estar muy atento a la dirección de debemos tomar antes de soltar el cabo del arpeo si queremos localizar el pecio.\n\nEntre los restos más significativos se encuentra la maquinilla, donde aún están las artes de pesca, el resto es un amasijo de hierros y chapas que sirven de guarida a congrios, bogavantes y pulpos. No es un pecio que nos sorprenda en exceso, pero vale la pena indagar entre sus restos en busca de la abundante vida que allí se protege. Nos encontraremos grandes cardúmenes de salmonetes, fanecas, bogas, chicharros y jargos. Sitio predilecto para los grandes pulpos y las doradas de gran porte. Prestar especial atención a los cabos y restos de redes que se encuentran en torno a este pequeño pecio, sobre todo si la visibilidad es escasa.',
        NULL,
        'inmersion',
        'Pecio',
        'Advanced Open Water',
        60,
        4,
        1,
        1,
        '/img/servicios/inmersiones/pecio_lolo_nin.jpg',
        NULL
    ),
    (
        14,
        'Apnea Básica',
        'Este programa te proporciona la formación y el conocimiento necesario para realizar inmersiones de apnea con un compañero en un entorno de una piscina/ aguas confinadas a una profundidad de cinco metros. Recibirás la certificación Basic Freediving SSI después de finalizar este programa.',
        NULL,
        'curso',
        'Apnea',
        'Sin experiencia',
        45,
        8,
        1,
        1,
        '/img/servicios/cursos/apnea_basica.jpg',
        NULL
    ),
    (
        15,
        'Extended Range',
        'El programa Extended Range SSI te cualifica para realizar inmersiones a 40 metros de profundidad utilizando mezclas de nitrox hasta 50%. El programa se puede realizar con tu Sistema Total de Buceo estándar, una botella grande con válvula H o Y o una bibotella. Recibirás la certificación Extended Range SSI después de completar este programa.',
        NULL,
        'curso',
        'Técnico RX',
        'Advanced Open Water',
        200,
        9,
        1,
        1,
        '/img/servicios/cursos/extended_range.jpg',
        NULL
    ),
    (
        16,
        'Dive Guide',
        'Este programa te da las habilidades y la experiencia necesaria para guiar de forma segura a grupos de buceadores certificados, y es el primer paso hacia una carrera emocionante como Profesional de Buceo SSI sin la necesidad de realizar un programa de instructor. Los Dive Guides SSI profesionales pueden trabajar para Centros de Buceo SSI, guiar inmersiones en diferentes entornos y condiciones. Recibirás la certificación de Dive Guide SSI después de finalizar este programa.',
        NULL,
        'curso',
        'Profesionales',
        'Course OWD',
        180,
        4,
        1,
        1,
        '/img/servicios/cursos/dive_guide.jpg',
        NULL
    ),
    (
        17,
        'Submarinismo o buceo para niños',
        'Si hay un lugar donde los niños disfrutan es en el mar. Además de divertirse en la playa y practicar la natación, el mundo submarino atrae la atención de cualquier niño. ¿Te imaginas lo que puedes encontrar sumergiéndote bajo el agua? Sin duda, el submarinismo es una de las experiencias más mágicas que además puedes disfrutar en familia.\n\nLos niños están encantados con la cantidad de sorpresas que se pueden encontrar bajo el mar. Tal vez no encuentren sirenas, pero pueden darse de bruces con el propio Nemo y con infinidad de seres sorprendentes a los que no pueden acceder en la superficie. El descubrimiento, la aventura y también todos los conocimientos que pueden extraer de ese entorno nuevo son los principales atractivos para que los más pequeños se inicien en un deporte acuático como es el submarinismo o buceo para los niños.',
        NULL,
        'actividad',
        NULL,
        'Sin experiencia',
        180,
        5,
        1,
        1,
        '/img/servicios/actividades/sub_y_buceo_ninos.jpg',
        NULL
    ),
    (
        18,
        'Rutas de la naturaleza en barco y Snorkel',
        'Te ofrecemos la posibilidad de embarcarte en nuestras embarcaciones para deleitarte del paisaje que la Bahía ofrece, nos adentraremos en los espacios de especial protección para las aves y aprovecharemos la visita a la Isla de Mouro (Reserva Marina) para realizar snorkel (buceo con tubo), esto está organizado tanto para grupos reducidos (max. 10 personas por lancha). Te permitirá disfrutar de un paraje que no te dejará indiferente, los asistentes podrán observar especies como el águila pescadora, los grandes bandos de anátidas, la espátula común. Te sorprenderás al observar pequeñas manchas de bosque mediterráneo, con encinas y laureles en pleno arco Atlántico, lo diminuto que es el nido del Milano negro encaramado en un eucalipto, y que el ostrero es capaz de abrir las almejas finas con su potente pico.\n \nDonde realmente nos damos cuenta de la riqueza natural de la Isla de Mouro es en sus fondos, podemos encontrar gran parte de las especies mas representativas del Cantábrico. El hábitat marino en esta zona es complejo y cambiante en función de la época del año, pasaremos a los arenales someros de la zona sur a través de un sinfín de pasadizos y laberintos. El snorkel en esta isla es apto para todos los públicos, desde el que no tiene experiencia alguna, hasta el mas experto, encontrará en estos fondos, una experiencia única.\nEsta actividad tiene una duración aproximada de 2 horas, amena e interesante para niños y adultos, los aficionados a la fotografía de naturaleza y aves tendrán la oportunidad de capturar con sus cámaras singulares instantáneas.',
        NULL,
        'oferta',
        'Actividad',
        'Sin experiencia',
        200,
        6,
        1,
        1,
        '/img/servicios/ofertas/ruta_snorkel.png',
        NULL
    ),
    (
        19,
        'Bundle Kits',
        'Este paquete combina los programas Buceador Stress & Rescue y React Right SSI. Aprende reconocer y manejar el estrés, prevenir accidentes y manejar adecuadamente las situaciones de emergencia si ocurren. Recibirás las dos certificaciones Buceador Stress & Rescue SSI y React Right SSI después de finalizar ambos programas del paquete',
        NULL,
        'oferta',
        'Formación',
        'Stress & Rescue',
        400,
        7,
        1,
        1,
        '/img/servicios/ofertas/bundle_kit.jpg',
        NULL
    );

-- Inserciones para la tabla ACTIVIDADES_UBICACIONES
INSERT IGNORE INTO
    actividades_ubicaciones (id_actividad, id_ubicacion)
VALUES (1, 1),
    (2, 1),
    (3, 2),
    (4, 3),
    (5, 4),
    (6, 5),
    (7, 6),
    (8, 7),
    (9, 8),
    (10, 9),
    (11, 10),
    (12, 11),
    (13, 12),
    (14, 13),
    (15, 1),
    (16, 1),
    (17, 1),
    (18, 1),
    (19, 14);