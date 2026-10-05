package main.ui.rpg;

import java.awt.*;

public class Avatar2D {
    private final String name;
    private int x, y;
    private final Color color;
    private final boolean isBot;

    private int doctorLevel;
    private int mechanicLevel;
    private int coins;

    public Avatar2D(String name, int x, int y, Color color, boolean isBot, int doctorLevel, int mechanicLevel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
        this.isBot = isBot;
        this.doctorLevel = doctorLevel;
        this.mechanicLevel = mechanicLevel;
        this.coins = 100;
    }

    public void move(int dx, int dy, Rectangle boundary) {
        int newX = x + dx;
        int newY = y + dy;

        if (boundary.contains(newX, newY, 32, 40)) {
            x = newX;
            y = newY;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Малювання 2D аватарки в стилі Among Us (скафандр / костюм з візором та рюкзаком)
     */
    public void draw(Graphics2D g2) {
        // 1. Рюкзак ззаду
        g2.setColor(color.darker());
        g2.fillRoundRect(x - 6, y + 8, 10, 20, 6, 6);

        // 2. Основне тіло
        g2.setColor(color);
        g2.fillRoundRect(x, y, 28, 36, 16, 16);

        // 3. Обводка тіла
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, 28, 36, 16, 16);

        // 4. Глянцевий візор (шолом)
        g2.setColor(new Color(150, 220, 255));
        g2.fillRoundRect(x + 12, y + 6, 14, 12, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x + 12, y + 6, 14, 12, 8, 8);

        // Блік на візорі
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 15, y + 8, 4, 3);

        // 5. Ім'я над персонажем
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(name, x - 5, y - 6);
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public boolean isBot() { return isBot; }

    public int getDoctorLevel() { return doctorLevel; }
    public void setDoctorLevel(int doctorLevel) { this.doctorLevel = doctorLevel; }

    public int getMechanicLevel() { return mechanicLevel; }
    public void setMechanicLevel(int mechanicLevel) { this.mechanicLevel = mechanicLevel; }

    public int getCoins() { return coins; }
    public void addCoins(int amount) { this.coins += amount; }
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
}
