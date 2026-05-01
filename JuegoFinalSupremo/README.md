# Juego Final Supremo

Proyecto JavaFX/Maven para una practica de juego por habitaciones con estructuras de datos propias.

## Ejecutar

En este equipo Maven necesita `JAVA_HOME`:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
.\mvnw.cmd javafx:run
```

## Probar

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
.\mvnw.cmd test
```

Resultado verificado: 7 tests ejecutados, 0 fallos.

## Partes principales

- `src/main/java/org/example/juegofinalsupremo/HelloApplication.java`: interfaz JavaFX.
- `src/main/java/org/example/juegofinalsupremo/model`: jugador, habitacion, celda, objeto, enemigo, estado, motor y log.
- `src/main/java/org/example/juegofinalsupremo/data`: lista, cola, pila y grafo de rejilla con BFS.
- `src/main/java/org/example/juegofinalsupremo/contracts`: interfaces de acciones y repositorio.
- `src/main/java/org/example/juegofinalsupremo/exceptions`: excepciones de juego, movimiento, accion y almacenamiento.
- `src/main/java/org/example/juegofinalsupremo/io`: guardado y carga JSON.
- `src/main/resources/samples/sample-game.json`: JSON de ejemplo.
- `src/test/java/org/example/juegofinalsupremo`: pruebas JUnit.
- `docs`: diseno, UML, boceto y diario IA.

## Pendiente para entrega completa

- Convertir la memoria Markdown en PDF y poner portada con nombres reales de alumnos.
- Exportar los `.puml` a imagen/PDF si el profesor no acepta PlantUML fuente.
- Grabar el video obligatorio con todos los alumnos.
- Crear repositorio GitHub y ZIP final.
- Ampliar pruebas con mas casos de combate, puertas, inventario y errores JSON.
- Implementar varias habitaciones conectadas por un grafo general si se quiere subir nota.
- Implementar Dijkstra si se anaden costes de movimiento variables.
- Revisar manualmente la interfaz JavaFX en pantalla antes de entregar.
