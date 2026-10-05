package main;

import main.db.DatabaseHandler;
import main.db.GameSessionDAO;
import main.db.UserDAO;
import main.models.User;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== ІНІЦІАЛІЗАЦІЯ БАЗИ ДАНИХ КУРСОВОЇ РОБОТИ (kursova_db) ===");
        
        // 1. Автоматичне створення таблиць (users, achievements, game_sessions)
        DatabaseHandler.initializeDatabaseTables();

        // 2. Тестова реєстрація/вхід користувача
        User testUser = UserDAO.registerOrLogin("Player1", "password123");
        if (testUser != null) {
            System.out.println("✅ Користувач авторизований: " + testUser.getUsername() + " (ID: " + testUser.getId() + ", Рівень: " + testUser.getLevel() + ")");
            
            // Нарахуємо початковий досвід і збережемо ачівку
            UserDAO.addExperienceAndStats(testUser.getId(), 250, true);
            UserDAO.unlockAchievement(testUser.getId(), "FIRST_BLOOD", "Перший крок у бункер");
            
            // Запишемо тестову сесію гри
            GameSessionDAO.saveSession(testUser.getId(), "Ядерна зима", "SINGLEPLAYER", 6, 3, 85);
        }
    }
}
