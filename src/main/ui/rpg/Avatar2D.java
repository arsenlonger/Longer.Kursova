package main.ui.rpg;

import java.awt.*;

public class Avatar2D {
    private final String name;
    private int x, y;
    private final Color color;

    private int doctorLevel;   // 0: Немає, 1: Обробка ран, 2: Шви, 3: Операції
    private int mechanicLevel; // 0: Немає, 1: Тимчасовий ремонт, 2: Надійний ремонт, 3: Повний
    private int coins;

    public Avatar2D(String name, int x, int y, Color color, int doctorLevel, int mechanicLevel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
        this.doctorLevel = doctorLevel;
        this.mechanicLevel = mechanicLevel;
        this.coins = 100; // Початкові монети
    }

    public void move(int dx, int dy, Rectangle boundary) {
        int newX = x + dx;
        int newY = y + dy;

        if (boundary.contains(newX, newY, 24, 24)) {
            x = newX;
            y = newY;
        }
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }

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
