package main.ui;

import main.config.SoundManager;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final GameFrame mainFrame;

    public SettingsPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(new Color(22, 22, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("⚙️ НАЛАШТУВАННЯ ГРИ");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 87, 51));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Перемикач Музики
        JCheckBox chkMusic = new JCheckBox("Увімкнути Фонову Музику", SoundManager.isMusicEnabled());
        chkMusic.setFont(new Font("SansSerif", Font.BOLD, 18));
        chkMusic.setForeground(Color.WHITE);
        chkMusic.setOpaque(false);
        chkMusic.addActionListener(e -> SoundManager.setMusicEnabled(chkMusic.isSelected()));
        gbc.gridy = 1;
        add(chkMusic, gbc);

        // Перемикач Ефектів
        JCheckBox chkEffects = new JCheckBox("Увімкнути Звукові Ефекти", SoundManager.isSoundEffectsEnabled());
        chkEffects.setFont(new Font("SansSerif", Font.BOLD, 18));
        chkEffects.setForeground(Color.WHITE);
        chkEffects.setOpaque(false);
        chkEffects.addActionListener(e -> SoundManager.setSoundEffectsEnabled(chkEffects.isSelected()));
        gbc.gridy = 2;
        add(chkEffects, gbc);

        // Кнопка Назад
        BunkerButton btnBack = new BunkerButton("⬅️ Назад до Меню");
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));
        gbc.gridy = 3;
        add(btnBack, gbc);
    }
}
