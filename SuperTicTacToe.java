import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// --- THE MAIN CLASS ---
public class SuperTicTacToe extends JFrame {

    private DarkTextField fieldPlayerX;
    private DarkTextField fieldPlayerO;

    public SuperTicTacToe() {
        super("Tic-Tac-Toe Arena 🎮");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 650);
        
        // Custom Gradient Background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 30), 0, h, new Color(10, 10, 15));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Central Card
        RoundedPanel container = new RoundedPanel(30, new Color(40, 40, 45));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("TIC-TAC-TOE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(220, 220, 220));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("ARENA");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        subtitleLabel.setForeground(GameColors.BLUE_X);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Inputs
        JPanel inputsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        inputsPanel.setOpaque(false);
        inputsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputsPanel.setMaximumSize(new Dimension(300, 200));

        inputsPanel.add(createLabel("PLAYER 1 (X)"));
        fieldPlayerX = new DarkTextField("");
        inputsPanel.add(fieldPlayerX);

        inputsPanel.add(Box.createVerticalStrut(10)); 

        inputsPanel.add(createLabel("PLAYER 2 (O)"));
        fieldPlayerO = new DarkTextField(""); 
        inputsPanel.add(fieldPlayerO);

        // Main Menu Buttons
        MenuButton btnSuper = new MenuButton("PLAY SUPER MODE", GameColors.BLUE_X);
        btnSuper.addActionListener(e -> openSettings("SUPER"));
        
        MenuButton btnRegular = new MenuButton("PLAY CLASSIC MODE", GameColors.RED_O);
        btnRegular.addActionListener(e -> openSettings("REGULAR"));

        container.add(titleLabel);
        container.add(subtitleLabel);
        container.add(Box.createVerticalStrut(20));
        container.add(inputsPanel);
        container.add(Box.createVerticalStrut(40));
        container.add(btnSuper);
        container.add(Box.createVerticalStrut(15));
        container.add(btnRegular);

        mainPanel.add(container);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setVerticalAlignment(SwingConstants.BOTTOM);
        return lbl;
    }

    private void openSettings(String type) {
        String p1 = fieldPlayerX.getText().trim();
        String p2 = fieldPlayerO.getText().trim();
        if (p1.isEmpty()) p1 = "Player 1";
        if (p2.isEmpty()) p2 = "Player 2";

        ModernSettingsDialog dialog = new ModernSettingsDialog(this, type);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            dispose();
            if (type.equals("SUPER")) {
                new SuperTicTacToeGame(p1, p2, dialog.getTargetWins(), dialog.getTimeLimit());
            } else {
                new RegularTicTacToe(p1, p2, dialog.getTargetWins(), dialog.getTimeLimit());
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(SuperTicTacToe::new);
    }
}

// --- MODERN SETTINGS DIALOG (With Rules Button) ---
class ModernSettingsDialog extends JDialog {
    private boolean confirmed = false;
    private int targetWins = 1;
    private int timeLimit = -1;
    private DarkComboBox<String> roundsBox;
    private DarkComboBox<String> timerBox;

    public ModernSettingsDialog(JFrame parent, String type) {
        super(parent, "Match Settings", true);
        setUndecorated(true);
        setSize(400, 520); // Slightly taller for rules button
        setLocationRelativeTo(parent);
        setBackground(new Color(0,0,0,0));

        RoundedPanel mainPanel = new RoundedPanel(20, new Color(30, 30, 35));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("MATCH SETTINGS");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel(type.equals("SUPER") ? "Super Tic-Tac-Toe" : "Classic Tic-Tac-Toe");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(GameColors.BLUE_X);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 5));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(300, 200));

        formPanel.add(createLabel("SERIES LENGTH"));
        roundsBox = new DarkComboBox<>(new String[]{"1 Round (Sudden Death)", "Best of 3", "Best of 5"});
        formPanel.add(roundsBox);

        formPanel.add(createLabel("MOVE TIMER"));
        timerBox = new DarkComboBox<>(new String[]{"No Timer", "5 Seconds", "10 Seconds", "30 Seconds"});
        formPanel.add(timerBox);

        // Buttons
        MenuButton btnRules = null;
        // ONLY show rules button if it is Super Mode
        if (type.equals("SUPER")) {
            btnRules = new MenuButton("READ RULES", new Color(100, 100, 100));
            btnRules.addActionListener(e -> new RulesDialog(this).setVisible(true));
        }

        MenuButton btnStart = new MenuButton("START MATCH", GameColors.BLUE_X);
        btnStart.addActionListener(e -> {
            processSettings();
            confirmed = true;
            setVisible(false);
        });

        MenuButton btnCancel = new MenuButton("CANCEL", new Color(60, 60, 60));
        btnCancel.addActionListener(e -> setVisible(false));

        mainPanel.add(title);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        
        if (btnRules != null) {
            mainPanel.add(btnRules);
            mainPanel.add(Box.createVerticalStrut(10));
        }
        
        mainPanel.add(btnStart);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(btnCancel);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        return lbl;
    }

    private void processSettings() {
        if (roundsBox.getSelectedIndex() == 1) targetWins = 2;
        if (roundsBox.getSelectedIndex() == 2) targetWins = 3;

        String timeStr = (String) timerBox.getSelectedItem();
        if (timeStr.contains("5 ")) timeLimit = 5;
        if (timeStr.contains("10")) timeLimit = 10;
        if (timeStr.contains("30")) timeLimit = 30;
    }

    public boolean isConfirmed() { return confirmed; }
    public int getTargetWins() { return targetWins; }
    public int getTimeLimit() { return timeLimit; }
}

// --- NEW RULES DIALOG ---
class RulesDialog extends JDialog {
    public RulesDialog(JDialog parent) {
        super(parent, "How to Play", true);
        setUndecorated(true);
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setBackground(new Color(0,0,0,0));

        RoundedPanel mainPanel = new RoundedPanel(20, new Color(25, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("HOW TO PLAY");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("The Strategy of Super Tic-Tac-Toe");
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subtitle.setForeground(GameColors.BLUE_X);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rules Text Area
        JTextArea rulesText = new JTextArea();
        rulesText.setText(
            "\n1. THE OBJECTIVE\n" +
            "Win 3 small boards in a row (horizontally, vertically, or diagonally) to win the game.\n\n" +
            "2. THE TWIST (FORCING MOVE)\n" +
            "You don't just pick any board! If you place your mark in the TOP-RIGHT square of a small board, your opponent MUST play in the TOP-RIGHT small board of the big grid.\n\n" +
            "3. FREE PLAY\n" +
            "If your opponent sends you to a board that is already full or won, you are free to play anywhere you like!\n\n" +
            "4. WINNING A BOARD\n" +
            "Standard Tic-Tac-Toe rules apply to each small board. Once won, the board turns your color."
        );
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rulesText.setForeground(new Color(200, 200, 200));
        rulesText.setBackground(new Color(25, 25, 30));
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setEditable(false);
        rulesText.setFocusable(false);
        rulesText.setBorder(new EmptyBorder(20, 10, 20, 10));

        MenuButton btnGotIt = new MenuButton("I UNDERSTAND", GameColors.BLUE_X);
        btnGotIt.addActionListener(e -> dispose());

        mainPanel.add(title);
        mainPanel.add(subtitle);
        mainPanel.add(new JScrollPane(rulesText) {
            {
                setBorder(null);
                getViewport().setBackground(new Color(25, 25, 30));
            }
        });
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnGotIt);

        add(mainPanel);
    }
}

// --- CUSTOM UI COMPONENTS ---

class DarkComboBox<E> extends JComboBox<E> {
    public DarkComboBox(E[] items) {
        super(items);
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(40, 40, 45)); 
                        g2.fillRect(0,0,getWidth(),getHeight());
                        g2.setColor(Color.WHITE);
                        int size = 6;
                        int x = (getWidth()-size)/2;
                        int y = (getHeight()-size)/2;
                        g2.fillPolygon(new int[]{x, x+size, x+size/2}, new int[]{y, y, y+size}, 3);
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? GameColors.BLUE_X : new Color(40, 40, 45));
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        setBackground(new Color(25, 25, 30));
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBorder(BorderFactory.createLineBorder(new Color(60,60,60), 1));
    }
}

class RoundedPanel extends JPanel {
    private int radius;
    private Color bgColor;

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        this.bgColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class DarkTextField extends JTextField {
    public DarkTextField(String text) {
        super(text);
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBorder(new EmptyBorder(10, 15, 10, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(25, 25, 30));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.dispose();
        super.paintComponent(g);
    }
}

class MenuButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private boolean isHovered = false;

    public MenuButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        this.hoverColor = color.brighter();
        
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setMaximumSize(new Dimension(300, 50));
        setPreferredSize(new Dimension(300, 50));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHovered ? hoverColor : baseColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50); 
        g2.dispose();
        super.paintComponent(g);
    }
}

class SmallBackButton extends JButton {
    public SmallBackButton() {
        super("← Back");
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(80, 30));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
}

class GameColors {
    public static final Color BG_DARK = new Color(18, 18, 18); 
    public static final Color BTN_DARK = new Color(44, 44, 44); 
    public static final Color BLUE_X = new Color(94, 151, 246); 
    public static final Color RED_O = new Color(226, 92, 92);   
    public static final Color BTN_POPUP = new Color(240, 240, 240); 
}

class RoundedButton extends JButton {
    private Color currentColor;
    private boolean isWonState = false;

    public RoundedButton() {
        super("");
        setFont(new Font("Arial", Font.BOLD, 55));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        this.currentColor = GameColors.BTN_DARK;
    }

    public void setWonBackground(Color bg) {
        this.currentColor = bg;
        this.isWonState = true;
        repaint();
    }
    
    public void resetState() {
        this.currentColor = GameColors.BTN_DARK;
        this.isWonState = false;
        setText("");
        setEnabled(true);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isWonState ? currentColor : GameColors.BTN_DARK);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}

// --- GAME 1: REGULAR TIC-TAC-TOE ---
class RegularTicTacToe extends JFrame {
    private String pX, pO;
    private int targetWins;
    private int scoreX = 0, scoreO = 0;
    private char currentPlayer = 'X';
    private JButton[][] buttons = new JButton[3][3];
    private boolean gameOver = false;
    private int timeLimit, timeLeft;
    private Timer gameTimer;
    private JLabel lblTimer, lblScore;

    public RegularTicTacToe(String pX, String pO, int targetWins, int timeLimit) {
        super("Regular Tic-Tac-Toe");
        this.pX = pX; this.pO = pO;
        this.targetWins = targetWins;
        this.timeLimit = timeLimit;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(GameColors.BG_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        SmallBackButton btnBack = new SmallBackButton();
        btnBack.addActionListener(e -> goBack());
        topPanel.add(btnBack, BorderLayout.WEST);

        lblScore = new JLabel(getScoreText(), SwingConstants.CENTER);
        lblScore.setForeground(Color.WHITE);
        lblScore.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(lblScore, BorderLayout.CENTER);

        lblTimer = new JLabel(timeLimit > 0 ? "⏳ " + timeLimit : "", SwingConstants.RIGHT);
        lblTimer.setForeground(GameColors.BLUE_X);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 18));
        lblTimer.setPreferredSize(new Dimension(80, 30));
        topPanel.add(lblTimer, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(GameColors.BG_DARK); 
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 140)); 
                btn.setFocusPainted(false);
                btn.setBackground(GameColors.BTN_DARK);
                btn.setBorderPainted(false);
                
                final int row = r; final int col = c;
                btn.addActionListener(e -> playMove(btn));
                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
        startTimer();
    }

    private void goBack() {
        if (gameTimer != null) gameTimer.stop();
        dispose();
        new SuperTicTacToe();
    }

    private String getScoreText() {
        return pX + ": " + scoreX + " | " + pO + ": " + scoreO + " (Target: " + targetWins + ")";
    }

    private void startTimer() {
        if (timeLimit <= 0) return;
        timeLeft = timeLimit;
        lblTimer.setText("⏳ " + timeLeft);
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText("⏳ " + timeLeft);
            if (timeLeft <= 0) {
                gameTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Turn lost.");
                switchTurn();
            }
        });
        gameTimer.start();
    }

    private void playMove(JButton btn) {
        if (!btn.getText().isEmpty() || gameOver) return;
        if (gameTimer != null) gameTimer.stop();

        btn.setText(String.valueOf(currentPlayer));
        btn.setForeground(currentPlayer == 'X' ? GameColors.BLUE_X : GameColors.RED_O);

        if (checkWin()) {
            handleRoundWin(currentPlayer);
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "Round Draw!");
            resetBoard();
        } else {
            switchTurn();
        }
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        lblTimer.setForeground(currentPlayer == 'X' ? GameColors.BLUE_X : GameColors.RED_O);
        startTimer();
    }

    private void handleRoundWin(char winner) {
        if (winner == 'X') scoreX++; else scoreO++;
        lblScore.setText(getScoreText());

        if (scoreX >= targetWins || scoreO >= targetWins) {
            showCustomWinDialog(winner == 'X' ? GameColors.BLUE_X : GameColors.RED_O, 
                "CHAMPION: " + (winner == 'X' ? pX : pO) + "!");
            goBack();
        } else {
            JOptionPane.showMessageDialog(this, (winner == 'X' ? pX : pO) + " wins this round!");
            resetBoard();
        }
    }
    
    private void showCustomWinDialog(Color bg, String msg) {
        JDialog d = new JDialog(this, "Game Over", true);
        d.setUndecorated(true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        
        JLabel l = new JLabel(msg);
        l.setFont(new Font("Arial", Font.BOLD, 24));
        l.setForeground(Color.WHITE);
        
        JButton b = new JButton("OK");
        b.setBackground(GameColors.BTN_POPUP);
        b.setForeground(bg);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.addActionListener(e -> d.dispose());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);
        c.gridx=0; c.gridy=0; p.add(l, c);
        c.gridy=1; p.add(b, c);
        
        d.add(p);
        d.setSize(400, 200);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void resetBoard() {
        gameOver = false;
        currentPlayer = 'X';
        for (int r=0; r<3; r++) for (int c=0; c<3; c++) {
            buttons[r][c].setText("");
            buttons[r][c].setEnabled(true);
            buttons[r][c].setBackground(GameColors.BTN_DARK);
        }
        startTimer();
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (check(buttons[i][0], buttons[i][1], buttons[i][2])) return true;
            if (check(buttons[0][i], buttons[1][i], buttons[2][i])) return true;
        }
        if (check(buttons[0][0], buttons[1][1], buttons[2][2])) return true;
        if (check(buttons[0][2], buttons[1][1], buttons[2][0])) return true;
        return false;
    }
    private boolean check(JButton b1, JButton b2, JButton b3) {
        return !b1.getText().isEmpty() && b1.getText().equals(b2.getText()) && b2.getText().equals(b3.getText());
    }
    private boolean isBoardFull() {
        for(int r=0; r<3; r++) for(int c=0; c<3; c++) if(buttons[r][c].getText().isEmpty()) return false;
        return true;
    }
}

// --- GAME 2: SUPER TIC-TAC-TOE ---
class SuperTicTacToeGame extends JFrame {
    private final int SIZE = 3;
    private RoundedButton[][][][] buttons;
    private JPanel[][] subBoards;
    private char[][] bigBoardStatus;
    
    private char currentPlayer = 'X';
    private String pXName, pOName;
    private int targetWins;
    private int scoreX = 0, scoreO = 0;
    private int timeLimit, timeLeft;
    private Timer gameTimer;
    private JLabel lblTimer, lblScore;

    public SuperTicTacToeGame(String pX, String pO, int targetWins, int timeLimit) {
        super("Super Tic-Tac-Toe");
        this.pXName = pX; this.pOName = pO;
        this.targetWins = targetWins;
        this.timeLimit = timeLimit;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 900);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(GameColors.BG_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        SmallBackButton btnBack = new SmallBackButton();
        btnBack.addActionListener(e -> goBack());
        topPanel.add(btnBack, BorderLayout.WEST);

        lblScore = new JLabel(getScoreText(), SwingConstants.CENTER);
        lblScore.setForeground(Color.WHITE);
        lblScore.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(lblScore, BorderLayout.CENTER);

        lblTimer = new JLabel(timeLimit > 0 ? "⏳ " + timeLimit : "", SwingConstants.RIGHT);
        lblTimer.setForeground(GameColors.BLUE_X);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 18));
        lblTimer.setPreferredSize(new Dimension(80, 30));
        topPanel.add(lblTimer, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel mainGrid = new JPanel(new GridLayout(SIZE, SIZE, 20, 20));
        mainGrid.setBackground(GameColors.BG_DARK); 
        mainGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainGrid, BorderLayout.CENTER);

        initializeGame(mainGrid);
        setVisible(true);
        startTimer();
    }

    private void goBack() {
        if (gameTimer != null) gameTimer.stop();
        dispose();
        new SuperTicTacToe();
    }

    private String getScoreText() {
        return pXName + ": " + scoreX + " | " + pOName + ": " + scoreO + " (Target: " + targetWins + ")";
    }

    private void startTimer() {
        if (timeLimit <= 0) return;
        timeLeft = timeLimit;
        lblTimer.setText("⏳ " + timeLeft);
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText("⏳ " + timeLeft);
            if (timeLeft <= 0) {
                gameTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Turn lost.");
                switchTurn();
            }
        });
        gameTimer.start();
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        lblTimer.setForeground(currentPlayer == 'X' ? GameColors.BLUE_X : GameColors.RED_O);
        setTitle("Super TTT - Turn: " + ((currentPlayer == 'X')?pXName:pOName));
        startTimer();
    }

    private void initializeGame(JPanel mainGrid) {
        buttons = new RoundedButton[SIZE][SIZE][SIZE][SIZE];
        subBoards = new JPanel[SIZE][SIZE];
        bigBoardStatus = new char[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JPanel subPanel = new JPanel(new GridLayout(SIZE, SIZE, 4, 4));
                subPanel.setBackground(GameColors.BG_DARK);
                subBoards[row][col] = subPanel;
                bigBoardStatus[row][col] = ' '; 

                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        RoundedButton btn = new RoundedButton();
                        
                        final int bigR = row; final int bigC = col;
                        final int smallR = r; final int smallC = c;

                        btn.addActionListener(e -> handleMove(bigR, bigC, smallR, smallC));
                        buttons[row][col][r][c] = btn;
                        subPanel.add(btn);
                    }
                }
                mainGrid.add(subPanel);
            }
        }
    }

    private void handleMove(int bigRow, int bigCol, int smallRow, int smallCol) {
        if (bigBoardStatus[bigRow][bigCol] != ' ') return;

        RoundedButton clickedBtn = buttons[bigRow][bigCol][smallRow][smallCol];
        if (!clickedBtn.getText().equals("")) return; 
        
        if (gameTimer != null) gameTimer.stop();

        clickedBtn.setText(String.valueOf(currentPlayer));
        clickedBtn.setForeground(currentPlayer == 'X' ? GameColors.BLUE_X : GameColors.RED_O);

        if (checkSmallWin(bigRow, bigCol, currentPlayer)) {
            bigBoardStatus[bigRow][bigCol] = currentPlayer;
            Color winColor = (currentPlayer == 'X') ? GameColors.BLUE_X : GameColors.RED_O;
            
            for(int r=0; r<3; r++) {
                for(int c=0; c<3; c++) {
                    buttons[bigRow][bigCol][r][c].setWonBackground(winColor);
                    buttons[bigRow][bigCol][r][c].setEnabled(false);
                }
            }
        } else if (isSubBoardFull(bigRow, bigCol)) {
            bigBoardStatus[bigRow][bigCol] = 'D'; 
            disableSubBoard(bigRow, bigCol);
        }

        if (checkBigWin(currentPlayer)) {
            handleSeriesWin(currentPlayer);
            return;
        } 
        
        if (isGlobalFull()) {
            resolveGlobalDraw();
        }

        switchTurn();
    }

    private void handleSeriesWin(char winner) {
        if (winner == 'X') scoreX++; else scoreO++;
        lblScore.setText(getScoreText());
        
        Color winColor = (winner == 'X') ? GameColors.BLUE_X : GameColors.RED_O;
        String wName = (winner == 'X') ? pXName : pOName;

        if (scoreX >= targetWins || scoreO >= targetWins) {
            showCustomWinDialog(winColor, "SERIES CHAMPION: " + wName + "!");
            goBack();
        } else {
            showCustomWinDialog(winColor, wName + " wins this round!");
            resetBoard();
        }
    }

    private void showCustomWinDialog(Color bg, String msg) {
        JDialog d = new JDialog(this, "Winner!", true);
        d.setUndecorated(true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        
        JLabel l = new JLabel(msg);
        l.setFont(new Font("Arial", Font.BOLD, 28));
        l.setForeground(Color.WHITE);
        
        JButton b = new JButton("OK");
        b.setBackground(GameColors.BTN_POPUP);
        b.setForeground(bg); 
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.addActionListener(e -> d.dispose());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(25, 25, 25, 25);
        c.gridx=0; c.gridy=0; p.add(l, c);
        c.gridy=1; p.add(b, c);
        
        d.add(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void resetBoard() {
        currentPlayer = 'X';
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                bigBoardStatus[row][col] = ' ';
                subBoards[row][col].setBackground(GameColors.BG_DARK);
                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        buttons[row][col][r][c].resetState();
                    }
                }
            }
        }
        startTimer();
    }

    private boolean isGlobalFull() {
        for(int r=0; r<SIZE; r++) for(int c=0; c<SIZE; c++) if(bigBoardStatus[r][c] == ' ') return false;
        return true;
    }

    private void resolveGlobalDraw() {
        boolean resetHappened = false;
        for(int r=0; r<SIZE; r++) {
            for(int c=0; c<SIZE; c++) {
                if(bigBoardStatus[r][c] == 'D') {
                    resetSubBoard(r, c);
                    resetHappened = true;
                }
            }
        }
        if (resetHappened) JOptionPane.showMessageDialog(this, "Stalemate! Resetting drawn boards...");
        else {
            JOptionPane.showMessageDialog(this, "Complete Draw! Restarting Round.");
            resetBoard();
        }
    }

    private void resetSubBoard(int row, int col) {
        bigBoardStatus[row][col] = ' '; 
        subBoards[row][col].setBackground(GameColors.BG_DARK); 
        for(int r=0; r<SIZE; r++) {
            for(int c=0; c<SIZE; c++) {
                 buttons[row][col][r][c].resetState();
            }
        }
    }

    private boolean checkSmallWin(int bR, int bC, char p) {
        JButton[][] b = buttons[bR][bC];
        for (int i = 0; i < 3; i++) {
            if (check(b[i][0], b[i][1], b[i][2], p)) return true;
            if (check(b[0][i], b[1][i], b[2][i], p)) return true;
        }
        if (check(b[0][0], b[1][1], b[2][2], p)) return true;
        if (check(b[0][2], b[1][1], b[2][0], p)) return true;
        return false;
    }

    private boolean checkBigWin(char p) {
        for (int i = 0; i < 3; i++) {
            if (bigBoardStatus[i][0] == p && bigBoardStatus[i][1] == p && bigBoardStatus[i][2] == p) return true;
            if (bigBoardStatus[0][i] == p && bigBoardStatus[1][i] == p && bigBoardStatus[2][i] == p) return true;
        }
        if (bigBoardStatus[0][0] == p && bigBoardStatus[1][1] == p && bigBoardStatus[2][2] == p) return true;
        if (bigBoardStatus[0][2] == p && bigBoardStatus[1][1] == p && bigBoardStatus[2][0] == p) return true;
        return false;
    }

    private boolean check(JButton b1, JButton b2, JButton b3, char p) {
        return b1.getText().equals(String.valueOf(p)) &&
               b2.getText().equals(String.valueOf(p)) &&
               b3.getText().equals(String.valueOf(p));
    }

    private boolean isSubBoardFull(int bR, int bC) {
        for (int r = 0; r < SIZE; r++) for (int c = 0; c < SIZE; c++) 
            if (buttons[bR][bC][r][c].getText().isEmpty()) return false;
        return true;
    }

    private void disableSubBoard(int row, int col) {
        for (int r = 0; r < SIZE; r++) for (int c = 0; c < SIZE; c++) 
            buttons[row][col][r][c].setEnabled(false);
    }
}