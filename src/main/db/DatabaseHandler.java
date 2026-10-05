package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static final String DB_NAME = "kursova_db";
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Автоматичне створення таблиць в БД, якщо вони ще не існують
     */
    public static void initializeDatabaseTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL," +
                "avatar_index INT DEFAULT 1," +
                "level INT DEFAULT 1," +
                "experience INT DEFAULT 0," +
                "games_played INT DEFAULT 0," +
                "games_won INT DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createAchievementsTable = "CREATE TABLE IF NOT EXISTS achievements (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "achievement_code VARCHAR(50) NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE KEY unique_user_achieve (user_id, achievement_code)," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ");";

        String createGameSessionsTable = "CREATE TABLE IF NOT EXISTS game_sessions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "disaster_name VARCHAR(100) NOT NULL," +
                "mode VARCHAR(20) NOT NULL," +
                "total_players INT NOT NULL," +
                "survivors_count INT NOT NULL," +
                "survival_score INT NOT NULL," +
                "played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createAchievementsTable);
            stmt.execute(createGameSessionsTable);
            System.out.println("✅ Таблиці теми 'kursova_db' успішно перевірено/ініціалізовано.");
        } catch (SQLException e) {
            System.err.println("❌ Помилка ініціалізації таблиць в БД: " + e.getMessage());
        }
    }
}
