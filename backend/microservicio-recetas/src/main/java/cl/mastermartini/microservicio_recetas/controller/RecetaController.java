
package cl.mastermartini.microservicio_recetas.controller;

import cl.mastermartini.microservicio_recetas.entity.Receta;
import cl.mastermartini.microservicio_recetas.service.RecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@CrossOrigin(origins = "*")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    // Listar todas las recetas
    @GetMapping
    public List<Receta> listarRecetas() {
        return recetaService.listarRecetas();
    }

    // Buscar receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Receta> buscarPorId(@PathVariable Integer id) {
        return recetaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear una receta
    @PostMapping
    public ResponseEntity<Receta> crearReceta(@RequestBody Receta receta) {
        Receta nuevaReceta = recetaService.guardarReceta(receta);
        return ResponseEntity.status(201).body(nuevaReceta);
    }

    // Actualizar una receta
    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizarReceta(
            @PathVariable Integer id,
            @RequestBody Receta receta) {

        return recetaService.buscarPorId(id)
                .map(recetaExistente -> {
                    receta.setId(id);
                    Receta actualizada = recetaService.guardarReceta(receta);
                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar una receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Integer id) {

        if (recetaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        recetaService.eliminarReceta(id);
        return ResponseEntity.noContent().build();
    }
}
