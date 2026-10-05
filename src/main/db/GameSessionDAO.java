package main.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameSessionDAO {

    public static void saveSession(int userId, String disasterName, String mode, int totalPlayers, int survivorsCount, int survivalScore) {
        String sql = "INSERT INTO game_sessions (user_id, disaster_name, mode, total_players, survivors_count, survival_score) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, disasterName);
            stmt.setString(3, mode);
            stmt.setInt(4, totalPlayers);
            stmt.setInt(5, survivorsCount);
            stmt.setInt(6, survivalScore);
            stmt.executeUpdate();
            System.out.println("✅ Історію гри збережено у БД (Оцінка виживання: " + survivalScore + "%).");
        } catch (SQLException e) {
            System.err.println("❌ Помилка збереження сесії гри: " + e.getMessage());
        }
    }

    public static List<String> getUserGameHistory(int userId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT disaster_name, mode, survivors_count, total_players, survival_score, played_at " +
                "FROM game_sessions WHERE user_id = ? ORDER BY played_at DESC LIMIT 10";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String entry = String.format("Катастрофа: %s | Режим: %s | Вижило: %d/%d | Шанс: %d%% | Дата: %s",
                        rs.getString("disaster_name"),
                        rs.getString("mode"),
                        rs.getInt("survivors_count"),
                        rs.getInt("total_players"),
                        rs.getInt("survival_score"),
                        rs.getTimestamp("played_at").toString());
                history.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка читання історії ігор: " + e.getMessage());
        }
        return history;
    }
}
