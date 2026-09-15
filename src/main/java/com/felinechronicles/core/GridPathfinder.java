package com.felinechronicles.core;

import java.util.ArrayDeque;
import java.util.Arrays;

// Clase para la Mision 1: BFS y DFS en grilla
public final class GridPathfinder {

    // Movimientos: arriba, abajo, izquierda, derecha
    private static final int[] DF = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private GridPathfinder() {
    }

    // Clase interna para devolver multiples valores sin usar librerias
    public static final class Result {
        public final boolean reachable;
        public final int bfsMoves;
        public final int dfsMoves;

        private Result(boolean reachable, int bfsMoves, int dfsMoves) {
            this.reachable = reachable;
            this.bfsMoves = bfsMoves;
            this.dfsMoves = dfsMoves;
        }

        static Result unreachable() {
            return new Result(false, -1, -1);
        }

        static Result found(int bfsMoves, int dfsMoves) {
            return new Result(true, bfsMoves, dfsMoves);
        }
    }

    // Complejidad Temporal: O(F * C)
    // Complejidad Espacial: O(F * C)
    public static Result solve(int rows, int cols, boolean[][] mines,
                                     int startRow, int startCol, int destRow, int destCol) {
        // Nina es inalcanzable si hay mina en inicio o destino
        if (mines[startRow][startCol] || mines[destRow][destCol]) {
            return Result.unreachable();
        }
        // Retorno rapido si inicio es igual a destino
        if (startRow == destRow && startCol == destCol) {
            return Result.found(0, 0);
        }

        int bfsMoves = bfs(rows, cols, mines, startRow, startCol, destRow, destCol);
        if (bfsMoves == -1) {
            return Result.unreachable();
        }
        int dfsMoves = dfs(rows, cols, mines, startRow, startCol, destRow, destCol);
        return Result.found(bfsMoves, dfsMoves);
    }

    // Complejidad Temporal: O(F * C) porque visita cada celda maximo una vez
    // Complejidad Espacial: O(F * C) por arreglo de distancias y cola
    // BFS es correcto para hallar el camino mas corto en grafos no ponderados
    private static int bfs(int rows, int cols, boolean[][] mines,
                           int startRow, int startCol, int destRow, int destCol) {
        int[] distance = new int[rows * cols];
        Arrays.fill(distance, -1);
        int startIndex = startRow * cols + startCol;
        distance[startIndex] = 0;

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(startIndex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            int r = current / cols;
            int c = current % cols;
            
            // Retorna distancia minima al llegar a destino
            if (r == destRow && c == destCol) {
                return distance[current];
            }
            for (int k = 0; k < 4; k++) {
                int nr = r + DF[k];
                int nc = c + DC[k];
                
                // Evita salir de los limites
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                // Evita pisar minas
                if (mines[nr][nc]) continue;
                
                int neighborIndex = nr * cols + nc;
                if (distance[neighborIndex] != -1) continue;
                
                distance[neighborIndex] = distance[current] + 1;
                queue.add(neighborIndex);
            }
        }
        return -1;
    }

    // Complejidad Temporal: O(F * C)
    // Complejidad Espacial: O(F * C) por pila explicita
    // Se usa pila explicita con arreglos en vez de recursion para evitar StackOverflow
    private static int dfs(int rows, int cols, boolean[][] mines,
                           int startRow, int startCol, int destRow, int destCol) {
        int n = rows * cols;
        int[] stackRow = new int[n];
        int[] stackCol = new int[n];
        int[] stackDir = new int[n];
        boolean[] visited = new boolean[n];

        int top = 0;
        stackRow[0] = startRow;
        stackCol[0] = startCol;
        stackDir[0] = 0;
        visited[startRow * cols + startCol] = true;

        while (top >= 0) {
            int r = stackRow[top];
            int c = stackCol[top];

            // Tope de la pila equivale a profundidad/movimientos
            if (r == destRow && c == destCol) {
                return top;
            }

            if (stackDir[top] < 4) {
                int k = stackDir[top]++;
                int nr = r + DF[k];
                int nc = c + DC[k];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !mines[nr][nc] && !visited[nr * cols + nc]) {
                    visited[nr * cols + nc] = true;
                    top++;
                    stackRow[top] = nr;
                    stackCol[top] = nc;
                    stackDir[top] = 0;
                }
            } else {
                // Retrocede (backtrack) si probamos todas las direcciones
                top--;
            }
        }
        return -1;
    }
}
