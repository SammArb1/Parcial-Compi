package com.felinechronicles.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChurunMaximizer {

    public static class Edge {
        public final int from;
        public final int to;
        public final long weight; // En este caso el peso representa la ganancia de "churun"

        public Edge(int from, int to, long weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private final int numNodes;
    private final List<Edge> edges;

    // Centinelas seguros para maximizacion (sin riesgo de desbordar pues validamos antes de operar)
    private static final long NO_ROUTE = Long.MIN_VALUE;
    private static final long INF_CHURUN = Long.MAX_VALUE;

    public ChurunMaximizer(int numNodes) {
        this.numNodes = numNodes;
        this.edges = new ArrayList<>();
    }

    public void addDirectedEdge(int u, int v, long weight) {
        edges.add(new Edge(u, v, weight));
    }

    // Complejidad Temporal: O(V^3) donde V es el numero de nodos.
    // Complejidad Espacial: O(V^2) para la matriz cruzada N x N.
    public long[][] runFloydWarshall() {
        long[][] dist = new long[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            Arrays.fill(dist[i], NO_ROUTE);
            dist[i][i] = 0; // Inicializacion de la diagonal en 0
        }

        for (Edge edge : edges) {
            // Prevenimos sobreescritura con aristas peores si hay multiples conexiones
            if (dist[edge.from][edge.to] == NO_ROUTE || edge.weight > dist[edge.from][edge.to]) {
                dist[edge.from][edge.to] = edge.weight;
            }
        }

        for (int k = 0; k < numNodes; k++) {
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    // Evitamos hacer aritmetica sobre el centinela
                    if (dist[i][k] != NO_ROUTE && dist[k][j] != NO_ROUTE) {
                        long newDist = dist[i][k] + dist[k][j];
                        if (dist[i][j] == NO_ROUTE || newDist > dist[i][j]) {
                            dist[i][j] = newDist;
                        }
                    }
                }
            }
        }

        // Pasada final obligatoria para detectar y propagar ciclos de ganancia positiva
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                for (int k = 0; k < numNodes; k++) {
                    if (dist[i][k] != NO_ROUTE && dist[k][k] > 0 && dist[k][j] != NO_ROUTE) {
                        dist[i][j] = INF_CHURUN;
                        break; // Ya sabemos que llega al infinito, no hay que revisar mas
                    }
                }
            }
        }

        return dist;
    }

    // Complejidad Temporal: O(V * E) donde V es nodos y E es aristas.
    // Complejidad Espacial: O(V) para el arreglo de distancias de origen unico.
    public long[] runBellmanFord(int startNode) {
        long[] dist = new long[numNodes];
        Arrays.fill(dist, NO_ROUTE);
        dist[startNode] = 0;

        // Fase 1: Maximizar rutas (V-1 iteraciones)
        for (int i = 0; i < numNodes - 1; i++) {
            for (Edge edge : edges) {
                if (dist[edge.from] != NO_ROUTE) {
                    long newDist = dist[edge.from] + edge.weight;
                    if (dist[edge.to] == NO_ROUTE || newDist > dist[edge.to]) {
                        dist[edge.to] = newDist;
                    }
                }
            }
        }

        // Fase 2: Detectar ciclos positivos y propagar el infinito por todo nodo alimentado por el ciclo
        for (int i = 0; i < numNodes; i++) {
            for (Edge edge : edges) {
                if (dist[edge.from] != NO_ROUTE) {
                    if (dist[edge.from] == INF_CHURUN) {
                        dist[edge.to] = INF_CHURUN; // Si el origen ya es infinito, se propaga
                    } else {
                        long newDist = dist[edge.from] + edge.weight;
                        // Si aun podemos maximizar, significa que esta arista recibe de un ciclo positivo
                        if (dist[edge.to] == NO_ROUTE || newDist > dist[edge.to]) {
                            dist[edge.to] = INF_CHURUN;
                        }
                    }
                }
            }
        }

        return dist;
    }

    // Metodo que evalua cualquier respuesta calculada basandose en la precedencia estricta solicitada
    public String evaluateResult(int caseNumber, long maxChurun) {
        if (maxChurun == NO_ROUTE) {
            return "Case #" + caseNumber + ": Limon blocked the way";
        } else if (maxChurun == INF_CHURUN) {
            return "Case #" + caseNumber + ": Infinite churun!";
        } else {
            return "Case #" + caseNumber + ": " + maxChurun; // Puede ser negativo y es valido
        }
    }

    // Wrapper para que la GUI resuelva el problema via Floyd-Warshall comodamente
    public String solveWithFloydWarshall(int caseNumber, int start, int destination) {
        long[][] dist = runFloydWarshall();
        return evaluateResult(caseNumber, dist[start][destination]);
    }

    // Wrapper para que la GUI resuelva el problema via Bellman-Ford comodamente
    public String solveWithBellmanFord(int caseNumber, int start, int destination) {
        long[] dist = runBellmanFord(start);
        return evaluateResult(caseNumber, dist[destination]);
    }
}
