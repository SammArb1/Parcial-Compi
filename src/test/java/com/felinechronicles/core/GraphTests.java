package com.felinechronicles.core;

public class GraphTests {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DEL PARCIAL ===\n");
        testMission1();
        testMission2();
        testMission3();
        testMission4();
        System.out.println("\n=== TODAS LAS PRUEBAS FINALIZADAS ===");
    }

    private static void assertTest(String expected, String result, String testName) {
        if (expected.equals(result)) {
            System.out.println("[PASS] " + testName + " -> " + result);
        } else {
            System.out.println("[FAIL] " + testName);
            System.out.println("   Esperado: " + expected);
            System.out.println("   Obtenido: " + result);
        }
    }

    public static void testMission1() {
        System.out.println("--- Misión 1: BFS & DFS ---");
        
        String input1 = "10 10\n9\n0 1 2\n1 1 2\n2 2 2 9\n3 2 1 7\n5 3 3 6 9\n" +
                "6 4 0 1 2 7\n7 3 0 3 8\n8 2 7 9\n9 3 2 3 4\n0 0\n9 9\n0 0";
        assertTest("Case #1: BFS 18 DFS 32", NinaRescueService.execute(input1), "Ejemplo del enunciado");

        assertTest("Case #1: BFS 0 DFS 0", NinaRescueService.execute("3 3\n0\n1 1 1 1\n0 0"), "Inicio == destino");
        assertTest("Case #1: Nina is unreachable", NinaRescueService.execute("2 2\n1\n0 1 0\n0 0 1 1\n0 0"), "Mina en inicio");
        assertTest("Case #1: Nina is unreachable", NinaRescueService.execute("1 3\n1\n0 1 1\n0 0 0 2\n0 0"), "Pasillo bloqueado");
        assertTest("Case #1: BFS 3 DFS 3", NinaRescueService.execute("1 4\n0\n0 0 0 3\n0 0"), "Pasillo vacío");
    }

    public static void testMission2() {
        System.out.println("--- Misión 2: Dijkstra ---");
        
        // Caso 1
        DijkstraPathfinder d1 = new DijkstraPathfinder(2);
        d1.addBidirectionalEdge(0, 1, 100);
        assertTest("Case #1: 100", d1.findShortestPath(1, 0, 1), "Caso 1 (Directo)");

        // Caso 2
        DijkstraPathfinder d2 = new DijkstraPathfinder(3);
        d2.addBidirectionalEdge(0, 1, 100);
        d2.addBidirectionalEdge(0, 2, 200);
        d2.addBidirectionalEdge(1, 2, 50);
        assertTest("Case #2: 150", d2.findShortestPath(2, 2, 0), "Caso 2 (Ruta alterna)");

        // Caso 3
        DijkstraPathfinder d3 = new DijkstraPathfinder(2);
        assertTest("Case #3: Nina is very sad", d3.findShortestPath(3, 0, 1), "Caso 3 (Sin rutas)");
    }

    public static void testMission3() {
        System.out.println("\n--- Misión 3: Floyd-Warshall & Bellman-Ford ---");
        
        // Caso 1: Ruta normal
        ChurunMaximizer c1 = new ChurunMaximizer(5);
        c1.addDirectedEdge(0, 1, 50);
        c1.addDirectedEdge(0, 2, 10);
        c1.addDirectedEdge(1, 2, -30);
        c1.addDirectedEdge(1, 3, 40);
        c1.addDirectedEdge(2, 1, -5);
        c1.addDirectedEdge(2, 3, 60);
        c1.addDirectedEdge(3, 4, 20);
        assertTest("Case #1: 110", c1.solveWithFloydWarshall(1, 0, 4), "Caso 1 (Floyd-Warshall)");
        assertTest("Case #1: 110", c1.solveWithBellmanFord(1, 0, 4), "Caso 1 (Bellman-Ford)");

        // Caso 2: Ciclo infinito
        ChurunMaximizer c2 = new ChurunMaximizer(4);
        c2.addDirectedEdge(0, 1, 20);
        c2.addDirectedEdge(1, 2, 30);
        c2.addDirectedEdge(2, 1, -10);
        c2.addDirectedEdge(2, 3, 15);
        assertTest("Case #2: Infinite churun!", c2.solveWithFloydWarshall(2, 0, 3), "Caso 2 (Floyd-Warshall)");
        assertTest("Case #2: Infinite churun!", c2.solveWithBellmanFord(2, 0, 3), "Caso 2 (Bellman-Ford)");

        // Caso 3: Ruta negativa
        ChurunMaximizer c3 = new ChurunMaximizer(3);
        c3.addDirectedEdge(0, 1, -40);
        c3.addDirectedEdge(1, 2, -25);
        c3.addDirectedEdge(0, 2, -80);
        assertTest("Case #3: -65", c3.solveWithFloydWarshall(3, 0, 2), "Caso 3 (Floyd-Warshall)");
        assertTest("Case #3: -65", c3.solveWithBellmanFord(3, 0, 2), "Caso 3 (Bellman-Ford)");
    }

    public static void testMission4() {
        System.out.println("\n--- Misión 4: Kruskal ---");
        
        // Los nodos van de 1 a N, por lo que creamos tamaño N+1 (5) para que los índices coincidan directamente
        NetworkReconnector n1 = new NetworkReconnector(5);
        n1.addCable(1, 2, 10);
        n1.addCable(2, 3, 20);
        n1.addCable(3, 4, 30);
        n1.addCable(4, 1, 40);
        n1.addCable(1, 3, 15);
        
        // El grafo real tiene 4 nodos, pero como usamos índices 1 a 4, descontamos el nodo 0 al evaluar
        assertTest("Case #1: 55", n1.solve(1), "Caso 1 (MST Exitoso)");
    }
}