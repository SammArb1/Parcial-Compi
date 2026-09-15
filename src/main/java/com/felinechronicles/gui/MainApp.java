package com.felinechronicles.gui;

import com.felinechronicles.core.DijkstraPathfinder;
import com.felinechronicles.core.ChurunMaximizer;
import com.felinechronicles.core.NetworkReconnector;
import com.felinechronicles.core.NinaRescueService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class MainApp extends JFrame {

    private JComboBox<String> missionSelector;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextArea matrixArea;
    private JTabbedPane bottomTabs;
    private DrawingPanel drawingPanel;

    // Tema visual: Gatos heroicos y el villano
    private static final Color THEME_HERO_BG = new Color(255, 250, 240); // Cálido Pola Gold
    private static final Color THEME_VILLAIN_BG = new Color(230, 255, 230); // Limón Green
    private static final Color THEME_MINERVA_PURPLE = new Color(245, 240, 255); // Sabiduría de Minerva

    public MainApp() {
        setTitle("The Feline Graph Chronicles - Epic UI Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // --- Panel Izquierdo: Controles e Input ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] missions = {
            "Misión 1: Rescate de Nina (BFS/DFS)", 
            "Misión 2: Ruta de Pola (Dijkstra)", 
            "Misión 3: Minerva vs Limón (Floyd/Bellman)", 
            "Misión 4: Reconexión de Red (Kruskal)"
        };
        missionSelector = new JComboBox<>(missions);
        
        JButton loadSampleBtn = new JButton("Load Sample");
        loadSampleBtn.setBackground(Color.LIGHT_GRAY);
        loadSampleBtn.setForeground(Color.BLACK);

        JButton solveBtn = new JButton("Solve & Draw");
        solveBtn.setBackground(Color.LIGHT_GRAY);
        solveBtn.setForeground(Color.BLACK);

        controlsPanel.add(new JLabel("Misión:"));
        controlsPanel.add(missionSelector);
        controlsPanel.add(loadSampleBtn);
        controlsPanel.add(solveBtn);

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Pergamino de Entrada (Input)"));

        leftPanel.add(controlsPanel, BorderLayout.NORTH);
        leftPanel.add(inputScroll, BorderLayout.CENTER);

        // --- Panel Derecho: Motor de Dibujo ---
        drawingPanel = new DrawingPanel();
        
        // --- Panel Inferior: Pestañas para Resultados y Matriz ---
        bottomTabs = new JTabbedPane();
        bottomTabs.setPreferredSize(new Dimension(0, 220));

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(THEME_HERO_BG);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        bottomTabs.addTab("Crónicas de Resultados", outputScroll);

        matrixArea = new JTextArea();
        matrixArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        matrixArea.setEditable(false);
        matrixArea.setBackground(THEME_MINERVA_PURPLE);
        JScrollPane matrixScroll = new JScrollPane(matrixArea);
        bottomTabs.addTab("Matriz de Minerva (Exclusivo M3)", matrixScroll);

        // Ensamblaje Principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawingPanel);
        splitPane.setResizeWeight(0.4); 
        splitPane.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(bottomTabs, BorderLayout.SOUTH);

        // Eventos
        loadSampleBtn.addActionListener(this::handleLoadSample);
        solveBtn.addActionListener(this::handleSolve);
        
        // Carga inicial de datos
        missionSelector.setSelectedIndex(0);
        handleLoadSample(null);
    }

    private void handleLoadSample(ActionEvent e) {
        int mission = missionSelector.getSelectedIndex() + 1;
        String sampleData = "";
        
        switch (mission) {
            case 1:
                sampleData = "10 10\n9\n0 1 2\n1 1 2\n2 2 2 9\n3 2 1 7\n5 3 3 6 9\n6 4 0 1 2 7\n7 3 0 3 8\n8 2 7 9\n9 3 2 3 4\n0 0\n9 9\n0 0";
                break;
            case 2:
                sampleData = "3\n2 1 0 1\n0 1 100\n3 3 2 0\n0 1 100\n0 2 200\n1 2 50\n2 0 0 1";
                break;
            case 3:
                sampleData = "3\n5 7 0 4\n0 1 50\n0 2 10\n1 2 -30\n1 3 40\n2 1 -5\n2 3 60\n3 4 20\n4 4 0 3\n0 1 20\n1 2 30\n2 1 -10\n2 3 15\n3 3 0 2\n0 1 -40\n1 2 -25\n0 2 -80";
                break;
            case 4:
                sampleData = "1\n4 5\n1 2 10\n2 3 20\n3 4 30\n4 1 40\n1 3 15";
                break;
        }
        inputArea.setText(sampleData);
        outputArea.setForeground(Color.BLACK);
        outputArea.setText("Datos heroicos cargados para la Misión " + mission + ".\n¡Presiona Solve & Draw!\n");
        drawingPanel.clear();
    }

    private void handleSolve(ActionEvent e) {
        outputArea.setText("");
        matrixArea.setText(""); 
        outputArea.setForeground(Color.BLACK);
        
        String inputText = inputArea.getText();
        int missionIndex = missionSelector.getSelectedIndex() + 1;

        try {
            parseAndSolve(missionIndex, inputText);
        } catch (Exception ex) {
            outputArea.setForeground(Color.RED);
            outputArea.setText("Error en el formato de entrada. Revisa los datos.\n\nDetalle técnico para depurar:\n" + ex.toString());
            drawingPanel.showError("Error de formato. No se puede visualizar.");
        }
    }

    // Parseo seguro de texto y restricciones del lienzo
    private void parseAndSolve(int mission, String inputText) throws Exception {
        Scanner scanner = new Scanner(inputText);
        
        if (!scanner.hasNext()) {
            throw new IllegalArgumentException("No hay datos en el área de texto.");
        }

        switch (mission) {
            case 1: {
                Scanner tempScanner = new Scanner(inputText);
                int rows = tempScanner.nextInt();
                int cols = tempScanner.nextInt();
                
                String result1 = NinaRescueService.execute(inputText);
                String firstResult = result1.split("\n")[0]; // Para mostrar en el lienzo
                
                if (rows > 50 || cols > 50) {
                    drawingPanel.showError("Misión 1: Tamaño excedido (" + rows + "x" + cols + "). Máximo permitido: 50x50.\n" + firstResult);
                    tempScanner.close();
                } else {
                    boolean[][] mines = new boolean[rows][cols];
                    int rowsWithMines = tempScanner.nextInt();
                    for (int i = 0; i < rowsWithMines; i++) {
                        int r = tempScanner.nextInt();
                        int count = tempScanner.nextInt();
                        for (int j = 0; j < count; j++) {
                            int c = tempScanner.nextInt();
                            mines[r][c] = true;
                        }
                    }
                    int startRow = tempScanner.nextInt();
                    int startCol = tempScanner.nextInt();
                    int destRow = tempScanner.nextInt();
                    int destCol = tempScanner.nextInt();
                    tempScanner.close();
                    
                    List<Point> path = computeGridBFS(rows, cols, mines, startRow, startCol, destRow, destCol);
                    drawingPanel.drawGridExtended(rows, cols, mines, new Point(startCol, startRow), new Point(destCol, destRow), path, firstResult); 
                }
                
                outputArea.setText(result1 + "\n");
                bottomTabs.setSelectedIndex(0);
                break;
            }
            case 2: {
                int t = scanner.nextInt();
                StringBuilder sbRes = new StringBuilder();
                for (int caseNum = 1; caseNum <= t; caseNum++) {
                    int n2 = scanner.nextInt();
                    int e2 = scanner.nextInt();
                    int start2 = scanner.nextInt();
                    int dest2 = scanner.nextInt();
                    
                    List<DrawingPanel.EdgeUI> allEdges = new ArrayList<>();
                    DijkstraPathfinder dijkstra = new DijkstraPathfinder(n2);
                    for (int i = 0; i < e2; i++) {
                        int u = scanner.nextInt();
                        int v = scanner.nextInt();
                        long cost = scanner.nextLong();
                        dijkstra.addBidirectionalEdge(u, v, cost);
                        allEdges.add(new DrawingPanel.EdgeUI(u, v, cost, true));
                    }
                    
                    String res = dijkstra.findShortestPath(caseNum, start2, dest2);
                    sbRes.append(res).append("\n");
                    
                    if (caseNum == t) {
                        if (n2 > 60) {
                            drawingPanel.showError("Misión 2: Tamaño excedido (" + n2 + " nodos). Máximo permitido: 60 nodos.\n" + res);
                        } else {
                            List<DrawingPanel.EdgeUI> sol = computeDijkstraSolution(n2, allEdges, start2, dest2);
                            drawingPanel.setGraphData(n2, allEdges, sol, "Ruta de Pola", res, 0);
                        }
                    }
                }
                outputArea.setText(sbRes.toString());
                bottomTabs.setSelectedIndex(0); 
                break;
            }
            case 3: {
                int t = scanner.nextInt();
                StringBuilder sbRes = new StringBuilder();
                for (int caseNum = 1; caseNum <= t; caseNum++) {
                    int n3 = scanner.nextInt();
                    int e3 = scanner.nextInt();
                    int start3 = scanner.nextInt();
                    int dest3 = scanner.nextInt();
                    
                    List<DrawingPanel.EdgeUI> allEdges = new ArrayList<>();
                    ChurunMaximizer maximizer = new ChurunMaximizer(n3);
                    for (int i = 0; i < e3; i++) {
                        int u = scanner.nextInt();
                        int v = scanner.nextInt();
                        long cost = scanner.nextLong();
                        maximizer.addDirectedEdge(u, v, cost);
                        allEdges.add(new DrawingPanel.EdgeUI(u, v, cost, false));
                    }

                    // Correccion PDF: Cross-check estricto entre Bellman-Ford y Floyd-Warshall
                    long[] bfDist = maximizer.runBellmanFord(start3);
                    long bfAns = bfDist[dest3];
                    String resBF = maximizer.evaluateResult(caseNum, bfAns);
                    
                    long[][] fwMatrix = maximizer.runFloydWarshall();
                    long fwAns = fwMatrix[start3][dest3];
                    String resFW = maximizer.evaluateResult(caseNum, fwAns);
                    
                    sbRes.append(resBF);
                    
                    if (bfAns != fwAns) {
                        sbRes.append(" [WARNING: Mismatch! Bellman-Ford vs Floyd-Warshall!]");
                    }
                    sbRes.append("\n");

                    if (caseNum == t) {
                        if (n3 > 60) {
                            drawingPanel.showError("Misión 3: Tamaño excedido (" + n3 + " nodos). Máximo permitido: 60 nodos.\n" + resBF);
                        } else {
                            drawingPanel.setGraphData(n3, allEdges, allEdges, "Minerva vs Limón", resBF, 0);
                        }
                    }
                    
                    if (caseNum == t) {
                        StringBuilder sb = new StringBuilder("Matriz Cruzada N x N (Floyd-Warshall - Último Caso):\n\n");
                        for (int i = 0; i < n3; i++) {
                            for (int j = 0; j < n3; j++) {
                                long val = fwMatrix[i][j];
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
                    }
                }
                outputArea.setText("Evaluación (Bellman-Ford):\n" + sbRes.toString() + "\n\nRevisa la pestaña contigua para ver la Matriz de Floyd-Warshall.");
                bottomTabs.setSelectedIndex(1); 
                break;
            }
            case 4: {
                int t = scanner.nextInt();
                StringBuilder sbRes = new StringBuilder();
                for (int caseNum = 1; caseNum <= t; caseNum++) {
                    int n4 = scanner.nextInt();
                    int c4 = scanner.nextInt();
                    
                    List<DrawingPanel.EdgeUI> allEdges = new ArrayList<>();
                    NetworkReconnector kruskal = new NetworkReconnector(n4);
                    for (int i = 0; i < c4; i++) {
                        int u = scanner.nextInt() - 1;
                        int v = scanner.nextInt() - 1;
                        long cost = scanner.nextLong();
                        
                        kruskal.addCable(u, v, cost);
                        allEdges.add(new DrawingPanel.EdgeUI(u, v, cost, true));
                    }
                    
                    String res = kruskal.solve(caseNum);
                    sbRes.append(res).append("\n");
                    
                    if (caseNum == t) {
                        if (n4 > 100 || c4 > 300) {
                            drawingPanel.showError("Misión 4: Tamaño excedido (" + n4 + " nodos, " + c4 + " cables). Máximo permitido: 100 y 300.\n" + res);
                        } else {
                            List<DrawingPanel.EdgeUI> mst = computeKruskalSolution(n4, allEdges);
                            drawingPanel.setGraphData(n4, allEdges, mst, "Red de Kruskal (MST)", res, 1);
                        }
                    }
                }
                outputArea.setText(sbRes.toString());
                bottomTabs.setSelectedIndex(0);
                break;
            }
        }
        
        scanner.close();
    }
    
    // Mini BFS para recuperar el camino exacto de la Grilla (Aislando la GUI del core)
    private List<Point> computeGridBFS(int rows, int cols, boolean[][] mines, int startR, int startC, int destR, int destC) {
        List<Point> path = new ArrayList<>();
        if (mines[startR][startC] || mines[destR][destC]) return path;
        if (startR == destR && startC == destC) {
            path.add(new Point(startC, startR));
            return path;
        }
        
        int n = rows * cols;
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, -1);
        Arrays.fill(prev, -1);
        
        int startIdx = startR * cols + startC;
        dist[startIdx] = 0;
        
        Queue<Integer> q = new LinkedList<>();
        q.add(startIdx);
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        
        while(!q.isEmpty()) {
            int u = q.poll();
            int r = u / cols;
            int c = u % cols;
            if (r == destR && c == destC) break;
            
            for(int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !mines[nr][nc]) {
                    int v = nr * cols + nc;
                    if (dist[v] == -1) {
                        dist[v] = dist[u] + 1;
                        prev[v] = u;
                        q.add(v);
                    }
                }
            }
        }
        
        int curr = destR * cols + destC;
        if (dist[curr] != -1) {
            while(curr != startIdx) {
                path.add(new Point(curr % cols, curr / cols));
                curr = prev[curr];
            }
            path.add(new Point(startC, startR));
            Collections.reverse(path);
        }
        return path;
    }

    private List<DrawingPanel.EdgeUI> computeKruskalSolution(int n, List<DrawingPanel.EdgeUI> edges) {
        List<DrawingPanel.EdgeUI> sorted = new ArrayList<>(edges);
        sorted.sort((a, b) -> Long.compare(a.cost, b.cost));
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        
        List<DrawingPanel.EdgeUI> mst = new ArrayList<>();
        for (DrawingPanel.EdgeUI e : sorted) {
            int rootU = find(parent, e.u);
            int rootV = find(parent, e.v);
            if (rootU != rootV) {
                parent[rootU] = rootV;
                mst.add(e);
            }
        }
        return mst;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    private List<DrawingPanel.EdgeUI> computeDijkstraSolution(int n, List<DrawingPanel.EdgeUI> edges, int start, int dest) {
        List<DrawingPanel.EdgeUI> path = new ArrayList<>();
        long[] dist = new long[n];
        int[] prev = new int[n];
        DrawingPanel.EdgeUI[] prevEdge = new DrawingPanel.EdgeUI[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> Long.compare(dist[a], dist[b]));
        pq.add(start);

        List<List<DrawingPanel.EdgeUI>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (DrawingPanel.EdgeUI e : edges) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(e);
        }

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (u == dest) break;

            for (DrawingPanel.EdgeUI e : adj.get(u)) {
                int v = (e.u == u) ? e.v : e.u;
                if (dist[u] + e.cost < dist[v]) {
                    dist[v] = dist[u] + e.cost;
                    prev[v] = u;
                    prevEdge[v] = e;
                    pq.add(v);
                }
            }
        }

        int curr = dest;
        while (prev[curr] != -1) {
            path.add(prevEdge[curr]);
            curr = prev[curr];
        }
        return path;
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

class DrawingPanel extends JPanel {
    private String message = "Área de Visión Mística";
    private boolean isError = false;
    private int type = 0; // 0: vacio/error, 1: grilla, 2: grafo
    
    private int nodesOrRows, edgesOrCols;
    private String title = "";
    private String finalAnswer = "";
    private List<EdgeUI> allEdges = new ArrayList<>();
    private List<EdgeUI> solutionEdges = new ArrayList<>();
    
    // Grid specific variables
    private boolean[][] mines;
    private Point startNode;
    private Point destNode;
    private List<Point> gridPath;
    private int indexOffset = 0;

    public static class EdgeUI {
        int u, v;
        long cost;
        boolean isBidirectional;
        public EdgeUI(int u, int v, long cost, boolean isBidirectional) {
            this.u = u; this.v = v; this.cost = cost; this.isBidirectional = isBidirectional;
        }
    }

    public DrawingPanel() {
        setBackground(new Color(30, 30, 35));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                "Ojo de Minerva (Lienzo Java2D)",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE));
    }

    public void clear() {
        type = 0;
        message = "Esperando la llamada heroica...";
        isError = false;
        repaint();
    }

    public void showError(String error) {
        type = 0;
        message = error;
        isError = true;
        repaint();
    }

    public void drawGridExtended(int rows, int cols, boolean[][] mines, Point start, Point dest, List<Point> path, String finalAnswer) {
        this.nodesOrRows = rows;
        this.edgesOrCols = cols;
        this.mines = mines;
        this.startNode = start;
        this.destNode = dest;
        this.gridPath = path;
        this.finalAnswer = finalAnswer;
        this.type = 1;
        this.isError = false;
        repaint();
    }

    public void setGraphData(int nodes, List<EdgeUI> allEdges, List<EdgeUI> solutionEdges, String title, String finalAnswer, int indexOffset) {
        this.nodesOrRows = nodes;
        this.allEdges = allEdges;
        this.solutionEdges = solutionEdges;
        this.title = title;
        this.finalAnswer = finalAnswer;
        this.indexOffset = indexOffset;
        this.type = 2;
        this.isError = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (type == 0) {
            g2.setColor(isError ? new Color(255, 100, 100) : Color.LIGHT_GRAY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String[] lines = message.split("\n");
            int yOffset = - (lines.length * 20) / 2;
            for (String line : lines) {
                FontMetrics fm = g2.getFontMetrics();
                int msgW = fm.stringWidth(line);
                g2.drawString(line, (w - msgW) / 2, h / 2 + yOffset);
                yOffset += 20;
            }
        } else if (type == 1) { 
            // Dibujo de grilla (M1) mejorado
            g2.setColor(new Color(100, 200, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Misión 1: Rescate de Nina (" + nodesOrRows + "x" + edgesOrCols + ")", 20, 30);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString("Respuesta: " + finalAnswer, 20, 50);
            
            int cellSize = Math.min((w - 40) / edgesOrCols, (h - 70) / nodesOrRows);
            if (cellSize < 2) cellSize = 2;
            
            int startX = (w - (edgesOrCols * cellSize)) / 2;
            int startY = (h - (nodesOrRows * cellSize)) / 2 + 30;
            
            for (int r = 0; r < nodesOrRows; r++) {
                for (int c = 0; c < edgesOrCols; c++) {
                    int px = startX + c * cellSize;
                    int py = startY + r * cellSize;
                    
                    // Pintar Camino Resultante (Verde)
                    if (gridPath != null && gridPath.contains(new Point(c, r))) {
                        g2.setColor(new Color(50, 255, 50, 200));
                        g2.fillRect(px, py, cellSize, cellSize);
                    }
                    
                    // Pintar Minas/Obstáculos (Rojo / X)
                    if (mines != null && mines[r][c]) {
                        g2.setColor(new Color(255, 80, 80));
                        g2.fillRect(px, py, cellSize, cellSize);
                        if (cellSize > 10) {
                            g2.setColor(Color.DARK_GRAY);
                            g2.setStroke(new BasicStroke(2));
                            g2.drawLine(px + 4, py + 4, px + cellSize - 4, py + cellSize - 4);
                            g2.drawLine(px + cellSize - 4, py + 4, px + 4, py + cellSize - 4);
                        }
                    }
                    
                    // Dibujar el borde de la celda
                    g2.setStroke(new BasicStroke(1));
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.drawRect(px, py, cellSize, cellSize);
                    
                    // Pintar 'S' (Inicio) y 'N' (Nina / Destino)
                    if (startNode != null && startNode.x == c && startNode.y == r) {
                        drawLetterCell(g2, "S", px, py, cellSize, new Color(50, 150, 255));
                    }
                    if (destNode != null && destNode.x == c && destNode.y == r) {
                        drawLetterCell(g2, "N", px, py, cellSize, new Color(255, 215, 0));
                    }
                }
            }
        } else if (type == 2) { 
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Grafo: " + title, 20, 30);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString("Respuesta: " + finalAnswer, 20, 50);
            
            int centerX = w / 2;
            int centerY = h / 2 + 30;
            int radius = Math.min(w, h) / 3;
            
            Point[] coords = new Point[nodesOrRows];
            for (int i = 0; i < nodesOrRows; i++) {
                double angle = 2 * Math.PI * i / nodesOrRows;
                int nx = centerX + (int)(radius * Math.cos(angle));
                int ny = centerY + (int)(radius * Math.sin(angle));
                coords[i] = new Point(nx, ny);
            }

            g2.setStroke(new BasicStroke(1));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (EdgeUI edge : allEdges) {
                if (edge.u >= nodesOrRows || edge.v >= nodesOrRows) continue;
                Point p1 = coords[edge.u];
                Point p2 = coords[edge.v];
                
                g2.setColor(new Color(150, 150, 150, 120)); 
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(String.valueOf(edge.cost), midX, midY - 5);
            }

            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(50, 255, 50)); 
            for (EdgeUI edge : solutionEdges) {
                if (edge.u >= nodesOrRows || edge.v >= nodesOrRows) continue;
                Point p1 = coords[edge.u];
                Point p2 = coords[edge.v];
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < nodesOrRows; i++) {
                Point p = coords[i];
                g2.setColor(new Color(70, 130, 180)); 
                g2.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2.setColor(Color.WHITE);
                g2.drawOval(p.x - 12, p.y - 12, 24, 24);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String text = String.valueOf(i + indexOffset); 
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(text);
                int textH = fm.getAscent();
                g2.drawString(text, p.x - textW / 2, p.y + textH / 2 - 2);
            }
        }
    }
    
    // Metodo helper para dibujar S y N centrados en una celda
    private void drawLetterCell(Graphics2D g2, String letter, int px, int py, int cellSize, Color bgColor) {
        g2.setColor(bgColor);
        g2.fillRect(px, py, cellSize, cellSize);
        g2.setColor(Color.WHITE);
        g2.drawRect(px, py, cellSize, cellSize);
        
        int fontSize = Math.max(10, cellSize - 6);
        g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(letter);
        int textH = fm.getAscent();
        g2.drawString(letter, px + (cellSize - textW) / 2, py + (cellSize + textH) / 2 - 2);
    }
}
