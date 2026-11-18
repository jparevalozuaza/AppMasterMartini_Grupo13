
package cl.mastermartini.microservicio_recetas.controller;

import cl.mastermartini.microservicio_recetas.entity.Receta;
import cl.mastermartini.microservicio_recetas.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @Autowired
    private MockMvc mockMvc;

    private Receta receta;

    @BeforeEach
    void configurar() {
        RecetaController controller = new RecetaController(recetaService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();


        receta = new Receta();
        receta.setId(1);
        receta.setTitulo("Torta de chocolate");
        receta.setDescripcion("Torta húmeda de chocolate.");
        receta.setCategoria("Pasteleria");
        receta.setTiempoPreparacion(60);
        receta.setIngredientes("Harina, chocolate y huevos.");
        receta.setPreparacion("Mezclar y hornear.");
    }


    @Test
    void listarRecetas_debeResponder200YDevolverLasRecetas() throws Exception {

        when(recetaService.listarRecetas()).thenReturn(List.of(receta));

        mockMvc.perform(get("/api/recetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Torta de chocolate"));

        verify(recetaService).listarRecetas();
    }

    @Test
    void buscarPorId_debeResponder200CuandoExiste() throws Exception {

        when(recetaService.buscarPorId(1)).thenReturn(Optional.of(receta));

        mockMvc.perform(get("/api/recetas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Torta de chocolate"));

        verify(recetaService).buscarPorId(1);
    }

    @Test
    void buscarPorId_debeResponder404CuandoNoExiste() throws Exception {

        when(recetaService.buscarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recetas/99"))
                .andExpect(status().isNotFound());

        verify(recetaService).buscarPorId(99);
    }

    @Test
    void crearReceta_debeResponder201CuandoSeCrea() throws Exception {

        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(receta);

        mockMvc.perform(post("/api/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Torta de chocolate",
                                    "descripcion": "Torta húmeda de chocolate.",
                                    "categoria": "Pasteleria",
                                    "tiempoPreparacion": 60,
                                    "ingredientes": "Harina, chocolate y huevos.",
                                    "preparacion": "Mezclar y hornear."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Torta de chocolate"));

        verify(recetaService).guardarReceta(any(Receta.class));
    }

    @Test
    void actualizarReceta_debeResponder200CuandoExiste() throws Exception {

        when(recetaService.buscarPorId(1)).thenReturn(Optional.of(receta));
        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(receta);

        mockMvc.perform(put("/api/recetas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Torta de chocolate especial",
                                    "descripcion": "Torta especial.",
                                    "categoria": "Pasteleria",
                                    "tiempoPreparacion": 70,
                                    "ingredientes": "Harina, chocolate, huevos y leche.",
                                    "preparacion": "Mezclar y hornear."
                                }
                                """))
                .andExpect(status().isOk());

        verify(recetaService).buscarPorId(1);
        verify(recetaService).guardarReceta(any(Receta.class));
    }

    @Test
    void actualizarReceta_debeResponder404CuandoNoExiste() throws Exception {

        when(recetaService.buscarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/recetas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Receta inexistente"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(recetaService).buscarPorId(99);
        verify(recetaService, never()).guardarReceta(any(Receta.class));
    }

    @Test
    void eliminarReceta_debeResponder204CuandoExiste() throws Exception {

        when(recetaService.buscarPorId(1)).thenReturn(Optional.of(receta));

        mockMvc.perform(delete("/api/recetas/1"))
                .andExpect(status().isNoContent());

        verify(recetaService).buscarPorId(1);
        verify(recetaService).eliminarReceta(1);
    }

    @Test
    void eliminarReceta_debeResponder404CuandoNoExiste() throws Exception {

        when(recetaService.buscarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/recetas/99"))
                .andExpect(status().isNotFound());

        verify(recetaService).buscarPorId(99);
        verify(recetaService, never()).eliminarReceta(99);
    }

}
