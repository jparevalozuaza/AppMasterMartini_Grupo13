package cl.duoc.mastermartini.repository

import cl.duoc.mastermartini.model.Curso
import cl.duoc.mastermartini.model.Leccion

object CursoRepository {

    val cursos = listOf(

        Curso(
            id = 1,
            titulo = "Introducción a la Chocolatería",
            categoria = "Chocolatería",
            descripcion = "Aprende los fundamentos del trabajo con chocolate y sus principales técnicas.",
            lecciones = listOf(
                Leccion(
                    id = 101,
                    titulo = "Conociendo el chocolate",
                    descripcion = "Origen, tipos y características del chocolate.",
                    contenido = "El chocolate se obtiene a partir del cacao. Existen diferentes variedades, como chocolate negro, con leche y blanco. Cada uno tiene características propias de sabor, textura y composición."
                ),
                Leccion(
                    id = 102,
                    titulo = "Tipos de chocolate",
                    descripcion = "Diferencias entre chocolate negro, con leche y blanco.",
                    contenido = "El chocolate negro contiene cacao, manteca de cacao y azúcar. El chocolate con leche incorpora leche en su composición. El chocolate blanco utiliza manteca de cacao, azúcar y leche, sin sólidos de cacao."
                ),
                Leccion(
                    id = 103,
                    titulo = "Herramientas básicas",
                    descripcion = "Utensilios necesarios para trabajar con chocolate.",
                    contenido = "Para trabajar con chocolate se pueden utilizar espátulas, bowls, termómetros, moldes y rasquetas. Estas herramientas ayudan a controlar la temperatura y conseguir buenos resultados."
                )
            )
        ),

        Curso(
            id = 2,
            titulo = "Fundamentos de Pastelería",
            categoria = "Pastelería",
            descripcion = "Conoce las preparaciones y técnicas esenciales de la pastelería.",
            lecciones = listOf(
                Leccion(
                    id = 201,
                    titulo = "Ingredientes básicos",
                    descripcion = "Conoce los ingredientes más utilizados.",
                    contenido = "La harina, el azúcar, los huevos, la mantequilla y la leche son ingredientes habituales en pastelería. Cada uno cumple una función importante en la estructura, textura y sabor de las preparaciones."
                ),
                Leccion(
                    id = 202,
                    titulo = "Técnicas de mezclado",
                    descripcion = "Aprende diferentes formas de integrar ingredientes.",
                    contenido = "Existen distintas técnicas de mezclado, como batir, incorporar, mezclar y plegar. La técnica utilizada depende de la preparación y del resultado que se desea obtener."
                ),
                Leccion(
                    id = 203,
                    titulo = "Preparaciones básicas",
                    descripcion = "Introducción a masas y cremas.",
                    contenido = "Las masas y cremas son preparaciones fundamentales de la pastelería. Se utilizan como base para elaborar tortas, tartas, postres y diferentes productos de vitrina."
                )
            )
        ),

        Curso(
            id = 3,
            titulo = "Introducción a la Panadería",
            categoria = "Panadería",
            descripcion = "Aprende los fundamentos de las masas y la elaboración de pan.",
            lecciones = listOf(
                Leccion(
                    id = 301,
                    titulo = "Ingredientes de panadería",
                    descripcion = "Conoce los ingredientes principales del pan.",
                    contenido = "La harina, el agua, la levadura y la sal son ingredientes fundamentales para elaborar muchos tipos de pan. Sus proporciones influyen en la textura y el resultado final."
                ),
                Leccion(
                    id = 302,
                    titulo = "Amasado y fermentación",
                    descripcion = "Comprende las etapas de preparación de una masa.",
                    contenido = "El amasado permite desarrollar la estructura de la masa. Durante la fermentación, la levadura produce gases que ayudan a aumentar el volumen y desarrollar aromas."
                ),
                Leccion(
                    id = 303,
                    titulo = "Horneado del pan",
                    descripcion = "Conoce los aspectos básicos de la cocción.",
                    contenido = "El horneado transforma la masa en pan mediante la aplicación de calor. La temperatura y el tiempo dependen del tamaño, tipo de masa y características del producto."
                )
            )
        ),

        Curso(
            id = 4,
            titulo = "Fundamentos de Heladería",
            categoria = "Heladería",
            descripcion = "Descubre los ingredientes y procesos básicos para elaborar helados.",
            lecciones = listOf(
                Leccion(
                    id = 401,
                    titulo = "Ingredientes del helado",
                    descripcion = "Conoce los componentes de una mezcla para helado.",
                    contenido = "Las mezclas para helado pueden contener leche, crema, azúcar, estabilizantes y diferentes ingredientes saborizantes. Su equilibrio influye en la textura y el sabor."
                ),
                Leccion(
                    id = 402,
                    titulo = "Preparación de mezclas",
                    descripcion = "Aprende los fundamentos de la elaboración.",
                    contenido = "La preparación de una mezcla consiste en integrar correctamente los ingredientes. Es importante respetar las proporciones y los procesos de higiene y conservación."
                ),
                Leccion(
                    id = 403,
                    titulo = "Congelación y conservación",
                    descripcion = "Conoce los procesos de congelación del helado.",
                    contenido = "La congelación permite obtener la textura característica del helado. Una temperatura adecuada y una correcta conservación ayudan a mantener su calidad."
                )
            )
        )
    )
}
