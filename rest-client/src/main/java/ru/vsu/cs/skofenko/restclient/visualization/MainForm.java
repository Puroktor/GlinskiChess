package ru.vsu.cs.skofenko.restclient.visualization;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessColor;
import ru.vsu.cs.skofenko.logic.chesspieces.ChessPiece;
import ru.vsu.cs.skofenko.logic.geometry.Coordinate;
import ru.vsu.cs.skofenko.logic.model.BoardCell;
import ru.vsu.cs.skofenko.logic.model.GameLogic;
import ru.vsu.cs.skofenko.logic.model.IGameLogic;
import ru.vsu.cs.skofenko.restclient.RestClient;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainForm extends JFrame {
    private class BoardPanel extends JPanel {
        private int size = 50;
        private final Polygon[][] board = new Polygon[IGameLogic.N][IGameLogic.N];
        private final Color[] colors = new Color[]{
                new Color(255, 206, 158),
                new Color(232, 171, 111),
                new Color(209, 139, 71)};

        public void setCellSize(int size) {
            this.size = size;
        }

        @Override
        public void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            setRenderHints((Graphics2D) gr);
            if (logic == null) {
                return;
            }
            int half = IGameLogic.N / 2;
            int count = 200;
            Coordinate selectedCord = logic.getSelectedCord();
            BoardCell[][] boardCells = logic.getBoard();
            for (int r = -half; r <= half; r++) {
                for (int q = -half; q <= half; q++) {
                    count--;
                    if (Math.abs(r + q) > half)
                        continue;
                    Coordinate cord = Coordinate.createFromAxial(r, q);
                    BoardCell cell = boardCells[cord.getI()][cord.getJ()];
                    if (cell == null) {
                        break;
                    }
                    board[cord.getI()][cord.getJ()] = new Polygon();
                    int centerX = (int) Math.round(size * (1.5 * q + half * 2));
                    int centerY = (int) Math.round(size * (Math.sqrt(3) * (r + 0.5 * q) + half * 2));
                    for (int k = 0; k < 6; k++) {
                        board[cord.getI()][cord.getJ()].addPoint(centerX + (int) Math.round(size * Math.cos(k * 2 * Math.PI / 6)),
                                centerY + (int) Math.round(size * Math.sin(k * 2 * Math.PI / 6)));
                    }
                    if (cord.equals(selectedCord)) {
                        paintPolygon(board[cord.getI()][cord.getJ()], gr, Color.YELLOW);
                    } else if (cell.isCapturable()) {
                        paintPolygon(board[cord.getI()][cord.getJ()], gr, Color.RED);
                    } else {
                        paintPolygon(board[cord.getI()][cord.getJ()], gr, colors[(count) % 3]);
                    }
                    if (cell.isReachable()) {
                        drawReachable(gr, centerX, centerY, size / 2);
                    }
                    drawChessPiece(gr, cell.getPiece(), centerX, centerY);
                }
            }
        }

        private void setRenderHints(Graphics2D gr) {
            gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gr.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }

        private void drawChessPiece(Graphics gr, ChessPiece piece, int x, int y) {
            if (piece != null) {
                gr.drawImage(ChessPieceIcons.getIcon(piece), x - size / 2, y - size / 2, size, size, null);
            }
        }

        private void paintPolygon(Polygon p, Graphics gr, Color color) {
            gr.setColor(color);
            gr.fillPolygon(p);
            gr.setColor(Color.BLACK);
            gr.drawPolygon(p);
        }

        private void drawReachable(Graphics gr, int centerX, int centerY, int size) {
            gr.setColor(Color.YELLOW);
            gr.fillOval(centerX - size / 2, centerY - size / 2, size, size);
        }

        public void panelClicked(int x, int y) {
            for (int i = 0; i < IGameLogic.N; i++) {
                for (int j = 0; j < IGameLogic.N; j++) {
                    if (board[i][j] != null && board[i][j].contains(x, y)) {
                        if (logic.selectPiece(Coordinate.createFromInner(i, j))) {
                            logic.promotePawn(showDialogWindow());
                        }
                        return;
                    }
                }
            }
        }
    }

    private class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            BoardPanel panel = (BoardPanel) e.getComponent();
            panel.setCellSize((int) Math.round(Math.min(panel.getWidth(), panel.getHeight()) / (1.8 * IGameLogic.N)));
        }
    }

    private JPanel mainPanel;
    private JPanel boardPanelCont;
    private JPanel player2Panel;
    private JPanel player1Panel;
    private JLabel stateLabel;
    private JComboBox<String> comboBox;
    private JLabel player2Label;
    private JLabel player1Label;

    private final ResourceBundle bundle;
    private static final JLabel loadingIcon = new JLabel(new ImageIcon(MainForm.class.getResource("/static/LoadingIcon.gif")));
    private IGameLogic logic;

    public MainForm(Locale locale) {
        bundle = ResourceBundle.getBundle("main-form", locale);
        this.setTitle(bundle.getString("app-title"));
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        player1Label.setText(bundle.getString("player1-name"));
        player2Label.setText(bundle.getString("player2-name"));

        comboBox.addItem(bundle.getString("box-single"));
        comboBox.addItem(bundle.getString("box-multi"));

        setSize(640, 480);
        setExtendedState(MAXIMIZED_BOTH);

        logic = new GameLogic();
        stateLabel.setText(bundle.getString(logic.getGameState().name().toLowerCase()));

        boardPanelCont.setLayout(new GridBagLayout());
        SquarePanel square = new SquarePanel();
        boardPanelCont.add(square);
        square.setLayout(new BorderLayout());

        BoardPanel boardPanel = new BoardPanel();
        square.add(boardPanel, BorderLayout.CENTER);
        boardPanel.addComponentListener(new ResizeListener());

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ((BoardPanel) e.getComponent()).panelClicked(e.getX(), e.getY());
                repaint();
            }
        });

        comboBox.addActionListener(new ActionListener() {
            private Thread newLogicThread;

            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> jComboBox = (JComboBox<String>) e.getSource();
                if (newLogicThread != null && newLogicThread.isAlive()) {
                    newLogicThread.interrupt();
                }
                if (logic instanceof RestClient) {
                    ((RestClient) logic).terminate();
                }
                logic = null;
                newLogicThread = new Thread(() -> {
                    if (jComboBox.getSelectedIndex() == 0) {
                        setPanelsVisibility(true);
                        logic = new GameLogic();
                        MainForm.this.repaint();
                    } else {
                        stateLabel.setText(bundle.getString("waiting-label"));
                        setPanelsVisibility(false);
                        RestClient restClient = new RestClient(MainForm.this::returnToSinglePlayer);
                        logic = restClient;
                        restClient.waitForOtherPlayer(MainForm.this::repaint);
                        setPanelsVisibility(true);
                    }
                });
                newLogicThread.start();
            }

            private void setPanelsVisibility(boolean value) {
                square.setVisible(value);
                if (value) {
                    boardPanelCont.remove(loadingIcon);
                } else {
                    boardPanelCont.add(loadingIcon);
                }
                boardPanelCont.validate();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (logic instanceof RestClient) {
                    ((RestClient) logic).terminate();
                }
            }
        });

    }

    public ChessPiece showDialogWindow() {
        String[] possibilities = bundle.getString("option-possibilities").split("[ \\t]+");
        while (true) {
            try {
                String str = (String) JOptionPane.showInputDialog(
                        this,
                        bundle.getString("option-mes"),
                        bundle.getString("option-title"),
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities,
                        possibilities[0]);

                return ChessPiece.getChessPieceFromStr(str, logic.getNowTurn());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void returnToSinglePlayer(String key) {
        comboBox.setSelectedIndex(0);
        JOptionPane.showMessageDialog(this, bundle.getString(key), bundle.getString("message"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void repaint() {
        if (logic.getNowTurn() == ChessColor.WHITE) {
            player1Panel.setBackground(Color.RED);
            player2Panel.setBackground(Color.LIGHT_GRAY);
        } else {
            player2Panel.setBackground(Color.RED);
            player1Panel.setBackground(Color.LIGHT_GRAY);
        }
        stateLabel.setText(bundle.getString(logic.getGameState().name().toLowerCase()));
        super.repaint();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        boardPanelCont = new JPanel();
        boardPanelCont.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(boardPanelCont, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        player2Panel = new JPanel();
        player2Panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        player2Panel.setBackground(new Color(-4144960));
        mainPanel.add(player2Panel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(135, -1), new Dimension(135, -1), new Dimension(135, -1), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(10, 5, 10, 5), -1, -1));
        player2Panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        player2Label = new JLabel();
        Font player2LabelFont = this.$$$getFont$$$("Arial Black", -1, 18, player2Label.getFont());
        if (player2LabelFont != null) player2Label.setFont(player2LabelFont);
        player2Label.setText("");
        panel1.add(player2Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-16777216));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), new Dimension(30, 30), new Dimension(30, 30), 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 0, 5), -1, -1));
        player2Panel.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        stateLabel = new JLabel();
        Font stateLabelFont = this.$$$getFont$$$("Arial Black", -1, 16, stateLabel.getFont());
        if (stateLabelFont != null) stateLabel.setFont(stateLabelFont);
        stateLabel.setForeground(new Color(-4972032));
        stateLabel.setText("");
        panel3.add(stateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        player1Panel = new JPanel();
        player1Panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        player1Panel.setBackground(new Color(-65536));
        mainPanel.add(player1Panel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(135, 55), new Dimension(135, 55), new Dimension(135, 55), 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(10, 5, 10, 5), -1, -1));
        player1Panel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(-1, 50), 0, false));
        player1Label = new JLabel();
        Font player1LabelFont = this.$$$getFont$$$("Arial Black", -1, 18, player1Label.getFont());
        if (player1LabelFont != null) player1Label.setFont(player1LabelFont);
        player1Label.setText("");
        panel4.add(player1Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setBackground(new Color(-1));
        panel4.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), new Dimension(30, 30), new Dimension(30, 30), 0, false));
        comboBox = new JComboBox();
        Font comboBoxFont = this.$$$getFont$$$("Arial Black", -1, 12, comboBox.getFont());
        if (comboBoxFont != null) comboBox.setFont(comboBoxFont);
        mainPanel.add(comboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(135, 25), new Dimension(135, 25), new Dimension(135, 25), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
