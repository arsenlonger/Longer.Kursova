package main;

import main.db.DatabaseHandler;
import main.ui.GameFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // 1. Автоматична перевірка та створення таблиць у MySQL (MAMP)
        DatabaseHandler.initializeDatabaseTables();

        // 2. Запуск GUI додатка в потоці Swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            GameFrame frame = new GameFrame();
            frame.setVisible(true);
            System.out.println("🚀 Гра 'Бункер 2D' успішно запущена!");
        });
    }
}
