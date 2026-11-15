package cl.mastermartini.microservicio_recetas.repository;

import cl.mastermartini.microservicio_recetas.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {

}
