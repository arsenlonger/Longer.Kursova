package main.ui;

import main.models.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameBoardPanel extends JPanel {
    private final GameFrame mainFrame;
    private Disaster currentDisaster;
    private List<Player> players;
    private int currentRound = 1;

    private JLabel disasterLabel;
    private JTextArea chatArea;
    private JPanel playersPanel;
    private JPanel myCardPanel;

    public GameBoardPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(15, 15, 18));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
    }

    private void initComponents() {
        // Верхня панель - Інформація про катастрофу
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 36));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 87, 51), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        disasterLabel = new JLabel("🌋 КАТАСТРОФА: Завантаження...", SwingConstants.CENTER);
        disasterLabel.setFont(new Font("Serif", Font.BOLD, 22));
        disasterLabel.setForeground(new Color(255, 87, 51));
        topPanel.add(disasterLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Центральна панель - 2D Бункер та список гравців
        playersPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        playersPanel.setOpaque(false);
        add(playersPanel, BorderLayout.CENTER);

        // Права панель - Чат дискусії ботів та гравців
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(340, 0));
        rightPanel.setBackground(new Color(22, 22, 26));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "💬 ЧАТ ОБГОВОРЕННЯ",
                0, 0, new Font("SansSerif", Font.BOLD, 14), Color.WHITE));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chatArea.setBackground(new Color(12, 12, 15));
        chatArea.setForeground(Color.LIGHT_GRAY);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScroll = new JScrollPane(chatArea);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        // Нижня панель - Картка поточного гравця та кнопки дій
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        myCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        myCardPanel.setBackground(new Color(25, 25, 30));
        bottomPanel.add(myCardPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);

        BunkerButton btnVote = new BunkerButton("🗳️ ГПУ (Голосування)");
        btnVote.setPreferredSize(new Dimension(220, 45));
        btnVote.addActionListener(e -> triggerVotingPhase());

        BunkerButton btnExit = new BunkerButton("🚪 Вийти");
        btnExit.setPreferredSize(new Dimension(140, 45));
        btnExit.addActionListener(e -> mainFrame.showPanel("MENU"));

        actionsPanel.add(btnVote);
        actionsPanel.add(btnExit);

        bottomPanel.add(actionsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void startNewGame() {
        currentRound = 1;
        currentDisaster = CardGenerator.generateRandomDisaster();
        disasterLabel.setText("🌋 КАТАСТРОФА: " + currentDisaster.getName() + " — " + currentDisaster.getDescription());

        // Генерація 6 гравців (1 людина, 5 ботів)
        players = new ArrayList<>();
        players.add(new Player(1, "Ви (Гравець)", false, 1, CardGenerator.generateRandomCard()));
        for (int i = 2; i <= 6; i++) {
            players.add(new Player(i, "Бот " + (i - 1), true, i, CardGenerator.generateRandomCard()));
        }

        chatArea.setText("=== ПОЧАТОК НОВОЇ ПАРТІЇ У БУНКЕРІ ===\n");
        chatArea.append("🤖 Скан бункера завершено. Усього місць: 3 з 6.\n");
        chatArea.append("📢 Увага! Відкрийте одну зі своїх рис для обговорення.\n\n");

        updateUIComponents();
    }

    private void updateUIComponents() {
        playersPanel.removeAll();
        for (Player p : players) {
            JPanel pCard = new JPanel(new BorderLayout());
            pCard.setBackground(p.isEliminated() ? new Color(50, 20, 20) : new Color(32, 32, 38));
            pCard.setBorder(BorderFactory.createLineBorder(p.isEliminated() ? Color.RED : new Color(0, 255, 102), 2));

            JLabel nameLabel = new JLabel((p.isBot() ? "🤖 " : "👤 ") + p.getName() + (p.isEliminated() ? " [ВИГНАНО]" : ""), SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            nameLabel.setForeground(Color.WHITE);
            pCard.add(nameLabel, BorderLayout.NORTH);

            JTextArea info = new JTextArea();
            info.setEditable(false);
            info.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info.setBackground(pCard.getBackground());
            info.setForeground(Color.LIGHT_GRAY);

            StringBuilder sb = new StringBuilder();
            CharacterCard card = p.getCard();
            if (card.isProfessionRevealed()) sb.append("Професія: ").append(card.getProfession()).append("\n");
            if (card.isHealthRevealed()) sb.append("Здоров'я: ").append(card.getHealthCondition()).append("\n");
            if (card.isBaggageRevealed()) sb.append("Багаж: ").append(card.getBaggage()).append("\n");

            if (sb.length() == 0) sb.append("Характеристики поки приховані...");
            info.setText(sb.toString());

            pCard.add(info, BorderLayout.CENTER);
            playersPanel.add(pCard);
        }

        // Оновлення моєї картки внизу
        myCardPanel.removeAll();
        if (!players.isEmpty()) {
            CharacterCard myCard = players.get(0).getCard();
            JLabel myInfo = new JLabel(String.format("<html><b>Ваш Персонаж:</b> %s (%d років)<br><b>Професія:</b> %s (%d років досвіду)<br><b>Здоров'я:</b> %s<br><b>Багаж:</b> %s</html>",
                    myCard.getName(), myCard.getAge(), myCard.getProfession(), myCard.getExperienceYears(), myCard.getHealthCondition(), myCard.getBaggage()));
            myInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
            myInfo.setForeground(Color.WHITE);
            myCardPanel.add(myInfo);

            JButton btnRevealProf = new JButton("Відкрити Професію");
            btnRevealProf.addActionListener(e -> {
                myCard.setProfessionRevealed(true);
                chatArea.append("👤 Ви відкрили свою професію: " + myCard.getProfession() + "\n");
                updateUIComponents();
            });
            myCardPanel.add(btnRevealProf);
        }

        playersPanel.revalidate();
        playersPanel.repaint();
        myCardPanel.revalidate();
        myCardPanel.repaint();
    }

    private void triggerVotingPhase() {
        // Просте голосування
        String[] options = players.stream()
                .filter(p -> !p.isEliminated() && p.getId() != 1)
                .map(Player::getName)
                .toArray(String[]::new);

        if (options.length == 0) return;

        String selected = (String) JOptionPane.showInputDialog(this,
                "Оберіть гравця, якого вважаєте найменш корисним для бункера:",
                "🗳️ Голосування та вигнання", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selected != null) {
            for (Player p : players) {
                if (p.getName().equals(selected)) {
                    p.setEliminated(true);
                    chatArea.append("🚨 БІЛЬШІСТЮ ГОЛОСІВ ВИГНАНО: " + p.getName() + "!\n");
                    break;
                }
            }
            updateUIComponents();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Малювання фону 2D підземного сховища
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 5));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
