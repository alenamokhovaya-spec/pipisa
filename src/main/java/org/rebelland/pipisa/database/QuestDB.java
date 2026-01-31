package org.rebelland.pipisa.database;

import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.rcore.RCore;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestDB {

    private static QuestDB instance;

    public static QuestDB getInstance() {
        if (instance == null) instance = new QuestDB();
        return instance;
    }

    public void initializeTablesQuest() {
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS quests (" +
                    "  block VARCHAR(64)," +
                    "  progress INT DEFAULT 0," +
                    "  maxprogress INT DEFAULT 0," +
                    "  uuid VARCHAR(36)," +
                    "  PRIMARY KEY (uuid, block)" +
                    ") DEFAULT CHARSET=utf8mb4");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Создать квест
    public boolean createQuest(UUID uuid, CompMaterial block, int maxProgress){
        String sql = "INSERT IGNORE INTO quests (uuid, block, maxprogress) VALUES ( ?, ?, ?)";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, block.name());
            ps.setInt(3, maxProgress);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    // Обновить прогресс
    public boolean updateProgress(UUID uuid, CompMaterial block, int newProgress) {
        String sql = "UPDATE quests SET progress = ? WHERE uuid = ? AND block = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newProgress);
            ps.setString(2, uuid.toString());
            ps.setString(3, block.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //Количество записей с определенным игроком
    public int getQuestCountByUUID(String uuid) {
        String sql = "SELECT COUNT(*) as count FROM quests WHERE uuid = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    // ПОЛУЧИТЬ ПРОГРЕСС И МАКСИМАЛЬНЫЙ ПРОГРЕСС ДЛЯ ПРОВЕРКИ
    public QuestProgress getQuestProgress(UUID uuid, CompMaterial block) {
        String sql = "SELECT progress, maxprogress FROM quests WHERE uuid = ? AND block = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, block.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new QuestProgress(rs.getInt("progress"), rs.getInt("maxprogress"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    /**
     * Получить все блоки (CompMaterial) для указанного UUID
     * @param uuid UUID игрока
     * @return Список блоков или пустой список, если записей нет
     */
    public List<CompMaterial> getAllBlocksByUUID(UUID uuid) {
        List<CompMaterial> blocks = new ArrayList<>();
        String sql = "SELECT block FROM quests WHERE uuid = ?";

        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        CompMaterial material = CompMaterial.valueOf(rs.getString("block"));
                        blocks.add(material);
                    } catch (Exception e) {
                        // Логируем ошибку, если не удалось преобразовать строку в CompMaterial
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blocks;
    }


    // ПРОВЕРКА: ДОСТИГ ЛИ ПРОГРЕСС МАКСИМАЛЬНОГО ЗНАЧЕНИЯ
    public boolean isQuestCompleted(UUID uuid, CompMaterial block) {
        QuestProgress progress = getQuestProgress(uuid, block);
        if (progress == null) return false;

        // Проверка: progress >= maxprogress
        return progress.getProgress() >= progress.getMaxProgress();
    }
    // КЛАСС-ОБОЛОЧКА ДЛЯ ХРАНЕНИЯ ПРОГРЕССА
    public static class QuestProgress {
        private final int progress;
        private final int maxProgress;

        public QuestProgress(int progress, int maxProgress) {
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public int getProgress() { return progress; }
        public int getMaxProgress() { return maxProgress; }

        // Метод для проверки больше или равно
        public boolean isCompleted() {
            return progress >= maxProgress;
        }

        // Метод для получения оставшегося прогресса
        public int getRemaining() {
            return Math.max(0, maxProgress - progress);
        }
    }


}
