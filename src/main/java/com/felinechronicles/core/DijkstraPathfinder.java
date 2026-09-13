package com.felinechronicles.core;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraPathfinder {

    // Clase interna para representar aristas y nodos en la cola de prioridad
    public static class Edge implements Comparable<Edge> {
        public final int targetNode;
        public final long weight;

        public Edge(int targetNode, long weight) {
            this.targetNode = targetNode;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Long.compare(this.weight, other.weight);
        }
    }

    // Grafo representado como lista de adyacencia para optimizar espacio y tiempo de iteracion
    private final List<List<Edge>> graph;
    private final int numNodes;

    // Constante que representa infinito (sin ruta), Long.MAX_VALUE previene desbordamientos futuros
    private static final long INF = Long.MAX_VALUE;

    public DijkstraPathfinder(int numNodes) {
        this.numNodes = numNodes;
        this.graph = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            this.graph.add(new ArrayList<>());
        }
    }

    // Metodo modular para inicializar el grafo. Agrega conexiones bidireccionales
    public void addBidirectionalEdge(int u, int v, long weight) {
        graph.get(u).add(new Edge(v, weight));
        graph.get(v).add(new Edge(u, weight));
    }

    // Complejidad Temporal: O((V + E) log V) donde V es el numero de nodos y E de aristas.
    // Complejidad Espacial: O(V + E) por la lista de adyacencia, el arreglo de distancias y la PriorityQueue.
    // Dijkstra es la opcion correcta porque halla la ruta minima desde un unico origen de forma eficiente
    // en grafos con pesos no negativos; la PriorityQueue optimiza la extraccion del nodo con menor costo.
    public String findShortestPath(int caseNumber, int start, int destination) {
        // Caso base rapido si el origen y el destino son el mismo
        if (start == destination) {
            return "Case #" + caseNumber + ": 0";
        }

        // Inicializamos distancias a INF
        long[] distances = new long[numNodes];
        for (int i = 0; i < numNodes; i++) {
            distances[i] = INF;
        }
        distances[start] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>();
        pq.add(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int currentNode = current.targetNode;
            long currentDistance = current.weight;

            // Ignoramos nodos desactualizados en la cola (optimizacion clave)
            if (currentDistance > distances[currentNode]) {
                continue;
            }

            // Parada temprana: si sacamos el destino, ya encontramos el camino mas corto garantizado
            if (currentNode == destination) {
                break;
            }

            for (Edge neighborEdge : graph.get(currentNode)) {
                int neighbor = neighborEdge.targetNode;
                long weight = neighborEdge.weight;

                // Prevenimos operaciones aritmeticas sobre nuestro valor centinela (INF)
                if (distances[currentNode] != INF) {
                    long newDistance = distances[currentNode] + weight;
                    // Relajacion de la arista si encontramos un camino mas corto
                    if (newDistance < distances[neighbor]) {
                        distances[neighbor] = newDistance;
                        pq.add(new Edge(neighbor, newDistance));
                    }
                }
            }
        }

        // Formateo del resultado final exactamente como se requiere
        if (distances[destination] == INF) {
            return "Case #" + caseNumber + ": Nina is very sad";
        } else {
            return "Case #" + caseNumber + ": " + distances[destination];
        }
    }
}
