# Resumen del Miniproyecto 02: Administrador de Tareas Locales

Se ha implementado el administrador de tareas de forma exitosa usando Jetpack Compose y Room, cumpliendo con la consigna para mantenerlo **súper sencillo**.

## Características implementadas:

1. **Persistencia con Room Database**
   - Creado el modelo `Task` (`data/Task.kt`) con los campos necesarios (id, title, description, isCompleted, timestamp).
   - Creado `TaskDao` con métodos simples para Insertar, Actualizar, Borrar, Leer todas las tareas y Borrar las tareas completadas.
   - Creada la base de datos local `AppDatabase`.

2. **ViewModel y Estado**
   - Creado `TaskViewModel` para gestionar el estado de las listas y la interacción con Room mediante Coroutines.

3. **Interfaz de Usuario (Jetpack Compose)**
   - **`TopAppBar`**: Una barra superior que incluye un menú desplegable (tres puntitos) con las opciones de:
      - Ordenar tareas por Estado y Fecha.
      - Eliminar todas las tareas completadas.
   - **`LazyColumn`**: Lista dinámica de tareas. Cada ítem (`TaskItem`) cuenta con:
      - Un `Checkbox` para marcar la tarea como completada (el texto se tacha).
      - Textos para título, descripción y fecha de creación.
      - Un ícono de papelera para eliminar la tarea.
   - **`FloatingActionButton`**: Botón inferior derecho para agregar una nueva tarea.
   - **`TaskDialog`**: Un modal (AlertDialog) para la creación y edición de tareas con campos sencillos.

4. **Configuración de Gradle**
   - Agregadas las dependencias de **Room** (runtime, ktx, compiler).
   - Configurado **KSP (Kotlin Symbol Processing)** de manera óptima para compilar los DAOs en Kotlin 2.2+.
   - Añadidas dependencias de **Material Icons Extended** para enriquecer los íconos de la aplicación.

### ¿Qué sigue para ti?
La aplicación ya compila y funciona perfectamente. Solamente te hace falta generar las **capturas de pantalla** de la app y subirlas junto con el repositorio a GitHub para armar el PDF que te pide el documento de evaluación.
