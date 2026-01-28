package org.rebelland.pipisa.database;

import org.rebelland.rcore.RCore;
import java.sql.*;
import java.util.UUID;

public class TreasuryRepository {

    private static TreasuryRepository instance;

    public static TreasuryRepository getInstance() {
        if (instance == null) instance = new TreasuryRepository();
        return instance;
    }

    public void initializeTables() {
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Таблица Казны (balance)
            // id = название казны, amount = деньги
            stmt.execute("CREATE TABLE IF NOT EXISTS treasuries (" +
                    "  id VARCHAR(64) PRIMARY KEY," +
                    "  amount INT DEFAULT 0" +
                    ") DEFAULT CHARSET=utf8mb4");

            // 2. Таблица Игроков (players)
            // Глобальный кэш игроков. Никаких связей с казной.
            stmt.execute("CREATE TABLE IF NOT EXISTS treasury_players (" +
                    "  uuid VARCHAR(36) PRIMARY KEY," +
                    "  name VARCHAR(64) NOT NULL" +
                    ") DEFAULT CHARSET=utf8mb4");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Казна (Balance) ---

    /**
     * Создает казну (счет), если её нет.
     */
    public boolean createTreasury(String treasuryId) {
        String sql = "INSERT IGNORE INTO treasuries (id, amount) VALUES (?, 0)";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treasuryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteTreasury(String treasuryId) {
        String sql = "DELETE FROM treasuries WHERE id = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treasuryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getBalance(String treasuryId) {
        String sql = "SELECT amount FROM treasuries WHERE id = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treasuryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("amount");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void deposit(String treasuryId, int amount) {
        if (amount <= 0) return;
        String sql = "UPDATE treasuries SET amount = amount + ? WHERE id = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, treasuryId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Снять деньги (атомарно).
     * @return true, если денег хватило и снятие прошло успешно.
     */
    public boolean withdraw(String treasuryId, int amount) {
        if (amount <= 0) return false;
        String sql = "UPDATE treasuries SET amount = amount - ? WHERE id = ? AND amount >= ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, treasuryId);
            ps.setInt(3, amount); // Проверка баланса
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean treasuryExists(String treasuryId) {
        String sql = "SELECT 1 FROM treasuries WHERE id = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treasuryId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- Игроки (Глобальный реестр) ---

    /**
     * Сохраняет или обновляет данные игрока.
     * Вызывать при входе игрока (PlayerJoinEvent), чтобы база знала ники.
     */
    public void updatePlayer(UUID uuid, String name) {
        String sql = "INSERT INTO treasury_players (uuid, name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name)";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Получить имя игрока по UUID.
     */
    public String getPlayerName(UUID uuid) {
        String sql = "SELECT name FROM treasury_players WHERE uuid = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
