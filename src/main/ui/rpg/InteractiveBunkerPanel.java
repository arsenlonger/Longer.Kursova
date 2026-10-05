package main.ui.rpg;

import main.models.CardGenerator;
import main.models.CharacterCard;
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
    private CharacterCard playerCard;
    private final List<Avatar2D> botAvatars = new ArrayList<>();
    private final List<Coin2D> droppedCoins = new ArrayList<>();

    // Навички
    private int botanyLevel = 0;

    // Інвентар
    private boolean hasWaterBucket = false;
    private boolean hasSeeds = true;

    // Фізична робота
    private boolean isPerformingWork = false;
    private int workSecondsRemaining = 0;
    private Timer workTimer;

    // Клавіші керування
    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;

    // Ресурси
    private float oxygenLevel = 100f;
    private float foodLevel = 100f;
    private float waterLevel = 100f;

    // Теплиця
    public enum FarmStage { EMPTY, PLANTED, GROWING, READY }
    private FarmStage currentFarmStage = FarmStage.EMPTY;
    private int farmGrowSecondsRemaining = 0;
    private Timer farmTimer;

    // Цикл Днів
    private int currentDay = 1;
    private boolean isNight = false;
    private boolean oxygenLeak = false;
    private long leakStartTime = 0;

    private Timer gameLoop60Fps;
    private long lastBotWaypointTime = 0;
    private String statusNotification = "Керування WASD: прохід крізь ДВЕРІ. Натисніть E для прибирання, поливу та дій!";

    public InteractiveBunkerPanel(GameFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 14));
        setFocusable(true);

        initPlayerCard();
        initAvatars();
        initInputListeners();
        initGameEngine60Fps();
        initTopHUD();
    }

    private void initPlayerCard() {
        playerCard = CardGenerator.generateRandomCard();
    }

    private void initAvatars() {
        playerAvatar = new Avatar2D("Ви (Гравець)", 480, 120, new Color(46, 204, 113), new Color(100, 60, 30), false, 0, 1);

        botAvatars.clear();
        botAvatars.add(new Avatar2D("Бот 1 (Інженер)", 120, 100, new Color(230, 126, 34), new Color(50, 50, 50), true, 0, 3));
        botAvatars.add(new Avatar2D("Бот 2 (Лікар)", 120, 330, new Color(240, 240, 240), new Color(180, 120, 60), true, 3, 0));
        botAvatars.add(new Avatar2D("Бот 3 (Агроном)", 480, 330, new Color(241, 196, 15), new Color(80, 40, 20), true, 0, 0));
        botAvatars.add(new Avatar2D("Бот 4 (Електрик)", 800, 330, new Color(155, 89, 182), new Color(30, 30, 30), true, 0, 2));
        botAvatars.add(new Avatar2D("Бот 5 (Військовий)", 800, 100, new Color(52, 73, 94), new Color(120, 90, 40), true, 0, 0));
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
                // Поміркована, контрольована швидкість руху (2.2f)
                float moveSpeed = isPerformingWork ? 0f : 2.2f;
                float dx = 0, dy = 0;
                if (wPressed) dy -= moveSpeed;
                if (sPressed) dy += moveSpeed;
                if (aPressed) dx -= moveSpeed;
                if (dPressed) dx += moveSpeed;

                boolean moving = (dx != 0 || dy != 0);
                playerAvatar.setMoving(moving);
                if (moving) {
                    playerAvatar.movePlayer(dx, dy, new Rectangle(30, 30, 980, 480));
                }
                playerAvatar.updateSmoothMovement(new Rectangle(30, 30, 980, 480));

                // Збір монет під ногами
                for (Coin2D coin : droppedCoins) {
                    if (coin.checkPickup(playerAvatar.getX(), playerAvatar.getY())) {
                        playerAvatar.addCoins(coin.getAmount());
                        statusNotification = "💰 Зібрано монету 🪙! Поточний баланс: " + playerAvatar.getCoins() + " монет.";
                    }
                }

                // Інженер авто-ремонтує кисень з затримкою 4 сек
                if (oxygenLeak) {
                    Avatar2D engineer = botAvatars.get(0);
                    engineer.setTargetPosition(150, 100);

                    if (System.currentTimeMillis() - leakStartTime > 4000) {
                        oxygenLeak = false;
                        oxygenLevel = Math.min(100f, oxygenLevel + 35f);
                        statusNotification = "🛠️ Бот 1 (Інженер) усунув витік кисню у Кисневому блоці!";
                    }
                }

                for (Avatar2D bot : botAvatars) {
                    if (!oxygenLeak || bot != botAvatars.get(0)) {
                        bot.updateSmoothMovement(new Rectangle(30, 30, 980, 480));
                    }
                }

                long now = System.currentTimeMillis();
                if (now - lastBotWaypointTime > 5000) {
                    lastBotWaypointTime = now;
                    for (Avatar2D bot : botAvatars) {
                        if (!oxygenLeak || bot != botAvatars.get(0)) {
                            int tx = 50 + rand.nextInt(880);
                            int ty = 50 + rand.nextInt(400);
                            bot.setTargetPosition(tx, ty);
                        }
                    }
                }

                // ПОВІЛЬНА ВТРАТА (Кисень: 1% в 20 сек)
                float o2Loss = oxygenLeak ? 0.04f : 0.0015f;
                oxygenLevel = Math.max(0f, oxygenLevel - o2Loss);
                foodLevel = Math.max(0f, foodLevel - 0.002f);
                waterLevel = Math.max(0f, waterLevel - 0.002f);

                if (rand.nextFloat() < 0.0002f && !oxygenLeak) {
                    oxygenLeak = true;
                    leakStartTime = System.currentTimeMillis();
                    statusNotification = "⚠️ УВАГА! Витік у Кисневому блоці! Інженер висунувся на ремонт!";
                }
            }
            repaint();
        });
        gameLoop60Fps.start();
    }

    private void initTopHUD() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        topPanel.setOpaque(false);

        BunkerButton btnCard = new BunkerButton("🎴 Моя Картка (Хто я)");
        btnCard.setPreferredSize(new Dimension(220, 38));
        btnCard.addActionListener(e -> showMyCardDialog());

        BunkerButton btnSleep = new BunkerButton("🛏️ Лягти спати (Кінець Дня)");
        btnSleep.setPreferredSize(new Dimension(250, 38));
        btnSleep.addActionListener(e -> startNightPhase());

        BunkerButton btnBack = new BunkerButton("⬅️ Меню");
        btnBack.setPreferredSize(new Dimension(110, 38));
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));

        topPanel.add(btnCard);
        topPanel.add(btnSleep);
        topPanel.add(btnBack);

        add(topPanel, BorderLayout.NORTH);
    }

    private void showMyCardDialog() {
        String cardDetails = String.format(
                "👤 ВАШ ПЕРСОНАЖ:\n" +
                "• Ім'я та Вік: %s, %d років (%s)\n" +
                "• Професія: %s (%d років досвіду)\n" +
                "• Здоров'я: %s\n" +
                "• Хобі: %s\n" +
                "• Фобія: %s\n" +
                "• Багаж: %s\n" +
                "• Спец-карта: %s (%s)\n" +
                "\n🎓 ВАШІ ВИВЧЕНІ НАВИЧКИ:\n" +
                "• Медицина: %d Рівень\n" +
                "• Механіка: %d Рівень\n" +
                "• Ботаніка/Агрономія: %d Рівень\n",
                playerCard.getName(), playerCard.getAge(), playerCard.getGender(),
                playerCard.getProfession(), playerCard.getExperienceYears(),
                playerCard.getHealthCondition(),
                playerCard.getHobby(),
                playerCard.getPhobia(),
                playerCard.getBaggage(),
                playerCard.getSpecialCard().getTitle(), playerCard.getSpecialCard().getDescription(),
                playerAvatar.getDoctorLevel(), playerAvatar.getMechanicLevel(), botanyLevel
        );

        JOptionPane.showMessageDialog(this, cardDetails, "🎴 КАРАТКА ПЕРСОНАЖА ТА НАВИЧКИ", JOptionPane.INFORMATION_MESSAGE);
    }

    public void startNightPhase() {
        if (isNight) return;
        isNight = true;
        statusNotification = "🌙 НІЧ " + currentDay + ": Усі мешканці бункера сплять...";

        playerAvatar.setPosition(770, 100);
        int offset = 0;
        for (Avatar2D bot : botAvatars) {
            bot.setPosition(770 + (offset % 3) * 35, 120 + (offset / 3) * 35);
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
        if (isPerformingWork) return;
        Point p = new Point(playerAvatar.getX(), playerAvatar.getY());

        if (BunkerMap.OXYGEN_ROOM.bounds.contains(p)) {
            if (oxygenLeak) {
                oxygenLeak = false;
                oxygenLevel = Math.min(100f, oxygenLevel + 40f);
                statusNotification = "🔧 Ви усунули витік у кисневому блоці!";
            } else {
                statusNotification = "💨 Кисневий блок працює нормально.";
            }
        } else if (BunkerMap.LIBRARY_MED_BAY.bounds.contains(p)) {
            openLibraryCustomDialog();
        } else if (BunkerMap.COUNCIL_ROOM.bounds.contains(p)) {
            openCouncilRoomCustomDialog();
        } else if (BunkerMap.HYDROPONICS_ROOM.bounds.contains(p)) {
            handleHydroponicsFarming();
        } else if (BunkerMap.WATER_STATION.bounds.contains(p)) {
            handleWaterStation();
        } else if (BunkerMap.SLEEPING_QUARTERS.bounds.contains(p)) {
            startNightPhase();
        }
    }

    private void startPhysicalChoreAndDropCoins(String choreName, int roomCenterX, int roomCenterY) {
        isPerformingWork = true;
        workSecondsRemaining = 2;
        statusNotification = "🧹 " + choreName + " (Залишилось: " + workSecondsRemaining + " сек)...";

        if (workTimer != null) workTimer.stop();
        workTimer = new Timer(1000, e -> {
            workSecondsRemaining--;
            if (workSecondsRemaining <= 0) {
                isPerformingWork = false;
                // Спавн 5 монет на підлозі кімнати 🪙
                Random rand = new Random();
                for (int i = 0; i < 5; i++) {
                    int cx = roomCenterX + rand.nextInt(120) - 60;
                    int cy = roomCenterY + rand.nextInt(80) - 40;
                    droppedCoins.add(new Coin2D(cx, cy, 1));
                }
                statusNotification = "🪙 На підлозі з'явилися монети! Підійдіть та зберіть їх ногами!";
                ((Timer) e.getSource()).stop();
            } else {
                statusNotification = "🧹 " + choreName + " (Залишилось: " + workSecondsRemaining + " сек)...";
            }
        });
        workTimer.start();
    }

    private void handleWaterStation() {
        String[] options = {"🪣 Набрати відро води для теплиці", "⚙️ Обслугувати насос води (+25% води)", "Скасувати"};
        int choice = JOptionPane.showOptionDialog(this,
                "💧 ВОДНА СТАНЦІЯ\nПоточне відро води: " + (hasWaterBucket ? "Є 🪣" : "Немає"),
                "Водна Станція", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            hasWaterBucket = true;
            statusNotification = "🪣 Ви набрали відро води! Тепер ідіть до Теплиці для поливу.";
        } else if (choice == 1) {
            waterLevel = Math.min(100f, waterLevel + 25f);
            statusNotification = "💧 Ви обслугували насос води! Воду поповнено.";
        }
    }

    private void handleHydroponicsFarming() {
        if (currentFarmStage == FarmStage.EMPTY) {
            if (hasSeeds) {
                currentFarmStage = FarmStage.PLANTED;
                statusNotification = "🌱 Насіння засіяно! Тепер наберіть відро води у Водній Станції та полийте теплицю.";
            } else {
                statusNotification = "❌ У вас немає насіння для засіву!";
            }
        } else if (currentFarmStage == FarmStage.PLANTED) {
            if (hasWaterBucket) {
                hasWaterBucket = false;
                currentFarmStage = FarmStage.GROWING;
                farmGrowSecondsRemaining = (botanyLevel >= 1) ? 5 : 10;
                statusNotification = "🚿 Теплицю полито відром води! Рослини ростуть (" + farmGrowSecondsRemaining + " сек)...";

                if (farmTimer != null) farmTimer.stop();
                farmTimer = new Timer(1000, e -> {
                    farmGrowSecondsRemaining--;
                    if (farmGrowSecondsRemaining <= 0) {
                        currentFarmStage = FarmStage.READY;
                        statusNotification = "🌾 УРОЖАЙ ДОЗРІВ! Натисніть E у теплиці, щоб зібрати їжу!";
                        ((Timer) e.getSource()).stop();
                    } else {
                        statusNotification = "🌱 Рослини ростуть... Залишилось: " + farmGrowSecondsRemaining + " сек.";
                    }
                });
                farmTimer.start();
            } else {
                statusNotification = "❌ У вас немає відра води! Підійдіть до Водної Станції та наберіть воду (🪣).";
            }
        } else if (currentFarmStage == FarmStage.GROWING) {
            statusNotification = "🌱 Рослини ще ростуть... Залишилось: " + farmGrowSecondsRemaining + " сек.";
        } else if (currentFarmStage == FarmStage.READY) {
            currentFarmStage = FarmStage.EMPTY;
            foodLevel = Math.min(100f, foodLevel + 40f);
            statusNotification = "🌾 УРОЖАЙ ЗІБРАНО! +40% Їжі поповнено в бункер!";
        }
    }

    private void openCouncilRoomCustomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "🗳️ ЗАЛ ЗАСІДАНЬ", true);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(new Color(22, 24, 30));

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setOpaque(false);

        JLabel infoLabel = new JLabel("<html><center><b>🗳️ ЗАЛ ЗАСІДАНЬ</b><br>Баланс: " + playerAvatar.getCoins() + " монет.<br>Оберіть дію:</center></html>", SwingConstants.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        BunkerButton btnSweep = new BunkerButton("🧹 Підмести підлогу (Скинути монети)");
        btnSweep.setPreferredSize(new Dimension(340, 42));
        btnSweep.addActionListener(e -> {
            dialog.dispose();
            startPhysicalChoreAndDropCoins("Підмітаємо підлогу у Залі Засідань", 510, 140);
        });

        BunkerButton btnVote = new BunkerButton("🗳️ Сісти за стіл (Фаза Голосування)");
        btnVote.setPreferredSize(new Dimension(340, 42));
        btnVote.addActionListener(e -> {
            dialog.dispose();
            statusNotification = "🗳️ Ви сіли за Стіл Переговорів. Перехід до голосування...";
            mainFrame.showPanel("GAME");
        });

        contentPanel.add(infoLabel);
        contentPanel.add(btnSweep);
        contentPanel.add(btnVote);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openLibraryCustomDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "📚 ЦЕНТР ПРОКАЧКИ НАВИЧОК", true);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(new Color(20, 22, 28));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("<html><center><h2>📚 БІБЛІОТЕКА ТА ПРОКАЧКА НАВИЧОК</h2>" +
                "Баланс: <b>" + playerAvatar.getCoins() + " монет</b><br><br>" +
                "• 🩸 Медицина: <b>" + playerAvatar.getDoctorLevel() + " Рівень</b><br>" +
                "• 🔧 Механіка: <b>" + playerAvatar.getMechanicLevel() + " Рівень</b><br>" +
                "• 🌱 Агрономія: <b>" + botanyLevel + " Рівень</b></center></html>", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(headerLabel);
        centerPanel.add(Box.createVerticalStrut(15));

        BunkerButton btnDust = new BunkerButton("🧼 Протерти пил з книг (Скинути монети)");
        btnDust.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDust.setPreferredSize(new Dimension(360, 40));
        btnDust.addActionListener(e -> {
            dialog.dispose();
            startPhysicalChoreAndDropCoins("Протираємо пил з книг у бібліотеці", 160, 360);
        });
        centerPanel.add(btnDust);
        centerPanel.add(Box.createVerticalStrut(10));

        BunkerButton btnMed = new BunkerButton("🩸 Прокачати Медицину Lvl " + (playerAvatar.getDoctorLevel() + 1) + " (50 монет)");
        btnMed.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMed.setPreferredSize(new Dimension(360, 40));
        btnMed.addActionListener(e -> {
            if (playerAvatar.spendCoins(50)) {
                playerAvatar.setDoctorLevel(playerAvatar.getDoctorLevel() + 1);
                statusNotification = "🎉 Ви прокачали Медицину до " + playerAvatar.getDoctorLevel() + " рівня!";
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Недостатньо монет! Потрібно 50 монет.", "Помилка", JOptionPane.WARNING_MESSAGE);
            }
        });
        centerPanel.add(btnMed);
        centerPanel.add(Box.createVerticalStrut(10));

        BunkerButton btnMech = new BunkerButton("🔧 Прокачати Механіку Lvl " + (playerAvatar.getMechanicLevel() + 1) + " (50 монет)");
        btnMech.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMech.setPreferredSize(new Dimension(360, 40));
        btnMech.addActionListener(e -> {
            if (playerAvatar.spendCoins(50)) {
                playerAvatar.setMechanicLevel(playerAvatar.getMechanicLevel() + 1);
                statusNotification = "🎉 Ви прокачали Механіку до " + playerAvatar.getMechanicLevel() + " рівня!";
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Недостатньо монет! Потрібно 50 монет.", "Помилка", JOptionPane.WARNING_MESSAGE);
            }
        });
        centerPanel.add(btnMech);
        centerPanel.add(Box.createVerticalStrut(10));

        BunkerButton btnBotany = new BunkerButton("🌱 Агрономія Lvl 1 (Прискорення росту) (50 монет)");
        btnBotany.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBotany.setPreferredSize(new Dimension(360, 40));
        btnBotany.addActionListener(e -> {
            if (playerAvatar.spendCoins(50)) {
                botanyLevel = 1;
                statusNotification = "🎉 Ви прокачали Агрономію! Рослини ростуть за 5 сек!";
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Недостатньо монет! Потрібно 50 монет.", "Помилка", JOptionPane.WARNING_MESSAGE);
            }
        });
        centerPanel.add(btnBotany);

        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Металевий фон
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

        // 2. Ліва панель Днів
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

        // 3. БІЛЬШІ КІМНАТИ БУНКЕРА
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

        // 4. Дверні проходи між кімнатами
        for (Rectangle door : BunkerMap.getAllDoors()) {
            Rectangle dr = new Rectangle(door.x + 90, door.y, door.width, door.height);
            g2.setColor(new Color(255, 180, 0, 150));
            g2.fillRect(dr.x, dr.y, dr.width, dr.height);
            g2.setColor(Color.YELLOW);
            g2.drawRect(dr.x, dr.y, dr.width, dr.height);
        }

        // 5. Меблі
        g2.setColor(new Color(110, 65, 40));
        g2.fillOval(510, 115, 140, 80);
        g2.setColor(Color.BLACK);
        g2.drawOval(510, 115, 140, 80);

        // Кисневий блок
        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(150, 80, 35, 60);
        g2.fillRect(195, 80, 35, 60);

        // Теплиця
        g2.setColor(new Color(40, 140, 50));
        g2.fillRect(470, 310, 180, 40);
        g2.setColor(Color.GREEN);
        g2.drawRect(470, 310, 180, 40);

        if (currentFarmStage == FarmStage.PLANTED) {
            g2.setColor(new Color(120, 80, 40));
            g2.drawString("🌱 Засіяно (Потрібна вода 🪣)", 475, 335);
        } else if (currentFarmStage == FarmStage.GROWING) {
            g2.setColor(Color.YELLOW);
            g2.drawString("🌱 Росте (" + farmGrowSecondsRemaining + "с)", 500, 335);
        } else if (currentFarmStage == FarmStage.READY) {
            g2.setColor(Color.ORANGE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString("🌾 УРОЖАЙ ДОЗРІВ!", 490, 335);
        }

        // 6. Малювання сяючих монет на підлозі 🪙
        for (Coin2D coin : droppedCoins) {
            coin.draw(g2);
        }

        // 7. Малювання Аватарок
        for (Avatar2D bot : botAvatars) {
            bot.draw(g2);
        }
        playerAvatar.draw(g2);

        // 8. Нічний режим
        if (isNight) {
            g2.setColor(new Color(0, 0, 25, 210));
            g2.fillRect(0, 0, width, height);

            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Serif", Font.BOLD, 36));
            g2.drawString("🌙 НІЧ " + currentDay + ": БУНКЕР СПИТЬ", width / 2 - 220, height / 2);
        }

        // 9. Нижній HUD ресурсів
        g2.setColor(new Color(22, 24, 30));
        g2.fillRect(130, 490, 850, 65);
        g2.setColor(new Color(0, 255, 102));
        g2.drawRect(130, 490, 850, 65);

        int ox = (int) oxygenLevel;
        int fx = (int) foodLevel;
        int wx = (int) waterLevel;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("💨 КИСЕНЬ: " + ox + "%", 150, 512);
        g2.setColor(oxygenLeak ? Color.RED : Color.CYAN);
        g2.fillRect(150, 522, ox * 2, 12);

        g2.setColor(Color.WHITE);
        g2.drawString("🍲 ЇЖА: " + fx + "%", 410, 512);
        g2.setColor(Color.ORANGE);
        g2.fillRect(410, 522, fx * 2, 12);

        g2.setColor(Color.WHITE);
        g2.drawString("💧 ВОДА: " + wx + "% (Відро: " + (hasWaterBucket ? "Є 🪣" : "Ні") + ")", 670, 512);
        g2.setColor(Color.BLUE);
        g2.fillRect(670, 522, wx * 2, 12);

        g2.setColor(Color.YELLOW);
        g2.drawString(statusNotification, 150, 548);
    }
}
