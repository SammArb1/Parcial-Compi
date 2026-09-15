# The Feline Graph Chronicles 🐾

Proyecto final del curso de Lenguajes y Compiladores. Consiste en un sistema algorítmico y visual basado en teoría de grafos para ayudar a los heroicos gatos **Pola** y **Minerva** a salvar a **Nina** de las garras del villano **Limón**.

## 👥 Integrantes del Equipo
* **Samuel Arbeláez** - Desarrollo Core Algorítmico (Misiones 2, 3, 4) y Estandarización de Arquitectura.
* **Nicolás Aguas** - Desarrollo Core Algorítmico (Misión 1) y Motor de Dibujo (Java 2D GUI).

## 🏗 Arquitectura y Estructura del Proyecto

El proyecto está diseñado bajo un patrón arquitectónico modular y estrictamente desacoplado, garantizando que el núcleo matemático (`core`) sea 100% independiente de la capa de visualización (`gui`).

* `src/main/java/com/felinechronicles/core/`: Contiene los algoritmos puros sin una sola dependencia UI.
  * **Misión 1 (BFS/DFS):** `GridPathfinder` evalúa laberintos y minas iterativamente.
  * **Misión 2 (Dijkstra):** `DijkstraPathfinder` busca la ruta más corta evadiendo barreras numéricas extremas con `PriorityQueue`.
  * **Misión 3 (Floyd-Warshall & Bellman-Ford):** `ChurunMaximizer` rastrea ganancias en rutas dirigidas detectando y propagando "ciclos infinitos".
  * **Misión 4 (Kruskal & Union-Find):** `NetworkReconnector` reconstruye los cables críticos de la ciudad usando *Path Compression* y *Union by Rank*.
* `src/main/java/com/felinechronicles/gui/`: Interfaz Gráfica (`MainApp.java`) desarrollada en **Java Swing** y **Java 2D**. Incluye "El Ojo de Minerva", un lienzo inteligente que grafica nodos y rutas aplicando límites seguros de renderizado para evitar bloqueos del sistema.
* `src/test/java/com/felinechronicles/core/`: Batería de pruebas automatizadas `GraphTests.java` para validar estrictamente cada resultado contra los requerimientos del evaluador (juez online).

## 🚀 Cómo Compilar y Ejecutar

El proyecto utiliza exclusivamente librerías estándar del JDK 17 (Cero dependencias externas, cero frameworks adicionales). Compilarlo y ejecutarlo desde la consola es rápido y seguro.

### Desde Windows (PowerShell / CMD)
Abre tu terminal, posiciónate en la raíz del proyecto (`Parcial`) y ejecuta los siguientes comandos:

1. **Crear la carpeta de binarios (si no existe) y Compilar todo el código fuente:**
   ```bash
   mkdir -p bin
   javac -d bin src/main/java/com/felinechronicles/core/*.java src/main/java/com/felinechronicles/gui/*.java
   ```
2. **Ejecutar la Interfaz Gráfica:**
   ```bash
   java -cp bin com.felinechronicles.gui.MainApp
   ```

*(Opcional)* Para correr los Tests algorítmicos sin abrir la interfaz gráfica:
```bash
javac -d bin src/main/java/com/felinechronicles/core/*.java src/test/java/com/felinechronicles/core/*.java
java -cp bin com.felinechronicles.core.GraphTests
```

## ⚠️ Limitaciones conocidas y Reglas de Seguridad
* **Protección de la GUI:** Para evitar congelamientos de la Interfaz (`OOM` o sobrecarga del thread gráfico `AWT-EventQueue`), la visualización de Java 2D bloquea gráficamente el renderizado si los datos de entrada superan los topes exigidos por el PDF (Grillas > 50x50, o Grafos > 60 nodos / 300 aristas). 
* Si se superan estos topes, el lienzo mostrará un mensaje de protección rojo; sin embargo, el motor **Core** continuará el cálculo y la consola de resultados imprimirá la respuesta normalmente sin importar el tamaño gigantesco.
* **Protección de Consola:** Cualquier formato corrupto en el área de texto (input) será atrapado y devuelto como un mensaje visual legible. Bajo ninguna circunstancia el programa generará un *Stack Trace* crudo en la terminal de Java, cumpliendo el requerimiento estricto.