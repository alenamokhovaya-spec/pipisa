package org.rebelland.pipisa.model;

import org.mineacademy.fo.remain.CompMaterial;

public class QuestModel {
    private CompMaterial block;
    private int amount;
    private int progress;
    private String name;
    private boolean completed;
    private String lore;

    public QuestModel (CompMaterial block, int amount,  int progress, boolean completed, String name, String lore) {
        this.block = block;
        this.amount = amount;
        this.progress = progress;
        this.completed = completed;
        this.name = name;
        this.lore = lore;
    }

    public void updateProgress(int progress){
        this.progress = progress;
        if (progress >= amount) {this.completed = true;}
    }

    public CompMaterial getBlock() { return block; }
    public int getAmount() { return amount; }
    public int getProgress() {return progress; }
    public boolean getCompleted() {return completed; }
    public String getName() { return name; }
    public String getLore() { return lore; }
}