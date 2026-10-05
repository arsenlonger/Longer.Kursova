package main.ui.rpg;

import java.awt.*;

public class Coin2D {
    private final int x;
    private final int y;
    private final int amount;
    private boolean isCollected;

    public Coin2D(int x, int y, int amount) {
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.isCollected = false;
    }

    public void draw(Graphics2D g2) {
        if (isCollected) return;

        // Малювання сяючої золотої монети 🪙
        g2.setColor(new Color(255, 215, 0));
        g2.fillOval(x, y, 14, 14);

        g2.setColor(new Color(218, 165, 32));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, 14, 14);

        g2.setColor(Color.WHITE);
        g2.fillOval(x + 3, y + 3, 4, 4);
    }

    public boolean checkPickup(int playerX, int playerY) {
        if (isCollected) return false;

        double dist = Math.hypot(playerX - x, playerY - y);
        if (dist < 22) {
            isCollected = true;
            return true;
        }
        return false;
    }

    public int getAmount() { return amount; }
    public boolean isCollected() { return isCollected; }
}
