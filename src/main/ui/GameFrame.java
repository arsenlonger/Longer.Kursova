package main.ui;

import main.ui.rpg.InteractiveBunkerPanel;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private final MainMenuPanel menuPanel;
    private final SettingsPanel settingsPanel;
    private final ProfilePanel profilePanel;
    private final GameBoardPanel gameBoardPanel;
    private final LANLobbyPanel lanLobbyPanel;
    private final InteractiveBunkerPanel rpgBunkerPanel;

    public GameFrame() {
        setTitle("☣️ БУНКЕР 2D: СУДНИЙ ДЕНЬ (WASD RPG)");
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
        lanLobbyPanel = new LANLobbyPanel(this);
        rpgBunkerPanel = new InteractiveBunkerPanel(this);

        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(settingsPanel, "SETTINGS");
        mainContainer.add(profilePanel, "PROFILE");
        mainContainer.add(gameBoardPanel, "GAME");
        mainContainer.add(lanLobbyPanel, "LAN");
        mainContainer.add(rpgBunkerPanel, "RPG");

        add(mainContainer);
        showPanel("MENU");
    }

    public void showPanel(String panelName) {
        if ("PROFILE".equals(panelName)) {
            profilePanel.reloadProfileData();
        } else if ("GAME".equals(panelName)) {
            gameBoardPanel.startNewGame();
        } else if ("RPG".equals(panelName)) {
            rpgBunkerPanel.requestFocusInWindow();
        }
        cardLayout.show(mainContainer, panelName);
    }
}
