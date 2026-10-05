package main.db;

import main.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static User registerOrLogin(String username, String password) {
        User existingUser = login(username, password);
        if (existingUser != null) {
            return existingUser;
        }

        // Якщо користувача немає — реєструємо
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // У реальному проєкті можна шифрувати
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new User(id, username, 1, 1, 0, 0, 0);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка реєстрації користувача: " + e.getMessage());
        }
        return null;
    }

    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("avatar_index"),
                        rs.getInt("level"),
                        rs.getInt("experience"),
                        rs.getInt("games_played"),
                        rs.getInt("games_won")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка авторизації: " + e.getMessage());
        }
        return null;
    }

    public static void addExperienceAndStats(int userId, int xpAmount, boolean won) {
        String sql = "UPDATE users SET experience = experience + ?, games_played = games_played + 1, " +
                "games_won = games_won + ? WHERE id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, xpAmount);
            stmt.setInt(2, won ? 1 : 0);
            stmt.setInt(3, userId);
            stmt.executeUpdate();

            checkLevelUp(userId);
        } catch (SQLException e) {
            System.err.println("❌ Помилка оновлення досвіду: " + e.getMessage());
        }
    }

    private static void checkLevelUp(int userId) {
        // Кожен рівень вимагає (level * 500) XP
        String selectSql = "SELECT level, experience FROM users WHERE id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int level = rs.getInt("level");
                int xp = rs.getInt("experience");
                int requiredXp = level * 500;

                if (xp >= requiredXp) {
                    String updateSql = "UPDATE users SET level = level + 1 WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.executeUpdate();
                        System.out.println("🎉 ВІТАЄМО! Користувач підвищив рівень до " + (level + 1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка перевірки рівня: " + e.getMessage());
        }
    }

    public static void unlockAchievement(int userId, String code, String title) {
        String sql = "INSERT IGNORE INTO achievements (user_id, achievement_code, title) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, code);
            stmt.setString(3, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Помилка розблокування досягнення: " + e.getMessage());
        }
    }

    public static List<String> getUserAchievements(int userId) {
        List<String> achievements = new ArrayList<>();
        String sql = "SELECT title FROM achievements WHERE user_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                achievements.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка читання досягнень: " + e.getMessage());
        }
        return achievements;
    }
}
