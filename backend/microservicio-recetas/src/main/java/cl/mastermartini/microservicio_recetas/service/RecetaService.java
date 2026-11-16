package cl.mastermartini.microservicio_recetas.service;

import cl.mastermartini.microservicio_recetas.entity.Receta;
import cl.mastermartini.microservicio_recetas.repository.RecetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {

    private final RecetaRepository recetaRepository;

    public RecetaService(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    public List<Receta> listarRecetas() {
        return recetaRepository.findAll();
    }

    public Optional<Receta> buscarPorId(Integer id) {
        return recetaRepository.findById(id);
    }

    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }

    public void eliminarReceta(Integer id) {
        recetaRepository.deleteById(id);
    }
}
