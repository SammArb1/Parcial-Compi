package com.felinechronicles.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkReconnector {

    // Clase interna estatica para representar un cable (arista) en el algoritmo de Kruskal
    public static class Edge implements Comparable<Edge> {
        public final int u;
        public final int v;
        public final long cost;

        public Edge(int u, int v, long cost) {
            this.u = u;
            this.v = v;
            this.cost = cost;
        }

        // Ordenamos estrictamente por costo (menor a mayor)
        @Override
        public int compareTo(Edge other) {
            return Long.compare(this.cost, other.cost);
        }
    }

    // Estructura Union-Find obligatoria para la verificacion y prevencion rapida de ciclos
    public static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0; // Inicializamos el rango en 0
            }
        }

        // Encuentra la raiz aplicando Path Compression estricto
        public int find(int i) {
            if (parent[i] == i) {
                return i;
            }
            // Compresion: el nodo apunta directamente a la raiz absoluta para futuras consultas en O(1) amortizado
            parent[i] = find(parent[i]);
            return parent[i];
        }

        // Une dos componentes usando Union by Rank, retorna true si fue exitoso, false si formaba ciclo
        public boolean union(int i, int j) {
            int rootI = find(i);
            int rootJ = find(j);

            if (rootI == rootJ) {
                return false; // Estan en el mismo componente, la arista formaria un ciclo
            }

            // Union by Rank: conectamos el arbol mas chato debajo del mas profundo
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++; // Al tener mismo rango, la nueva raiz gana 1 de profundidad
            }

            return true;
        }
    }

    private final int numNodes;
    private final List<Edge> edges;

    public NetworkReconnector(int numNodes) {
        this.numNodes = numNodes;
        this.edges = new ArrayList<>();
    }

    // Metodo para alimentar la red de cables disponibles desde la GUI
    public void addCable(int u, int v, long cost) {
        edges.add(new Edge(u, v, cost));
    }

    // Complejidad Temporal: O(C log C) dominada por el ordenamiento inicial de los C cables.
    // (Union-Find toma O(alpha(N)) lo cual es casi O(1) constante gracias a rank y compression).
    // Complejidad Espacial: O(N + C) para almacenar los N nodos en Union-Find y los C cables en la lista.
    public String solve(int caseNumber) {
        // Ordenamiento goloso (Greedy): revisamos primero los cables mas baratos
        Collections.sort(edges);

        UnionFind uf = new UnionFind(numNodes);
        long totalCost = 0;
        int edgesUsed = 0;

        for (Edge edge : edges) {
            // Intentamos unir los extremos; si devuelve true, es valido para el MST
            if (uf.union(edge.u, edge.v)) {
                totalCost += edge.cost;
                edgesUsed++;

                // Optimizacion: Un MST siempre tiene exactamente N - 1 aristas, detenemos temprano si terminamos
                if (edgesUsed == numNodes - 1) {
                    break;
                }
            }
        }

        // Validacion final: Revisar si los componentes lograron fusionarse en uno solo
        if (numNodes > 1 && edgesUsed == numNodes - 1) {
            return "Case #" + caseNumber + ": " + totalCost;
        } else if (numNodes == 1 || numNodes == 0) {
            // Grafo trivial o vacio
            return "Case #" + caseNumber + ": 0";
        } else {
            // No hubo cables suficientes para conectar todas las islas
            return "Case #" + caseNumber + ": Limon cut too many cables";
        }
    }
}
