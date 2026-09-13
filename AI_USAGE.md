## Documentación de uso de IA

**Herramientas utilizadas:** Google Gemini Pro / Antigravity.

**Prompts decisivos utilizados:**

1. **Prompt de análisis inicial:** *"Mira, este es el proyecto (parcial.pdf). Dime qué es lo más importante, qué debo tener en cuenta y qué es lo que más me exigirá."*
   * **Por qué fue necesario:** Sirvió para extraer las restricciones críticas del documento que no eran obvias a simple vista (como la prohibición de librerías externas de grafos, la necesidad de coincidir las frases carácter por carácter y los límites de tamaño para el dibujado de la GUI).

2. **Prompt de adaptación de estructuras:** *"Teniendo como base la forma en que programa mi profesor (código de ejemplo), dime cuáles son las estructuras de datos más importantes que debo usar y qué debo tener en cuenta para adaptar cada algoritmo (Dijkstra, Floyd-Warshall, Kruskal) a este proyecto."*
   * **Por qué fue necesario:** El código del profesor usaba tipos `int` y resolvía problemas estándar de minimización. Este prompt permitió definir la necesidad de migrar a `long` para evitar desbordamientos numéricos y conceptualizar cómo invertir la lógica de Bellman-Ford para un problema de maximización de "churun".

3. **Prompt de generación final:** *"Genérame los algoritmos de la Tarea 2, 3 y 4 teniendo en cuenta estas indicaciones: usa `long`, implementa Union-Find con Path Compression para Kruskal, maneja los valores centinelas (infinito) sin hacer aritmética sobre ellos, y asegúrate de retornar los mensajes exactos como 'Nina is very sad'."*
   * **Por qué fue necesario:** Fue el comando definitivo para obtener código modular, limpio, independiente de la GUI y que pasara los casos de prueba exactos exigidos por el evaluador automático.

**Errores o respuestas subóptimas de la IA y cómo se solucionaron:**
* **Problema:** La IA intentaba sumar la distancia actual con el peso de la arista sin verificar si la distancia actual era el valor "infinito", lo que causaba un desbordamiento numérico (overflow).
* **Solución:** Se le indicó explícitamente en el prompt #3 que nunca hiciera aritmética sobre los centinelas. Se corrigió añadiendo validaciones `if (dist != NO_ROUTE)` antes de cualquier suma.

**Lo que aprendí:**
* Comprendí que Floyd-Warshall puede usarse no solo para distancias, sino para detectar nodos afectados por ciclos positivos si se verifica la diagonal principal de la matriz (`dist[k][k] > 0`).
* Entendí la importancia del *Path Compression* en Union-Find, ya que aplana el árbol de búsqueda haciendo que las validaciones de ciclos en Kruskal sean de tiempo casi constante.