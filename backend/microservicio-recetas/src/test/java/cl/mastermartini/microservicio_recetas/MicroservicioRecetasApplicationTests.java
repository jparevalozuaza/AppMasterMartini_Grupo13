package cl.mastermartini.microservicio_recetas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MicroservicioRecetasApplicationTests {

	@Test
	void main_debeEjecutarSpringApplicationRun() {

		try (var springApplication = mockStatic(SpringApplication.class)) {

			MicroservicioRecetasApplication.main(new String[]{});

			springApplication.verify(() ->
					SpringApplication.run(
							MicroservicioRecetasApplication.class,
							new String[]{}
					)
			);
		}
	}
}
