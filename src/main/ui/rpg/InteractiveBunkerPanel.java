package main.ui.rpg;

import main.ui.BunkerButton;
import main.ui.GameFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InteractiveBunkerPanel extends JPanel {
    private final GameFrame mainFrame;
    private Avatar2D playerAvatar;

    // Ресурси бункера (0 - 100%)
    private int oxygenLevel = 100;
    private int foodLevel = 100;
    private int waterLevel = 100;

    private boolean oxygenLeak = false;
    private Timer gameLoopTimer;
    private String statusNotification = "Керування WASD: ходіть по бункеру. Клавіша E або Пробіл — взаємодія з кімнатами!";

    public InteractiveBunkerPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 20));
        setFocusable(true);

        playerAvatar = new Avatar2D("Ви", 450, 140, new Color(0, 255, 150), 0, 1);

        initKeyListeners();
        initGameLoop();
        initOverlayUI();
    }

    private void initKeyListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int speed = 10;
                Rectangle mapBounds = new Rectangle(40, 40, 860, 440);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        playerAvatar.move(0, -speed, mapBounds);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        playerAvatar.move(0, speed, mapBounds);
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        playerAvatar.move(-speed, 0, mapBounds);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        playerAvatar.move(speed, 0, mapBounds);
                        break;
                    case KeyEvent.VK_E:
                    case KeyEvent.VK_SPACE:
                        interactWithCurrentRoom();
                        break;
                }
                repaint();
            }
        });
    }

    private void initGameLoop() {
        // Кожної секунди зменшується кисень, їжа та вода
        gameLoopTimer = new Timer(1000, e -> {
            if (oxygenLeak) {
                oxygenLevel = Math.max(0, oxygenLevel - 5);
            } else {
                oxygenLevel = Math.max(0, oxygenLevel - 1);
            }

            foodLevel = Math.max(0, foodLevel - 1);
            waterLevel = Math.max(0, waterLevel - 1);

            // Випадкова витік кисню з імовірністю 5%
            if (Math.random() < 0.05 && !oxygenLeak) {
                oxygenLeak = true;
                statusNotification = "⚠️ УВАГА! Витік у Кисневій Кімнаті! Підійдіть до генератора та натисніть E!";
            }

            repaint();
        });
        gameLoopTimer.start();
    }

    private void initOverlayUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);

        BunkerButton btnBack = new BunkerButton("⬅️ Меню");
        btnBack.setPreferredSize(new Dimension(120, 36));
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));
        topPanel.add(btnBack);

        add(topPanel, BorderLayout.NORTH);
    }

    private void interactWithCurrentRoom() {
        Point p = new Point(playerAvatar.getX(), playerAvatar.getY());

        if (BunkerMap.OXYGEN_ROOM.bounds.contains(p)) {
            if (oxygenLeak) {
                oxygenLeak = false;
                oxygenLevel = Math.min(100, oxygenLevel + 30);
                statusNotification = "✅ Ви успішно ліквідували витік кисню!";
            } else {
                statusNotification = "💨 Генератор кисню працює справно.";
            }
        } else if (BunkerMap.LIBRARY_MED_BAY.bounds.contains(p)) {
            openLibraryDialog();
        } else if (BunkerMap.COUNCIL_ROOM.bounds.contains(p)) {
            statusNotification = "🗳️ Ви сіли за Стіл Переговорів. Відкривається фаза голосування!";
            mainFrame.showPanel("GAME");
        } else if (BunkerMap.HYDROPONICS_ROOM.bounds.contains(p)) {
            foodLevel = Math.min(100, foodLevel + 20);
            statusNotification = "🍲 Ви зібрали урожай у теплиці! Їжу поповнено.";
        } else if (BunkerMap.WATER_STATION.bounds.contains(p)) {
            waterLevel = Math.min(100, waterLevel + 20);
            statusNotification = "💧 Ви обслугували насос води! Воду поповнено.";
        }
    }

    private void openLibraryDialog() {
        String[] options = {"Вивчити Механіку Lvl 2 (50 монет)", "Вивчити Медицину Lvl 1 (50 монет)", "Отримати 30 монет за роботу у медпункті", "Скасувати"};
        int choice = JOptionPane.showOptionDialog(this,
                "📚 БІБЛІОТЕКА ТА МЕДПУНКТ\nБаланс: " + playerAvatar.getCoins() + " монет.\nВаш рівень Медицини: " + playerAvatar.getDoctorLevel() + " | Механіки: " + playerAvatar.getMechanicLevel(),
                "Навчання навичкам", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            if (playerAvatar.spendCoins(50)) {
                playerAvatar.setMechanicLevel(2);
                statusNotification = "🎓 Вітаємо! Ви вивчили Механіку 2 рівня!";
            } else {
                statusNotification = "❌ Недостатньо монет для навчання!";
            }
        } else if (choice == 1) {
            if (playerAvatar.spendCoins(50)) {
                playerAvatar.setDoctorLevel(1);
                statusNotification = "🎓 Вітаємо! Ви вивчили Медицину 1 рівня (обробка ран)!";
            } else {
                statusNotification = "❌ Недостатньо монет для навчання!";
            }
        } else if (choice == 2) {
            playerAvatar.addCoins(30);
            statusNotification = "💰 Ви попрацювали у медпункті та заробили 30 монет!";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Малювання 2D кімнат бункера
        for (BunkerMap.Room room : BunkerMap.getAllRooms()) {
            g2.setColor(room.color);
            g2.fill(room.bounds);
            g2.setColor(new Color(0, 255, 102));
            g2.setStroke(new BasicStroke(2));
            g2.draw(room.bounds);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.drawString(room.name, room.bounds.x + 15, room.bounds.y + 30);
        }

        // 2. Малювання 2D аватарки гравця
        g2.setColor(playerAvatar.getColor());
        g2.fillOval(playerAvatar.getX(), playerAvatar.getY(), 24, 24);
        g2.setColor(Color.BLACK);
        g2.drawOval(playerAvatar.getX(), playerAvatar.getY(), 24, 24);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(playerAvatar.getName(), playerAvatar.getX() - 5, playerAvatar.getY() - 5);

        // 3. Малювання верхньої панелі ресурсів (Кисень, Їжа, Вода)
        g2.setColor(new Color(20, 20, 25));
        g2.fillRect(50, 490, 840, 70);
        g2.setColor(new Color(0, 255, 102));
        g2.drawRect(50, 490, 840, 70);

        // Шкала кисню
        g2.setColor(Color.WHITE);
        g2.drawString("💨 КИСЕНЬ: " + oxygenLevel + "%", 70, 515);
        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(70, 525, oxygenLevel * 2, 12);

        // Шкала їжі
        g2.setColor(Color.WHITE);
        g2.drawString("🍲 ЇЖА: " + foodLevel + "%", 320, 515);
        g2.setColor(Color.ORANGE);
        g2.fillRect(320, 525, foodLevel * 2, 12);

        // Шкала води
        g2.setColor(Color.WHITE);
        g2.drawString("💧 ВОДА: " + waterLevel + "%", 570, 515);
        g2.setColor(Color.BLUE);
        g2.fillRect(570, 525, waterLevel * 2, 12);

        // Повідомлення стану
        g2.setColor(Color.YELLOW);
        g2.drawString(statusNotification, 70, 553);
    }
}
