package main.ui.rpg;

import main.config.TextureLoader;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Avatar2D {
    private final String name;
    private float x, y;
    private float targetX, targetY;
    private final Color jacketColor;
    private final Color hairColor;
    private final boolean isBot;

    private int doctorLevel;
    private int mechanicLevel;
    private int coins;

    public Avatar2D(String name, float x, float y, Color jacketColor, Color hairColor, boolean isBot, int doctorLevel, int mechanicLevel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.jacketColor = jacketColor;
        this.hairColor = hairColor;
        this.isBot = isBot;
        this.doctorLevel = doctorLevel;
        this.mechanicLevel = mechanicLevel;
        this.coins = 100;
    }

    public void updateSmoothMovement(Rectangle boundary) {
        if (isBot) {
            float speed = 1.2f;
            if (Math.abs(x - targetX) > speed) {
                x += (targetX > x) ? speed : -speed;
            }
            if (Math.abs(y - targetY) > speed) {
                y += (targetY > y) ? speed : -speed;
            }
        }
    }

    public void movePlayer(float dx, float dy, Rectangle boundary) {
        float newX = x + dx;
        float newY = y + dy;

        if (boundary.contains(newX, newY, 28, 28)) {
            x = newX;
            y = newY;
        }
    }

    public void setTargetPosition(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }

    /**
     * Малювання 2D-персонажа: якщо у паці assets/images є PNG спрайт, малює PNG, інакше процедурну людину.
     */
    public void draw(Graphics2D g2) {
        int ix = (int) x;
        int iy = (int) y;

        // Перевірка наявності кастомного PNG спрайта з Figma
        String spriteName = isBot ? "bot.png" : "player.png";
        BufferedImage customSprite = TextureLoader.getTexture(spriteName);

        if (customSprite != null) {
            // Малювання завантаженого PNG спрайта з Figma
            g2.drawImage(customSprite, ix, iy, 36, 48, null);
        } else {
            // Процедурне малювання виживальника
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(ix - 2, iy + 22, 28, 10);

            g2.setColor(jacketColor);
            g2.fillRoundRect(ix, iy + 10, 24, 18, 8, 8);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(ix, iy + 10, 24, 18, 8, 8);

            g2.setColor(new Color(245, 198, 165));
            g2.fillOval(ix + 3, iy, 18, 18);
            g2.setColor(Color.BLACK);
            g2.drawOval(ix + 3, iy, 18, 18);

            g2.setColor(hairColor);
            g2.fillArc(ix + 3, iy, 18, 12, 0, 180);

            g2.setColor(Color.BLACK);
            g2.fillOval(ix + 7, iy + 8, 2, 3);
            g2.fillOval(ix + 14, iy + 8, 2, 3);
        }

        // 5. Ім'я персонажа
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(name);

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(ix + 12 - textWidth / 2 - 4, iy - 16, textWidth + 8, 14);

        g2.setColor(isBot ? new Color(255, 200, 100) : new Color(0, 255, 150));
        g2.drawString(name, ix + 12 - textWidth / 2, iy - 5);
    }

    public String getName() { return name; }
    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public Color getJacketColor() { return jacketColor; }
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
