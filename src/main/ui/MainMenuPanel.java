package main.ui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private final GameFrame mainFrame;

    public MainMenuPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(new Color(18, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;

        // Заголовок гри
        JLabel titleLabel = new JLabel("☣️ БУНКЕР 2D: СУДНИЙ ДЕНЬ");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 38));
        titleLabel.setForeground(new Color(255, 87, 51));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Соціально-стратегічна гра на виживання");
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Порожній відступ
        gbc.gridy = 2;
        add(Box.createVerticalStrut(20), gbc);

        // Кнопки Меню
        BunkerButton btnSingleplayer = new BunkerButton("🎮 Зайти в 2D Бункер (WASD)");
        btnSingleplayer.addActionListener(e -> mainFrame.showPanel("RPG"));
        gbc.gridy = 3;
        add(btnSingleplayer, gbc);

        BunkerButton btnMultiplayer = new BunkerButton("🌐 Мережева гра (LAN)");
        btnMultiplayer.addActionListener(e -> mainFrame.showPanel("LAN"));
        gbc.gridy = 4;
        add(btnMultiplayer, gbc);

        BunkerButton btnProfile = new BunkerButton("🏆 Профіль та Статистика");
        btnProfile.addActionListener(e -> mainFrame.showPanel("PROFILE"));
        gbc.gridy = 5;
        add(btnProfile, gbc);

        BunkerButton btnSettings = new BunkerButton("⚙️ Налаштування");
        btnSettings.addActionListener(e -> mainFrame.showPanel("SETTINGS"));
        gbc.gridy = 6;
        add(btnSettings, gbc);

        BunkerButton btnExit = new BunkerButton("🚪 Вихід з гри");
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 7;
        add(btnExit, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Малювання декоративного радіаційного фону
        g2.setColor(new Color(255, 87, 51, 15));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2.fillOval(centerX - 300, centerY - 300, 600, 600);
    }
}
