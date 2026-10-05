package main.ui;

import main.net.GameClient;
import main.net.GameServer;
import main.net.NetworkPacket;
import javax.swing.*;
import java.awt.*;

public class LANLobbyPanel extends JPanel {
    private final GameFrame mainFrame;
    private GameServer server;
    private GameClient client;

    private JTextField ipField;
    private JTextArea lobbyLogArea;
    private JLabel statusLabel;

    public LANLobbyPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(20, 20, 24));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Заголовок
        JLabel titleLabel = new JLabel("🌐 МЕРЕЖЕВА ГРА ПО ЛОКАЛЬНІЙ МЕРЕЖІ (LAN)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 87, 51));
        add(titleLabel, BorderLayout.NORTH);

        // Центральна частина
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setOpaque(false);

        JLabel ipLabel = new JLabel("IP Хоста:");
        ipLabel.setForeground(Color.WHITE);
        ipLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        ipField = new JTextField("127.0.0.1", 12);
        ipField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        BunkerButton btnHost = new BunkerButton("🏠 Створити Кімнату (Host)");
        btnHost.setPreferredSize(new Dimension(240, 40));
        btnHost.addActionListener(e -> startHost());

        BunkerButton btnJoin = new BunkerButton("🔗 Приєднатися");
        btnJoin.setPreferredSize(new Dimension(180, 40));
        btnJoin.addActionListener(e -> joinServer());

        controlsPanel.add(btnHost);
        controlsPanel.add(ipLabel);
        controlsPanel.add(ipField);
        controlsPanel.add(btnJoin);

        centerPanel.add(controlsPanel, BorderLayout.NORTH);

        // Лобі лог
        lobbyLogArea = new JTextArea("Вкажіть IP-адресу та приєднайтеся до сервера або створіть власну кімнату.\n");
        lobbyLogArea.setEditable(false);
        lobbyLogArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lobbyLogArea.setBackground(new Color(12, 12, 16));
        lobbyLogArea.setForeground(new Color(0, 255, 102));

        JScrollPane scrollPane = new JScrollPane(lobbyLogArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Статус: Очікування дій...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Нижня кнопка Назад
        BunkerButton btnBack = new BunkerButton("⬅️ Назад до Меню");
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startHost() {
        if (server == null) {
            server = new GameServer();
            server.startServer();
            lobbyLogArea.append("✅ LAN Сервер запущено на порту 9876! Очікування гравців...\n");
            statusLabel.setText("Статус: Хост запущено (Port 9876)");
            joinServer();
        }
    }

    private void joinServer() {
        String ip = ipField.getText().trim();
        client = new GameClient();
        boolean connected = client.connect(ip, 9876, this::onPacketReceived);
        if (connected) {
            lobbyLogArea.append("✅ Успішно підключено до хоста " + ip + "!\n");
            statusLabel.setText("Статус: Підключено до " + ip);
            client.sendPacket(new NetworkPacket(NetworkPacket.PacketType.CONNECT, "Гравець", "Приєднався до лобі"));
        } else {
            lobbyLogArea.append("❌ Помилка підключення до " + ip + "\n");
        }
    }

    private void onPacketReceived(NetworkPacket packet) {
        SwingUtilities.invokeLater(() -> {
            lobbyLogArea.append("📩 [" + packet.getSenderName() + "]: " + packet.getContent() + "\n");
        });
    }
}
