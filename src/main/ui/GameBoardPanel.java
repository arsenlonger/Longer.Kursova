package main.ui;

import main.ai.BotAI;
import main.db.GameSessionDAO;
import main.logic.SurvivalCalculator;
import main.models.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Верхня панель
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 36));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 87, 51), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        disasterLabel = new JLabel("🌋 КАТАСТРОФА: Завантаження...", SwingConstants.CENTER);
        disasterLabel.setFont(new Font("Serif", Font.BOLD, 20));
        disasterLabel.setForeground(new Color(255, 87, 51));
        topPanel.add(disasterLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Центральна панель - 2D Бункер та список гравців
        playersPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        playersPanel.setOpaque(false);
        add(playersPanel, BorderLayout.CENTER);

        // Права панель - Чат дискусії ботів
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(360, 0));
        rightPanel.setBackground(new Color(22, 22, 26));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY), "💬 ЧАТ ОБГОВОРЕННЯ БОТІВ",
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

        // Нижня панель - Картка та Кнопки Повернення в 2D Бункер
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        myCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        myCardPanel.setBackground(new Color(25, 25, 30));
        bottomPanel.add(myCardPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        BunkerButton btnReturnToRpg = new BunkerButton("🏃 Повернутися у 2D Бункер");
        btnReturnToRpg.setPreferredSize(new Dimension(240, 45));
        btnReturnToRpg.addActionListener(e -> mainFrame.showPanel("RPG"));

        BunkerButton btnVote = new BunkerButton("🗳️ Голосування");
        btnVote.setPreferredSize(new Dimension(180, 45));
        btnVote.addActionListener(e -> triggerVotingPhase());

        BunkerButton btnExit = new BunkerButton("🚪 Меню");
        btnExit.setPreferredSize(new Dimension(110, 45));
        btnExit.addActionListener(e -> mainFrame.showPanel("MENU"));

        actionsPanel.add(btnReturnToRpg);
        actionsPanel.add(btnVote);
        actionsPanel.add(btnExit);

        bottomPanel.add(actionsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void startNewGame() {
        currentRound = 1;
        currentDisaster = CardGenerator.generateRandomDisaster();
        disasterLabel.setText("🌋 КАТАСТРОФА: " + currentDisaster.getName() + " — " + currentDisaster.getDescription());

        players = new ArrayList<>();
        players.add(new Player(1, "Ви (Гравець)", false, 1, CardGenerator.generateRandomCard()));
        for (int i = 2; i <= 6; i++) {
            players.add(new Player(i, "Бот " + (i - 1), true, i, CardGenerator.generateRandomCard()));
        }

        chatArea.setText("=== ПОЧАТОК НОВОЇ ПАРТІЇ У БУНКЕРІ ===\n");
        chatArea.append("🤖 Скан бункера завершено. Усього місць: 3 з 6.\n");
        chatArea.append("📢 Раунд 1: Відкрийте одну зі своїх рис для обговорення.\n\n");

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

            if (sb.length() == 0) sb.append("Характеристики приховані...");
            info.setText(sb.toString());

            pCard.add(info, BorderLayout.CENTER);
            playersPanel.add(pCard);
        }

        myCardPanel.removeAll();
        if (!players.isEmpty() && !players.get(0).isEliminated()) {
            CharacterCard myCard = players.get(0).getCard();
            JLabel myInfo = new JLabel(String.format("<html><b>Персонаж:</b> %s (%d р.) | <b>Професія:</b> %s<br><b>Здоров'я:</b> %s | <b>Багаж:</b> %s</html>",
                    myCard.getName(), myCard.getAge(), myCard.getProfession(), myCard.getHealthCondition(), myCard.getBaggage()));
            myInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
            myInfo.setForeground(Color.WHITE);
            myCardPanel.add(myInfo);

            JButton btnRevealProf = new JButton("Професія");
            btnRevealProf.addActionListener(e -> revealTrait(0, "PROFESSION"));
            JButton btnRevealHealth = new JButton("Здоров'я");
            btnRevealHealth.addActionListener(e -> revealTrait(0, "HEALTH"));
            JButton btnRevealBaggage = new JButton("Багаж");
            btnRevealBaggage.addActionListener(e -> revealTrait(0, "BAGGAGE"));

            myCardPanel.add(btnRevealProf);
            myCardPanel.add(btnRevealHealth);
            myCardPanel.add(btnRevealBaggage);
        }

        playersPanel.revalidate();
        playersPanel.repaint();
        myCardPanel.revalidate();
        myCardPanel.repaint();
    }

    private void revealTrait(int playerIndex, String traitType) {
        CharacterCard card = players.get(playerIndex).getCard();
        if ("PROFESSION".equals(traitType)) card.setProfessionRevealed(true);
        if ("HEALTH".equals(traitType)) card.setHealthRevealed(true);
        if ("BAGGAGE".equals(traitType)) card.setBaggageRevealed(true);

        chatArea.append("👤 Ви відкрили рису для обговорення!\n");

        for (Player p : players) {
            if (p.isBot() && !p.isEliminated()) {
                p.getCard().setProfessionRevealed(true);
                Player botTarget = BotAI.selectVoteTarget(p, players, currentDisaster);
                if (botTarget != null) {
                    chatArea.append(BotAI.generateChatComment(p, botTarget, currentDisaster) + "\n");
                }
            }
        }
        updateUIComponents();
    }

    private void triggerVotingPhase() {
        List<Player> active = players.stream().filter(p -> !p.isEliminated()).collect(Collectors.toList());
        if (active.size() <= 3) {
            finishGameAndShowEpilogue(active);
            return;
        }

        String[] options = active.stream()
                .filter(p -> p.getId() != 1)
                .map(Player::getName)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Оберіть гравця для вигнання з бункера:",
                "🗳️ Таємне голосування (Раунд " + currentRound + ")", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selected != null) {
            for (Player p : players) {
                if (p.getName().equals(selected)) {
                    p.setEliminated(true);
                    chatArea.append("\n🚨 РЕЗУЛЬТАТ: Більшістю голосів вигнано " + p.getName() + "!\n\n");
                    break;
                }
            }
            currentRound++;
            updateUIComponents();

            List<Player> remaining = players.stream().filter(p -> !p.isEliminated()).collect(Collectors.toList());
            if (remaining.size() <= 3) {
                finishGameAndShowEpilogue(remaining);
            } else {
                // Після голосування пропонуємо повернутися у 2D Бункер
                int choice = JOptionPane.showConfirmDialog(this,
                        "Голосування завершено! Бажаєте повернутися у 2D Бункер для продовження гри?",
                        "🏃 Повернення в 2D Бункер", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    mainFrame.showPanel("RPG");
                }
            }
        }
    }

    private void finishGameAndShowEpilogue(List<Player> survivors) {
        SurvivalCalculator.SurvivalResult result = SurvivalCalculator.calculateBunkerSurvival(survivors, currentDisaster);

        GameSessionDAO.saveSession(1, currentDisaster.getName(), "SINGLEPLAYER", 6, survivors.size(), result.getScorePercent());

        JOptionPane.showMessageDialog(this, result.getEpilogue(),
                "🏆 ФІНАЛ ГРИ ТА РЕЗУЛЬТАТИ ВІДНОВЛЕННЯ",
                result.isVictory() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        mainFrame.showPanel("PROFILE");
    }
}
