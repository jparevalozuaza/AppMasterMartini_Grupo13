package cl.duoc.mastermartini.repository

import cl.duoc.mastermartini.model.Contenido

object ContenidoRepository {

    val contenidos = listOf(
        Contenido(
            titulo = "Introducción a la chocolatería",
            categoria = "Chocolatería",
            descripcion = "Conoce los fundamentos para trabajar con chocolate."
        ),
        Contenido(
            titulo = "Técnicas de templado",
            categoria = "Chocolatería",
            descripcion = "Aprende a conseguir un chocolate brillante y firme."
        ),
        Contenido(
            titulo = "Elaboración de bombones",
            categoria = "Chocolatería",
            descripcion = "Descubre los pasos básicos para elaborar bombones."
        ),
        Contenido(
            titulo = "Pastelería de vitrina",
            categoria = "Pastelería",
            descripcion = "Conoce preparaciones para vitrinas de pastelería."
        ),
        Contenido(
            titulo = "Técnicas de masas",
            categoria = "Pastelería",
            descripcion = "Aprende conceptos básicos sobre masas dulces."
        ),
        Contenido(
            titulo = "Decoración de tortas",
            categoria = "Pastelería",
            descripcion = "Explora técnicas iniciales de decoración."
        ),
        Contenido(
            titulo = "Técnicas de panadería",
            categoria = "Panadería",
            descripcion = "Conoce procesos básicos para elaborar panes."
        ),
        Contenido(
            titulo = "Fermentación de masas",
            categoria = "Panadería",
            descripcion = "Aprende cómo influye la fermentación en el pan."
        ),
        Contenido(
            titulo = "Elaboración de bollería",
            categoria = "Panadería",
            descripcion = "Descubre las bases de las masas de bollería."
        ),
        Contenido(
            titulo = "Introducción a la heladería",
            categoria = "Heladería",
            descripcion = "Conoce los fundamentos de la elaboración de helados."
        ),
        Contenido(
            titulo = "Bases para helados",
            categoria = "Heladería",
            descripcion = "Aprende sobre los componentes de una mezcla para helados."
        ),
        Contenido(
            titulo = "Sabores y combinaciones",
            categoria = "Heladería",
            descripcion = "Explora combinaciones para crear nuevos sabores."
        )
    )
}
