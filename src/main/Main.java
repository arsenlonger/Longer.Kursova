package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    // Налаштування підключення до бази даних у MAMP
    private static final String DB_NAME = "kursova_db";
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        System.out.println("=== Перевірка підключення до бази даних ===");
        System.out.println("Подключення до: " + URL);

        try (Connection connection = getConnection()) {
            System.out.println("✅ УСПІХ: З'єднання з базою даних '" + DB_NAME + "' встановлено!");
            
            // Тестовий запит для перевірки працездатності
            try (Statement statement = connection.createStatement()) {
                System.out.println("✅ База даних готова до роботи та виконання запитів.");
            }
        } catch (SQLException e) {
            System.err.println("❌ ПОМИЛКА підключення до бази даних!");
            System.err.println("Причина: " + e.getMessage());
            System.err.println("\nМожливі причини:");
            System.err.println("1. Перевірте, чи запущені сервери в MAMP (Apache та MySQL).");
            System.err.println("2. Якщо у MAMP для root встановлено порожній пароль, змініть PASSWORD на \"\".");
            e.printStackTrace();
        }
    }

    /**
     * Отримати підключення до бази даних kursova_db
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
