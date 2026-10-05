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

    // Ресурси (0 - 100%)
    private int oxygenLevel = 100;
    private int foodLevel = 100;
    private int waterLevel = 100;

    // Цикл Днів та Ночі
    private int currentDay = 1;
    private boolean isNight = false;
    private boolean oxygenLeak = false;

    private Timer gameLoopTimer;
    private Timer botMovementTimer;
    private String statusNotification = "Керування WASD: ходіть по бункеру. Натисніть E для взаємодії з кімнатою!";

    public InteractiveBunkerPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 16));
        setFocusable(true);

        initAvatars();
        initInputListeners();
        initTimers();
        initTopHUD();
    }

    private void initAvatars() {
        // Гравець (Зелений космонавт)
        playerAvatar = new Avatar2D("Ви (Гравець)", 450, 120, new Color(46, 204, 113), false, 0, 1);

        // 5 Ботів (Among Us кольори)
        botAvatars.clear();
        botAvatars.add(new Avatar2D("Бот 1 (Інженер)", 120, 100, new Color(231, 76, 60), true, 0, 3));
        botAvatars.add(new Avatar2D("Бот 2 (Лікар)", 120, 320, new Color(52, 152, 219), true, 3, 0));
        botAvatars.add(new Avatar2D("Бот 3 (Агроном)", 450, 320, new Color(241, 196, 15), true, 0, 0));
        botAvatars.add(new Avatar2D("Бот 4 (Електрик)", 750, 320, new Color(155, 89, 182), true, 0, 2));
        botAvatars.add(new Avatar2D("Бот 5 (Військовий)", 750, 100, new Color(230, 126, 34), true, 0, 0));
    }

    private void initInputListeners() {
        // Гарантія фокусу клавіатури при кліку мишею
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isNight) return; // Вночі рухатися не можна

                int speed = 12;
                Rectangle bounds = new Rectangle(30, 30, 880, 430);

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        playerAvatar.move(0, -speed, bounds);
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        playerAvatar.move(0, speed, bounds);
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        playerAvatar.move(-speed, 0, bounds);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        playerAvatar.move(speed, 0, bounds);
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

    private void initTimers() {
        // Повільний цикл втрати ресурсів (Кисень: 1% в 5 сек, Їжа/Вода: 1% в 8 сек)
        gameLoopTimer = new Timer(1000, e -> {
            if (isNight) return;

            // Втрата кисню
            if (oxygenLeak) {
                oxygenLevel = Math.max(0, oxygenLevel - 2);
            } else if (System.currentTimeMillis() % 5000 < 1000) {
                oxygenLevel = Math.max(0, oxygenLevel - 1);
            }

            // Втрата їжі та води
            if (System.currentTimeMillis() % 8000 < 1000) {
                foodLevel = Math.max(0, foodLevel - 1);
                waterLevel = Math.max(0, waterLevel - 1);
            }

            // Рідкісний витік кисню (1% шанс)
            if (Math.random() < 0.01 && !oxygenLeak) {
                oxygenLeak = true;
                statusNotification = "⚠️ ПОЛОМКА! Витік кисню! Підійдіть до Кисневого блоку та натисніть E!";
            }

            repaint();
        });
        gameLoopTimer.start();

        // Анімація ходьби ботів по бункеру
        Random rand = new Random();
        botMovementTimer = new Timer(2500, e -> {
            if (isNight) return;
            for (Avatar2D bot : botAvatars) {
                int dx = rand.nextInt(41) - 20;
                int dy = rand.nextInt(41) - 20;
                bot.move(dx, dy, new Rectangle(30, 30, 880, 430));
            }
            repaint();
        });
        botMovementTimer.start();
    }

    private void initTopHUD() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setOpaque(false);

        BunkerButton btnSleep = new BunkerButton("🛏️ Лягти спати (Кінець Дня)");
        btnSleep.setPreferredSize(new Dimension(250, 38));
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
        statusNotification = "🌙 НІЧ " + currentDay + ": Усі персонажі лягають спати...";

        // Відправляємо персонажів до спальні
        playerAvatar.setPosition(720, 100);
        int offset = 0;
        for (Avatar2D bot : botAvatars) {
            bot.setPosition(720 + (offset % 3) * 35, 120 + (offset / 3) * 35);
            offset++;
        }

        repaint();

        // Через 3.5 секунди настає новий день
        Timer nightTimer = new Timer(3500, e -> {
            currentDay++;
            isNight = false;
            statusNotification = "☀️ ДЕНЬ " + currentDay + ": Новий день у бункері! Усі встали до роботи.";
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
                oxygenLevel = Math.min(100, oxygenLevel + 40);
                statusNotification = "🔧 Ви успішно відремонтували подачу кисню!";
            } else {
                statusNotification = "💨 Кисневий блок працює нормально.";
            }
        } else if (BunkerMap.LIBRARY_MED_BAY.bounds.contains(p)) {
            openLibraryDialog();
        } else if (BunkerMap.COUNCIL_ROOM.bounds.contains(p)) {
            statusNotification = "🗳️ Ви сіли за Стіл Переговорів. Відкривається фаза голосування!";
            mainFrame.showPanel("GAME");
        } else if (BunkerMap.HYDROPONICS_ROOM.bounds.contains(p)) {
            foodLevel = Math.min(100, foodLevel + 25);
            statusNotification = "🍲 Ви зібрали урожай! Їжу поповнено.";
        } else if (BunkerMap.WATER_STATION.bounds.contains(p)) {
            waterLevel = Math.min(100, waterLevel + 25);
            statusNotification = "💧 Ви обслугували насос води! Воду поповнено.";
        } else if (BunkerMap.SLEEPING_QUARTERS.bounds.contains(p)) {
            startNightPhase();
        }
    }

    private void openLibraryDialog() {
        String[] options = {"Вивчити Механіку Lvl 2 (50 монет)", "Вивчити Медицину Lvl 1 (50 монет)", "Отримати 30 монет за чергування", "Скасувати"};
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

        // 1. Малювання лівої панелі днів (ДЕНЬ N)
        g2.setColor(new Color(25, 25, 32));
        g2.fillRect(10, 40, 110, 400);
        g2.setColor(new Color(255, 87, 51));
        g2.drawRect(10, 40, 110, 400);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Serif", Font.BOLD, 22));
        g2.drawString("🗓️ ДЕНЬ", 22, 75);
        g2.setFont(new Font("Serif", Font.BOLD, 48));
        g2.setColor(new Color(0, 255, 102));
        g2.drawString(String.valueOf(currentDay), 48, 135);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Статус:", 25, 180);
        g2.setColor(isNight ? Color.CYAN : Color.YELLOW);
        g2.drawString(isNight ? "🌙 НІЧ" : "☀️ ДЕНЬ", 25, 200);

        // 2. Малювання 2D Кімнат Бункера у стилі Among Us
        for (BunkerMap.Room room : BunkerMap.getAllRooms()) {
            // Зсув кімнат праворуч від панелі днів
            Rectangle r = new Rectangle(room.bounds.x + 90, room.bounds.y, room.bounds.width, room.bounds.height);

            g2.setColor(room.color);
            g2.fill(r);
            g2.setColor(new Color(0, 255, 102));
            g2.setStroke(new BasicStroke(2));
            g2.draw(r);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(room.name, r.x + 12, r.y + 25);
        }

        // 3. Малювання об'єктів у кімнатах (Стіл, Генератор, Медліжка)
        // Стіл у Залі Засідань
        g2.setColor(new Color(100, 60, 40));
        g2.fillOval(490, 110, 120, 70);
        g2.setColor(Color.BLACK);
        g2.drawOval(490, 110, 120, 70);

        // 4. Малювання 2D аватарки гравця та 5 ботів (Among Us Style)
        for (Avatar2D bot : botAvatars) {
            bot.draw(g2);
        }
        playerAvatar.draw(g2);

        // 5. Затемнення екрана вночі (Нічний режим)
        if (isNight) {
            g2.setColor(new Color(0, 0, 20, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Serif", Font.BOLD, 36));
            g2.drawString("🌙 НІЧ " + currentDay + ": БУНКЕР СПИТЬ", getWidth() / 2 - 240, getHeight() / 2);
        }

        // 6. Малювання нижнього HUD ресурсів
        g2.setColor(new Color(20, 20, 26));
        g2.fillRect(130, 460, 800, 65);
        g2.setColor(new Color(0, 255, 102));
        g2.drawRect(130, 460, 800, 65);

        // Шкала кисню
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("💨 КИСЕНЬ: " + oxygenLevel + "%", 150, 482);
        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(150, 492, oxygenLevel * 2, 12);

        // Шкала їжі
        g2.setColor(Color.WHITE);
        g2.drawString("🍲 ЇЖА: " + foodLevel + "%", 400, 482);
        g2.setColor(Color.ORANGE);
        g2.fillRect(400, 492, foodLevel * 2, 12);

        // Шкала води
        g2.setColor(Color.WHITE);
        g2.drawString("💧 ВОДА: " + waterLevel + "%", 650, 482);
        g2.setColor(Color.BLUE);
        g2.fillRect(650, 492, waterLevel * 2, 12);

        // Повідомлення стану
        g2.setColor(Color.YELLOW);
        g2.drawString(statusNotification, 150, 518);
    }
}
