package com.felinechronicles.gui;

import com.felinechronicles.core.DijkstraPathfinder;
import com.felinechronicles.core.ChurunMaximizer;
import com.felinechronicles.core.NetworkReconnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Scanner;

public class MainApp extends JFrame {

    private JComboBox<String> missionSelector;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextArea matrixArea;
    private JTabbedPane bottomTabs;
    private JPanel drawingPanel;

    public MainApp() {
        setTitle("The Feline Graph Chronicles - Solver UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null); 
        
        initComponents();
    }

    private void initComponents() {
        // --- Panel Izquierdo: Controles e Input ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] missions = {
            "Misión 1 (BFS/DFS)", 
            "Misión 2 (Dijkstra)", 
            "Misión 3 (Floyd-Warshall/Bellman-Ford)", 
            "Misión 4 (Kruskal)"
        };
        missionSelector = new JComboBox<>(missions);
        
        JButton loadSampleBtn = new JButton("Load Sample");
        JButton solveBtn = new JButton("Solve");

        controlsPanel.add(new JLabel("Seleccionar Misión:"));
        controlsPanel.add(missionSelector);
        controlsPanel.add(loadSampleBtn);
        controlsPanel.add(solveBtn);

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input de datos"));

        leftPanel.add(controlsPanel, BorderLayout.NORTH);
        leftPanel.add(inputScroll, BorderLayout.CENTER);

        // --- Panel Derecho: Motor de Dibujo ---
        // TODO Nicolás: Implementar motor de dibujo Java2D aquí, respetando los límites de tamaño (50x50, 60 nodos, 100 intersecciones).
        drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.DARK_GRAY);
        drawingPanel.setLayout(new BorderLayout());
        drawingPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "Área de Dibujo (Pendiente)", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                null, 
                Color.WHITE));
        
        JLabel placeholderLabel = new JLabel("<< Motor de Dibujo Reservado >>", SwingConstants.CENTER);
        placeholderLabel.setForeground(Color.WHITE);
        placeholderLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        drawingPanel.add(placeholderLabel, BorderLayout.CENTER);
        drawingPanel.setPreferredSize(new Dimension(400, 0));

        // --- Panel Inferior: Pestañas para Resultados y Matriz ---
        bottomTabs = new JTabbedPane();
        bottomTabs.setPreferredSize(new Dimension(0, 220));

        // Pestaña 1: Consola
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        bottomTabs.addTab("Consola de Resultados", outputScroll);

        // Pestaña 2: Matriz (Misión 3)
        matrixArea = new JTextArea();
        matrixArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        matrixArea.setEditable(false);
        matrixArea.setBackground(new Color(230, 240, 250));
        JScrollPane matrixScroll = new JScrollPane(matrixArea);
        bottomTabs.addTab("Matriz de Floyd-Warshall", matrixScroll);

        // Ensamblaje Principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawingPanel);
        splitPane.setResizeWeight(0.6); 
        splitPane.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(bottomTabs, BorderLayout.SOUTH);

        // Eventos
        loadSampleBtn.addActionListener(this::handleLoadSample);
        solveBtn.addActionListener(this::handleSolve);
    }

    private void handleLoadSample(ActionEvent e) {
        // Datos con nodos, aristas, conexiones y nodos de inicio/fin (comunes en M2 y M3)
        String sampleData = "3 3\n" +
                            "0 1 100\n" +
                            "1 2 50\n" +
                            "0 2 200\n" +
                            "0 2\n";
        inputArea.setText(sampleData);
        outputArea.setForeground(Color.BLACK);
        outputArea.setText("Datos de prueba cargados correctamente. Presiona Solve.\n");
    }

    private void handleSolve(ActionEvent e) {
        outputArea.setText("");
        matrixArea.setText(""); 
        outputArea.setForeground(Color.BLACK);
        
        String inputText = inputArea.getText();
        int missionIndex = missionSelector.getSelectedIndex() + 1;

        try {
            // Manejo global estricto: cualquier error de Scanner saltará aquí
            parseAndSolve(missionIndex, inputText);
        } catch (Exception ex) {
            // Cumpliendo la regla: Cero stack traces, mensaje exacto y amigable
            outputArea.setForeground(Color.RED);
            outputArea.setText("Error en el formato de entrada. Revisa los datos.");
        }
    }

    // 1. Parseo seguro de texto (Crítico)
    private void parseAndSolve(int mission, String inputText) throws Exception {
        Scanner scanner = new Scanner(inputText);
        
        // Verificamos si hay texto en absoluto
        if (!scanner.hasNextInt()) {
            throw new IllegalArgumentException("No hay datos");
        }

        int n = scanner.nextInt();
        int e = scanner.nextInt();

        switch (mission) {
            case 1:
                outputArea.setText("Misión 1 aún no implementada por el equipo.\n");
                bottomTabs.setSelectedIndex(0);
                break;

            case 2: // Dijkstra
                DijkstraPathfinder dijkstra = new DijkstraPathfinder(n);
                for (int i = 0; i < e; i++) {
                    dijkstra.addBidirectionalEdge(scanner.nextInt(), scanner.nextInt(), scanner.nextLong());
                }
                int start2 = scanner.nextInt();
                int dest2 = scanner.nextInt();
                
                String result2 = dijkstra.findShortestPath(1, start2, dest2);
                outputArea.setText(result2 + "\n");
                bottomTabs.setSelectedIndex(0); // Focus en resultados
                break;

            case 3: // Floyd-Warshall & Bellman-Ford
                ChurunMaximizer maximizer = new ChurunMaximizer(n);
                for (int i = 0; i < e; i++) {
                    maximizer.addDirectedEdge(scanner.nextInt(), scanner.nextInt(), scanner.nextLong());
                }
                int start3 = scanner.nextInt();
                int dest3 = scanner.nextInt();

                // Resultado Bellman-Ford
                String result3 = maximizer.solveWithBellmanFord(1, start3, dest3);
                outputArea.setText("Evaluación (Bellman-Ford):\n" + result3 + "\n\nRevisa la pestaña contigua para ver la Matriz de Floyd-Warshall.");
                
                // Matriz Floyd-Warshall (Requisito Específico Misión 3)
                long[][] matrix = maximizer.runFloydWarshall();
                StringBuilder sb = new StringBuilder("Matriz Cruzada N x N (Floyd-Warshall):\n\n");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        long val = matrix[i][j];
                        if (val == Long.MIN_VALUE) {
                            sb.append(String.format("%10s", "-INF"));
                        } else if (val == Long.MAX_VALUE) {
                            sb.append(String.format("%10s", "+INF"));
                        } else {
                            sb.append(String.format("%10d", val));
                        }
                    }
                    sb.append("\n");
                }
                matrixArea.setText(sb.toString());
                bottomTabs.setSelectedIndex(1); // Cambia el foco a la pestaña de la matriz automáticamente
                break;

            case 4: // Kruskal
                NetworkReconnector kruskal = new NetworkReconnector(n);
                for (int i = 0; i < e; i++) {
                    kruskal.addCable(scanner.nextInt(), scanner.nextInt(), scanner.nextLong());
                }
                
                String result4 = kruskal.solve(1);
                outputArea.setText(result4 + "\n");
                bottomTabs.setSelectedIndex(0);
                break;
        }
        
        scanner.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new MainApp().setVisible(true);
        });
    }
}
