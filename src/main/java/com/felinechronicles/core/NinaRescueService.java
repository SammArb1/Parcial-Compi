package com.felinechronicles.core;

import java.util.NoSuchElementException;
import java.util.Scanner;

// Servicio encargado de parsear y orquestar la Mision 1
public final class NinaRescueService {

    private NinaRescueService() {
    }

    // Complejidad Temporal: O(C * F * C) donde C es el numero de casos
    // Complejidad Espacial: O(F * C) por cada grilla procesada
    public static String execute(String rawInput) {
        Scanner sc = new Scanner(rawInput);
        StringBuilder output = new StringBuilder();
        int caseNumber = 1;
        try {
            while (sc.hasNextInt()) {
                int rows = sc.nextInt();
                int cols = sc.nextInt();
                
                // Si la grilla es 0x0 terminamos el parseo de la Mision 1
                if (rows == 0 && cols == 0) {
                    break;
                }
                if (rows < 1 || rows > 1000 || cols < 1 || cols > 1000) {
                    throw new IllegalArgumentException("Tamaño de grilla inválido.");
                }

                boolean[][] mines = new boolean[rows][cols];
                int rowsWithMines = sc.nextInt();
                if (rowsWithMines < 0 || rowsWithMines > rows) {
                    throw new IllegalArgumentException("Cantidad inválida de filas con minas.");
                }
                for (int i = 0; i < rowsWithMines; i++) {
                    int r = sc.nextInt();
                    int count = sc.nextInt();
                    if (r < 0 || r >= rows || count < 0) {
                        throw new IllegalArgumentException("Descriptor de fila mal formado.");
                    }
                    for (int j = 0; j < count; j++) {
                        int c = sc.nextInt();
                        verifyLimits(r, c, rows, cols);
                        mines[r][c] = true;
                    }
                }

                int startRow = sc.nextInt();
                int startCol = sc.nextInt();
                int destRow = sc.nextInt();
                int destCol = sc.nextInt();
                verifyLimits(startRow, startCol, rows, cols);
                verifyLimits(destRow, destCol, rows, cols);

                GridPathfinder.Result result = GridPathfinder.solve(
                        rows, cols, mines, startRow, startCol, destRow, destCol);

                if (!result.reachable) {
                    // Impresion exacta sin traducir
                    output.append("Case #").append(caseNumber).append(": Nina is unreachable\n");
                } else {
                    // Impresion exacta sin traducir
                    output.append("Case #").append(caseNumber).append(": BFS ")
                            .append(result.bfsMoves).append(" DFS ")
                            .append(result.dfsMoves).append('\n');
                }
                caseNumber++;
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Entrada mal formada.", e);
        }
        return output.toString().stripTrailing();
    }

    // Verifica que un punto no salga de los limites
    private static void verifyLimits(int r, int c, int rows, int cols) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalArgumentException("Coordenada fuera de los limites de la grilla.");
        }
    }
}
