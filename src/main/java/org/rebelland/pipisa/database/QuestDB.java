package org.rebelland.pipisa.database;

import org.mineacademy.fo.remain.CompMaterial;
import org.rebelland.pipisa.config.QuestConfig;
import org.rebelland.pipisa.model.QuestModel;
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

            // Добавлено поле isCompleted TINYINT (как boolean)
            stmt.execute("CREATE TABLE IF NOT EXISTS quests (" +
                    "  block VARCHAR(64)," +
                    "  progress INT DEFAULT 0," +
                    "  maxprogress INT DEFAULT 0," +
                    "  isCompleted TINYINT DEFAULT 0," +
                    "  uuid VARCHAR(36)," +
                    "  PRIMARY KEY (uuid, block)" +
                    ") DEFAULT CHARSET=utf8mb4");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Создать квест
    public boolean createQuest(UUID uuid, QuestModel quest) {
        String sql = "INSERT IGNORE INTO quests (uuid, block, maxprogress, progress, isCompleted) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, quest.getBlock().name());
            ps.setInt(3, quest.getAmount());
            ps.setInt(4, quest.getProgress());
            ps.setBoolean(5, quest.getCompleted());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Сохранить прогресс квеста (обновляет progress и isCompleted)
    public boolean saveQuestProgress(UUID uuid, QuestModel quest) {
        String sql = "UPDATE quests SET progress = ?, isCompleted = ? WHERE uuid = ? AND block = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quest.getProgress());
            ps.setBoolean(2, quest.getCompleted());
            ps.setString(3, uuid.toString());
            ps.setString(4, quest.getBlock().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Загружает все квесты игрока и возвращает список SimpleQuests
     */
    public List<QuestModel> getPlayerQuests(UUID uuid) {
        List<QuestModel> quests = new ArrayList<>();
        String sql = "SELECT block, progress, maxprogress, isCompleted FROM quests WHERE uuid = ?";

        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        CompMaterial material = CompMaterial.valueOf(rs.getString("block"));
                        int progress = rs.getInt("progress");
                        int maxProgress = rs.getInt("maxprogress");
                        boolean isCompleted = rs.getBoolean("isCompleted");

                        QuestModel template = QuestConfig.getInstance().findQuestTemplate(material, maxProgress);

                        String name;
                        String lore;

                        if (template != null) {
                            name = template.getName();
                            lore = template.getLore();
                        } else { //Заглушки
                            name = "Quest for " + material.name();
                            lore = "Collect " + maxProgress + " blocks";
                        }

                        quests.add(new QuestModel(
                                material,
                                maxProgress,
                                progress,
                                isCompleted,
                                name,   // Из конфига или заглушка
                                lore    // Из конфига или заглушка
                        ));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quests;
    }
}
