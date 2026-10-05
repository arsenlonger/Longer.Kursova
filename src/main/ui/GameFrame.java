package main.ui;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private final MainMenuPanel menuPanel;
    private final SettingsPanel settingsPanel;
    private final ProfilePanel profilePanel;
    private final GameBoardPanel gameBoardPanel;

    public GameFrame() {
        setTitle("☣️ БУНКЕР 2D: СУДНИЙ ДЕНЬ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 600));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        menuPanel = new MainMenuPanel(this);
        settingsPanel = new SettingsPanel(this);
        profilePanel = new ProfilePanel(this);
        gameBoardPanel = new GameBoardPanel(this);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(settingsPanel, "SETTINGS");
        mainContainer.add(profilePanel, "PROFILE");
        mainContainer.add(gameBoardPanel, "GAME");

        add(mainContainer);
        showPanel("MENU");
    }

    public void showPanel(String panelName) {
        if ("PROFILE".equals(panelName)) {
            profilePanel.reloadProfileData();
        } else if ("GAME".equals(panelName)) {
            gameBoardPanel.startNewGame();
        }
        cardLayout.show(mainContainer, panelName);
    }
}
