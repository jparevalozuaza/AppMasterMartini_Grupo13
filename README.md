# Master Martini – Aplicación Móvil



## 1. Descripción del proyecto



**Master Martini** es una aplicación móvil desarrollada para Master Martini Chile SPA, orientada a emprendedores y profesionales del área gastronómica.



La aplicación permite consultar recetas, explorar contenidos gastronómicos y acceder a recursos de aprendizaje relacionados con distintas especialidades, como pastelería, chocolatería, heladería, bollería y otros productos de repostería.



El proyecto fue desarrollado como parte de la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**.



## 2. Integrantes



| Integrante | Responsabilidad |

|---|---|

| José Miguel Antinao Olave | Desarrollo backend, microservicio de recetas, base de datos, endpoints e integración |

| Juan Pablo Arévalo | Desarrollo de la aplicación Android, funcionalidades móviles, pruebas e integración |



## 3. Tecnologías utilizadas



### Aplicación Android

- Kotlin.

- Android Studio.

- Jetpack Compose.

- Retrofit para consumo de servicios REST.

- ViewModel para gestión de estados.

- Coroutines para operaciones asíncronas.



### Backend

- Java 21.

- Spring Boot.

- Maven.

- Spring Data JPA.

- Hibernate.



### Base de datos

- MySQL 8.4.

- Docker para ejecutar el contenedor de base de datos.



### Pruebas y control de versiones

- JUnit.

- MockK.

- JaCoCo para medición de cobertura.

- Git y GitHub.



## 4. Funcionalidades principales



- Visualización de recetas gastronómicas.

- Búsqueda y exploración de contenidos.

- Consulta de detalles de recetas.

- Creación de recetas mediante operaciones CRUD.

- Edición de recetas existentes.

- Eliminación de recetas.

- Consumo de recetas desde una API externa.

- Visualización de contenidos y recursos de aprendizaje.

- Gestión de estados de la aplicación mediante ViewModel.



## 5. Arquitectura del proyecto



El proyecto utiliza una arquitectura cliente-servidor.



La aplicación Android consume los servicios REST mediante Retrofit. El backend está desarrollado con Spring Boot y utiliza MySQL para almacenar las recetas.



**Flujo de comunicación:**



Android (Kotlin y Jetpack Compose) → Retrofit → API REST Spring Boot → Spring Data JPA → MySQL.



Además, la aplicación consume una API externa mediante Retrofit para obtener recetas de DummyJSON.



## 6. Microservicio propio: Recetas



El microservicio está desarrollado con Spring Boot y permite administrar las recetas almacenadas en MySQL.



**Configuración:**

- Puerto del backend: `8080`.

- Puerto del contenedor MySQL: `3306`.

- Puerto de conexión desde el equipo anfitrión: `3307`.

- Base de datos: `master_martini_recetas`.



### Endpoints disponibles



URL base:



`http://localhost:8080`



| Método | Endpoint | Descripción |

|---|---|---|

| GET | `/api/recetas` | Obtener todas las recetas |

| GET | `/api/recetas/{id}` | Obtener una receta por ID |

| POST | `/api/recetas` | Crear una receta |

| PUT | `/api/recetas/{id}` | Actualizar una receta |

| DELETE | `/api/recetas/{id}` | Eliminar una receta |



## 7. API externa



Se utiliza **DummyJSON** como servicio externo para obtener recetas mediante solicitudes HTTP realizadas con Retrofit.



URL base:



`https://dummyjson.com/`



Endpoints principales:

- `GET /recipes`: obtener recetas.

- `GET /recipes/{id}`: obtener una receta por identificador.



La API permite consultar información de recetas externas. Las operaciones de escritura que simula DummyJSON no persisten realmente en su servidor, por lo que los cambios de esas operaciones se mantienen en la aplicación.



## 8. Requisitos para ejecutar el proyecto



- Android Studio.

- JDK 21 para ejecutar el backend.

- Docker Desktop.

- Git.

- Emulador Android o dispositivo compatible.



## 9. Instrucciones de ejecución



### 9.1. Obtener el código fuente



Clonar el repositorio:



```bash

git clone https://github.com/jparevalozuaza/AppMasterMartini_Grupo13.git

```



Ingresar al proyecto:



```bash

cd AppMasterMartini_Grupo13

```



### 9.2. Configurar y ejecutar la base de datos



La configuración de conexión utiliza variables de entorno definidas en un archivo `.env` ubicado en la carpeta `backend`.



El archivo debe contener las variables necesarias para configurar MySQL y la conexión del backend. No se deben publicar contraseñas ni credenciales en el repositorio.



Iniciar el contenedor MySQL utilizando el archivo de configuración Docker Compose del proyecto.



### 9.3. Ejecutar el backend



Ingresar a la carpeta del microservicio:



```bash

cd backend/microservicio-recetas

```



Ejecutar en Linux o macOS:



```bash

./mvnw spring-boot:run

```



En Windows:



```bash

mvnw.cmd spring-boot:run

```



El servicio estará disponible en:



`http://localhost:8080`



Se puede comprobar su funcionamiento consultando:



`http://localhost:8080/api/recetas`



### 9.4. Ejecutar la aplicación Android



1. Abrir Android Studio.

2. Seleccionar la carpeta del proyecto.

3. Esperar la sincronización de Gradle.

4. Iniciar un emulador Android o conectar un dispositivo.

5. Ejecutar la aplicación desde Android Studio.



**Importante para el emulador:** la configuración de desarrollo utiliza `127.0.0.1:8080` como dirección del backend. Para conectar el emulador con el backend que se ejecuta en el equipo anfitrión, utilizar:



```bash

adb reverse tcp:8080 tcp:8080

```



Este comando debe ejecutarse nuevamente cuando sea necesario restablecer la conexión después de reiniciar el emulador.



## 10. Pruebas y cobertura



El proyecto incorpora pruebas unitarias para validar el funcionamiento de componentes de la aplicación, incluyendo repositorios, ViewModels, modelos y lógica de negocio.



Se utilizaron herramientas como JUnit y MockK, junto con JaCoCo para medir la cobertura.



**Cobertura de pruebas alcanzada: 80%.**



Para ejecutar las pruebas unitarias desde la raíz del proyecto:



```bash

./gradlew testDebugUnitTest

```



Para generar el informe de cobertura:



```bash

./gradlew :app:createDebugUnitTestCoverageReport

```



El informe se genera en:



`app/build/reports/coverage/test/debug/index.html`



## 11. APK firmado



La aplicación cuenta con una versión APK de lanzamiento generada mediante Android Studio y firmada utilizando un archivo de tipo Java Keystore (`.jks`).



**Información de la compilación:**

- Tipo de compilación: Release.

- Formato de entrega: APK.

- Firma digital: configurada.

- Alias del certificado: `mastermartini`.



El APK firmado se adjunta como parte de la entrega académica.



El archivo `.jks` y sus credenciales se mantienen respaldados y protegidos. No se publican en el repositorio GitHub.



## 12. Control de versiones y colaboración



El desarrollo se gestionó mediante Git y GitHub, utilizando ramas de trabajo y Pull Requests para integrar los cambios al proyecto principal.



**Repositorio GitHub:**



https://github.com/jparevalozuaza/AppMasterMartini_Grupo13



## 13. Evidencias



Se consideran como evidencias del desarrollo:



- Pantallas principales de la aplicación.

- Funcionamiento del CRUD de recetas.

- Conexión entre Android y el microservicio.

- Consumo de la API externa.

- Ejecución de las pruebas unitarias.

- Informe de cobertura del 80%.

- Generación y firma del APK.

- Historial de commits y Pull Requests.



## 14. Consideraciones finales



Master Martini integra una aplicación móvil Android, un microservicio desarrollado con Spring Boot, una base de datos MySQL y el consumo de una API externa.



El proyecto contempla operaciones CRUD, pruebas unitarias, medición de cobertura, control de versiones y generación de una aplicación firmada para su distribución y evaluación.