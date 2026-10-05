package main.ui;

import main.db.GameSessionDAO;
import main.db.UserDAO;
import main.models.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProfilePanel extends JPanel {
    private final GameFrame mainFrame;
    private final JTextArea historyArea;
    private final JLabel userInfoLabel;

    public ProfilePanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 24));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Верхній заголовок
        JLabel titleLabel = new JLabel("🏆 ПРОФІЛЬ ГРАВЦЯ ТА ІСТОРІЯ ІГОР", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 87, 51));
        add(titleLabel, BorderLayout.NORTH);

        // Інформація про користувача
        userInfoLabel = new JLabel("Завантаження даних профілю...", SwingConstants.CENTER);
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        userInfoLabel.setForeground(Color.LIGHT_GRAY);

        // Історія ігор
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setBackground(new Color(12, 12, 16));
        historyArea.setForeground(new Color(0, 255, 102));
        JScrollPane scrollPane = new JScrollPane(historyArea);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(userInfoLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Кнопка повернення
        BunkerButton btnBack = new BunkerButton("⬅️ Назад до Меню");
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void reloadProfileData() {
        User user = UserDAO.login("Player1", "password123");
        if (user != null) {
            userInfoLabel.setText(String.format("👤 Користувач: %s  |  🎖️ Рівень: %d  |  ⭐ Досвід (XP): %d  |  🎮 Ігор: %d (Перемог: %d)",
                    user.getUsername(), user.getLevel(), user.getExperience(), user.getGamesPlayed(), user.getGamesWon()));

            List<String> history = GameSessionDAO.getUserGameHistory(user.getId());
            StringBuilder sb = new StringBuilder("=== ОСТАННІ ЗІГРАНІ ПАРТІЇ ===\n\n");
            if (history.isEmpty()) {
                sb.append("Історія ігор поки порожня. Зіграйте першу партію!");
            } else {
                for (String line : history) {
                    sb.append("• ").append(line).append("\n");
                }
            }
            historyArea.setText(sb.toString());
        }
    }
}
