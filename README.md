# Programación de Servicios y Procesos - Actividad de Aprendizaje 2 (AA2)
Aplicación de escritorio desarrollada con JavaFx, RxJava y Retrofit en Java para la asignatura de Programación de Servicios y Procesos del 2º curso de DAM

## Requisitos para la realización de la actividad
Se pide una serie de requisitos a cumplir para desarrollar la actividad. Esta consiste en una aplicación que, utilizando técnicas de programación reactiva, consuma al menos una API y presente la información al usuario en un interfaz gráfico desarrollado con
JavaFX. Para el consumo de la API se utilizará la librería Retrofit.

### Requisitos obligatorios
✅ 1. La aplicación deberá utilizar técnicas de programación reactiva
utilizando la librería RxJava en algún momento.

✅ 2. Desde la interfaz de la aplicación se podrán realizar (al menos) 2 llamadas diferentes a la API (2 endpoints diferentes 
(0.5 puntos cada uno)). Estas 2 llamadas deben devolver listados de ítems/elementos/objetos.

✅ 3. En la interfaz de la aplicación, se mostrará información detallada de los ítems de los dos listados anteriores. Utilizar la clase ObservableList de JavaFX para la visualización de los contenidos en los diferentes componentes de JavaFX que decidas utilizar. Si los componentes son de tipo ListView (0.5 puntos); si son de tipo TableView (1 punto)

✅ 4. Todas las operaciones de carga de datos se harán en segundo plano (de manera concurrente).

✅ 5. La aplicación implementará al menos una operación de búsqueda (0.5 puntos) y filtrado (0.5 puntos) sobre los datos previamente cargados de la API.

### Requisitos opcionales
✅ 1. La aplicación podrá cargar y mostrar en la interfaz algún tipo de contenido gráfico a partir de información dada por la API (una foto, por ejemplo) (1 punto).

⬜ 2. La aplicación se conectará con más de una API (1 punto).

⬜ 3. La aplicación permitirá la exportación del contenido devuelto por la API a un fichero CSV. Es importante que cada atributo de los ítems estén en una columna diferente (0.5 puntos). Además de la exportación a CSV, se podrá comprimir en ZIP el fichero CSV (0.5 puntos). Utilizar CompletableFuture para la implementación.

⬜ 4. Crear, utilizando WebFlux, un pequeño servicio web relacionado con la API seleccionada y consúmelo desde alguna zona de la aplicación JavaFX utilizando WebClient (1 punto).

⬜ 5. Realizar el seguimiento del proyecto utilizando la plataforma GitHub para almacenar el código y gestionando las issues (bug, mejoras, . . .) a medida que se vaya trabajando en él. Al menos tiene que haber 2 ramas: main y develop (0.5 puntos). También puede haber issues y ramas para cada uno de las funcionalidades (0.5 puntos).