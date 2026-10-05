package main.ui.rpg;

import main.ui.BunkerButton;
import main.ui.GameFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InteractiveBunkerPanel extends JPanel {
    private final GameFrame mainFrame;

    private Avatar2D playerAvatar;
    private final List<Avatar2D> botAvatars = new ArrayList<>();

    // Клавіші керування (Smooth WASD Input)
    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;

    // Ресурси (0 - 100%)
    private float oxygenLevel = 100f;
    private float foodLevel = 100f;
    private float waterLevel = 100f;

    // Цикл Днів та Ночі
    private int currentDay = 1;
    private boolean isNight = false;
    private boolean oxygenLeak = false;

    private Timer gameLoop60Fps;
    private long lastBotWaypointTime = 0;
    private String statusNotification = "Керування WASD: ходіть по бункеру. Клавіша E або Пробіл — взаємодія з кімнатами!";

    public InteractiveBunkerPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 14));
        setFocusable(true);

        initAvatars();
        initInputListeners();
        initGameEngine60Fps();
        initTopHUD();
    }

    private void initAvatars() {
        // Гравець (Завантажує player.png з 2 фреймами для анімації ходьби)
        playerAvatar = new Avatar2D("Ви (Гравець)", 440, 120, new Color(46, 204, 113), new Color(100, 60, 30), false, 0, 1);

        // 5 Ботів-людей із різним одягом та зачісками
        botAvatars.clear();
        botAvatars.add(new Avatar2D("Бот 1 (Інженер)", 120, 100, new Color(230, 126, 34), new Color(50, 50, 50), true, 0, 3));
        botAvatars.add(new Avatar2D("Бот 2 (Лікар)", 120, 310, new Color(240, 240, 240), new Color(180, 120, 60), true, 3, 0));
        botAvatars.add(new Avatar2D("Бот 3 (Агроном)", 440, 310, new Color(241, 196, 15), new Color(80, 40, 20), true, 0, 0));
        botAvatars.add(new Avatar2D("Бот 4 (Електрик)", 740, 310, new Color(155, 89, 182), new Color(30, 30, 30), true, 0, 2));
        botAvatars.add(new Avatar2D("Бот 5 (Військовий)", 740, 100, new Color(52, 73, 94), new Color(120, 90, 40), true, 0, 0));
    }

    private void initInputListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: case KeyEvent.VK_UP: wPressed = true; break;
                    case KeyEvent.VK_S: case KeyEvent.VK_DOWN: sPressed = true; break;
                    case KeyEvent.VK_A: case KeyEvent.VK_LEFT: aPressed = true; break;
                    case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: dPressed = true; break;
                    case KeyEvent.VK_E: case KeyEvent.VK_SPACE: interactWithCurrentRoom(); break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: case KeyEvent.VK_UP: wPressed = false; break;
                    case KeyEvent.VK_S: case KeyEvent.VK_DOWN: sPressed = false; break;
                    case KeyEvent.VK_A: case KeyEvent.VK_LEFT: aPressed = false; break;
                    case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: dPressed = false; break;
                }
            }
        });
    }

    private void initGameEngine60Fps() {
        Random rand = new Random();

        gameLoop60Fps = new Timer(16, e -> {
            if (!isNight) {
                // 1. Переміщення гравця з оновленням анімації ходьби
                float moveSpeed = 3.5f;
                float dx = 0, dy = 0;
                if (wPressed) dy -= moveSpeed;
                if (sPressed) dy += moveSpeed;
                if (aPressed) dx -= moveSpeed;
                if (dPressed) dx += moveSpeed;

                boolean moving = (dx != 0 || dy != 0);
                playerAvatar.setMoving(moving);
                if (moving) {
                    playerAvatar.movePlayer(dx, dy, new Rectangle(30, 30, 880, 430));
                }
                playerAvatar.updateSmoothMovement(new Rectangle(30, 30, 880, 430));

                // 2. Переміщення ботів
                for (Avatar2D bot : botAvatars) {
                    bot.updateSmoothMovement(new Rectangle(30, 30, 880, 430));
                }

                long now = System.currentTimeMillis();
                if (now - lastBotWaypointTime > 4000) {
                    lastBotWaypointTime = now;
                    for (Avatar2D bot : botAvatars) {
                        int tx = 50 + rand.nextInt(780);
                        int ty = 50 + rand.nextInt(350);
                        bot.setTargetPosition(tx, ty);
                    }
                }

                // 3. Збалансоване зменшення ресурсів
                float o2Loss = oxygenLeak ? 0.05f : 0.008f;
                oxygenLevel = Math.max(0f, oxygenLevel - o2Loss);
                foodLevel = Math.max(0f, foodLevel - 0.004f);
                waterLevel = Math.max(0f, waterLevel - 0.004f);

                if (rand.nextFloat() < 0.0005f && !oxygenLeak) {
                    oxygenLeak = true;
                    statusNotification = "⚠️ УВАГА! Витік у Кисневому блоці! Підійдіть до генератора та натисніть E!";
                }
            }
            repaint();
        });
        gameLoop60Fps.start();
    }

    private void initTopHUD() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setOpaque(false);

        BunkerButton btnSleep = new BunkerButton("🛏️ Лягти спати (Кінець Дня)");
        btnSleep.setPreferredSize(new Dimension(260, 38));
        btnSleep.addActionListener(e -> startNightPhase());

        BunkerButton btnBack = new BunkerButton("⬅️ Меню");
        btnBack.setPreferredSize(new Dimension(120, 38));
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));

        topPanel.add(btnSleep);
        topPanel.add(btnBack);

        add(topPanel, BorderLayout.NORTH);
    }

    public void startNightPhase() {
        if (isNight) return;
        isNight = true;
        statusNotification = "🌙 НІЧ " + currentDay + ": Усі мешканці бункера сплять...";

        playerAvatar.setPosition(720, 100);
        int offset = 0;
        for (Avatar2D bot : botAvatars) {
            bot.setPosition(720 + (offset % 3) * 35, 120 + (offset / 3) * 35);
            offset++;
        }

        repaint();

        Timer nightTimer = new Timer(3000, e -> {
            currentDay++;
            isNight = false;
            statusNotification = "☀️ ДЕНЬ " + currentDay + ": Новий день у бункері! Усі приступили до роботи.";
            ((Timer) e.getSource()).stop();
            repaint();
        });
        nightTimer.setRepeats(false);
        nightTimer.start();
    }

    private void interactWithCurrentRoom() {
        Point p = new Point(playerAvatar.getX(), playerAvatar.getY());

        if (BunkerMap.OXYGEN_ROOM.bounds.contains(p)) {
            if (oxygenLeak) {
                oxygenLeak = false;
                oxygenLevel = Math.min(100f, oxygenLevel + 40f);
                statusNotification = "🔧 Ви відремонтували клапан кисневого блоку!";
            } else {
                statusNotification = "💨 Кисневий блок працює стабільно.";
            }
        } else if (BunkerMap.LIBRARY_MED_BAY.bounds.contains(p)) {
            openLibraryDialog();
        } else if (BunkerMap.COUNCIL_ROOM.bounds.contains(p)) {
            statusNotification = "🗳️ Ви сіли за Стіл Переговорів. Відкривається фаза голосування!";
            mainFrame.showPanel("GAME");
        } else if (BunkerMap.HYDROPONICS_ROOM.bounds.contains(p)) {
            foodLevel = Math.min(100f, foodLevel + 25f);
            statusNotification = "🍲 Ви зібрали врожай у теплиці! Їжу поповнено.";
        } else if (BunkerMap.WATER_STATION.bounds.contains(p)) {
            waterLevel = Math.min(100f, waterLevel + 25f);
            statusNotification = "💧 Ви обслугували насос води! Воду поповнено.";
        } else if (BunkerMap.SLEEPING_QUARTERS.bounds.contains(p)) {
            startNightPhase();
        }
    }

    private void openLibraryDialog() {
        String[] options = {"Вивчити Механіку Lvl 2 (50 монет)", "Вивчити Медицину Lvl 1 (50 монет)", "Заробити 30 монет за зміну", "Скасувати"};
        int choice = JOptionPane.showOptionDialog(this,
                "📚 БІБЛІОТЕКА ТА МЕДПУНКТ\nБаланс: " + playerAvatar.getCoins() + " монет.\nРівень Медицини: " + playerAvatar.getDoctorLevel() + " | Механіки: " + playerAvatar.getMechanicLevel(),
                "Навчання у бункері", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0 && playerAvatar.spendCoins(50)) {
            playerAvatar.setMechanicLevel(2);
            statusNotification = "🎓 Ви вивчили Механіку 2 рівня!";
        } else if (choice == 1 && playerAvatar.spendCoins(50)) {
            playerAvatar.setDoctorLevel(1);
            statusNotification = "🎓 Ви вивчили Медицину 1 рівня!";
        } else if (choice == 2) {
            playerAvatar.addCoins(30);
            statusNotification = "💰 Ви відпрацювали зміну та отримали 30 монет!";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(new Color(16, 17, 22));
        g2.fillRect(0, 0, width, height);

        g2.setColor(new Color(25, 27, 34));
        g2.setStroke(new BasicStroke(1));
        for (int x = 0; x < width; x += 40) {
            g2.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += 40) {
            g2.drawLine(0, y, width, y);
        }

        g2.setColor(new Color(22, 24, 30));
        g2.fillRect(10, 40, 110, 400);
        g2.setColor(new Color(255, 87, 51));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(10, 40, 110, 400);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Serif", Font.BOLD, 22));
        g2.drawString("🗓️ ДЕНЬ", 22, 75);
        g2.setFont(new Font("Serif", Font.BOLD, 46));
        g2.setColor(new Color(0, 255, 102));
        g2.drawString(String.valueOf(currentDay), 48, 135);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Статус:", 25, 180);
        g2.setColor(isNight ? Color.CYAN : Color.YELLOW);
        g2.drawString(isNight ? "🌙 НІЧ" : "☀️ ДЕНЬ", 25, 200);

        for (BunkerMap.Room room : BunkerMap.getAllRooms()) {
            Rectangle r = new Rectangle(room.bounds.x + 90, room.bounds.y, room.bounds.width, room.bounds.height);

            g2.setPaint(new GradientPaint(r.x, r.y, room.color, r.x + r.width, r.y + r.height, room.color.darker()));
            g2.fill(r);

            g2.setColor(new Color(0, 255, 102, 180));
            g2.setStroke(new BasicStroke(2));
            g2.draw(r);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString(room.name, r.x + 10, r.y + 22);
        }

        g2.setColor(new Color(110, 65, 40));
        g2.fillOval(480, 105, 130, 75);
        g2.setColor(Color.BLACK);
        g2.drawOval(480, 105, 130, 75);

        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(150, 80, 35, 60);
        g2.fillRect(195, 80, 35, 60);
        g2.setColor(Color.WHITE);
        g2.drawRect(150, 80, 35, 60);
        g2.drawRect(195, 80, 35, 60);

        g2.setColor(new Color(40, 140, 50));
        g2.fillRect(440, 290, 170, 35);
        g2.setColor(Color.GREEN);
        g2.drawRect(440, 290, 170, 35);

        for (Avatar2D bot : botAvatars) {
            bot.draw(g2);
        }
        playerAvatar.draw(g2);

        if (isNight) {
            g2.setColor(new Color(0, 0, 25, 210));
            g2.fillRect(0, 0, width, height);

            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Serif", Font.BOLD, 36));
            g2.drawString("🌙 НІЧ " + currentDay + ": БУНКЕР СПИТЬ", width / 2 - 220, height / 2);
        }

        g2.setColor(new Color(22, 24, 30));
        g2.fillRect(130, 460, 800, 65);
        g2.setColor(new Color(0, 255, 102));
        g2.drawRect(130, 460, 800, 65);

        int ox = (int) oxygenLevel;
        int fx = (int) foodLevel;
        int wx = (int) waterLevel;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("💨 КИСЕНЬ: " + ox + "%", 150, 482);
        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(150, 492, ox * 2, 12);

        g2.setColor(Color.WHITE);
        g2.drawString("🍲 ЇЖА: " + fx + "%", 400, 482);
        g2.setColor(Color.ORANGE);
        g2.fillRect(400, 492, fx * 2, 12);

        g2.setColor(Color.WHITE);
        g2.drawString("💧 ВОДА: " + wx + "%", 650, 482);
        g2.setColor(Color.BLUE);
        g2.fillRect(650, 492, wx * 2, 12);

        g2.setColor(Color.YELLOW);
        g2.drawString(statusNotification, 150, 518);
    }
}
