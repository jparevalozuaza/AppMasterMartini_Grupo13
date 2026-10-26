package cl.duoc.mastermartini.repository

import cl.duoc.mastermartini.model.Contenido

object ContenidoRepository {

    val contenidos = listOf(
        Contenido(
            id = 1,
            titulo = "Introducción a la chocolatería",
            categoria = "Chocolatería",
            descripcion = "Conoce los fundamentos para trabajar con chocolate."
        ),
        Contenido(
            id = 2,
            titulo = "Técnicas de templado",
            categoria = "Chocolatería",
            descripcion = "Aprende a conseguir un chocolate brillante y firme."
        ),
        Contenido(
            id = 3,
            titulo = "Elaboración de bombones",
            categoria = "Chocolatería",
            descripcion = "Descubre los pasos básicos para elaborar bombones."
        ),
        Contenido(
            id = 4,
            titulo = "Pastelería de vitrina",
            categoria = "Pastelería",
            descripcion = "Conoce preparaciones para vitrinas de pastelería."
        ),
        Contenido(
            id = 5,
            titulo = "Técnicas de masas",
            categoria = "Pastelería",
            descripcion = "Aprende conceptos básicos sobre masas dulces."
        ),
        Contenido(
            id = 6,
            titulo = "Decoración de tortas",
            categoria = "Pastelería",
            descripcion = "Explora técnicas iniciales de decoración."
        ),
        Contenido(
            id = 7,
            titulo = "Técnicas de panadería",
            categoria = "Panadería",
            descripcion = "Conoce procesos básicos para elaborar panes."
        ),
        Contenido(
            id = 8,
            titulo = "Fermentación de masas",
            categoria = "Panadería",
            descripcion = "Aprende cómo influye la fermentación en el pan."
        ),
        Contenido(
            id = 9,
            titulo = "Elaboración de bollería",
            categoria = "Panadería",
            descripcion = "Descubre las bases de las masas de bollería."
        ),
        Contenido(
            id = 10,
            titulo = "Introducción a la heladería",
            categoria = "Heladería",
            descripcion = "Conoce los fundamentos de la elaboración de helados."
        ),
        Contenido(
            id = 11,
            titulo = "Bases para helados",
            categoria = "Heladería",
            descripcion = "Aprende sobre los componentes de una mezcla para helados."
        ),
        Contenido(
            id = 12,
            titulo = "Sabores y combinaciones",
            categoria = "Heladería",
            descripcion = "Explora combinaciones para crear nuevos sabores."
        )
    )
}
