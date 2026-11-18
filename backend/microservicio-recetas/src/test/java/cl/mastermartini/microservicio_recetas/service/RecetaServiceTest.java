
package cl.mastermartini.microservicio_recetas.service;

import cl.mastermartini.microservicio_recetas.entity.Receta;
import cl.mastermartini.microservicio_recetas.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaService recetaService;

    private Receta receta;

    @BeforeEach
    void configurar() {
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
    void listarRecetas_debeDevolverTodasLasRecetas() {
        when(recetaRepository.findAll()).thenReturn(List.of(receta));

        List<Receta> resultado = recetaService.listarRecetas();

        assertEquals(1, resultado.size());
        assertEquals("Torta de chocolate", resultado.get(0).getTitulo());

        verify(recetaRepository).findAll();
    }

    @Test
    void buscarPorId_debeDevolverRecetaCuandoExiste() {
        when(recetaRepository.findById(1)).thenReturn(Optional.of(receta));

        Optional<Receta> resultado = recetaService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Torta de chocolate", resultado.get().getTitulo());

        verify(recetaRepository).findById(1);
    }

    @Test
    void buscarPorId_debeDevolverVacioCuandoNoExiste() {
        when(recetaRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Receta> resultado = recetaService.buscarPorId(99);

        assertTrue(resultado.isEmpty());

        verify(recetaRepository).findById(99);
    }


    @Test
    void guardarReceta_debeGuardarYDevolverLaReceta() {
        when(recetaRepository.save(receta)).thenReturn(receta);

        Receta resultado = recetaService.guardarReceta(receta);

        assertNotNull(resultado);
        assertEquals("Torta de chocolate", resultado.getTitulo());

        verify(recetaRepository).save(receta);
    }

    @Test
    void eliminarReceta_debeSolicitarLaEliminacionAlRepositorio() {
        recetaService.eliminarReceta(1);

        verify(recetaRepository).deleteById(1);
    }

}
