package org.rebelland.pipisa.model;

import org.mineacademy.fo.remain.CompMaterial;

public class SimpleQuests {
    private final CompMaterial block;
    private final int maxProgress;
    private int progress;
    private final String title;
    private final String description;
    private boolean isCompleted; // Новое поле

    public SimpleQuests(
            CompMaterial block,
            int maxProgress,
            int progress,
            String title,
            String description,
            boolean isCompleted
    ) {
        this.block = block;
        this.maxProgress = maxProgress;
        this.progress = progress;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public CompMaterial getBlock() {
        return block;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        // Автоматическая проверка завершения при установке прогресса
        if (this.progress >= this.maxProgress) {
            this.isCompleted = true;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}