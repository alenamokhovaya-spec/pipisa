package org.rebelland.pipisa.command.lists;

import org.mineacademy.fo.remain.CompMaterial;

public class SimpleQuests {
    public final CompMaterial block;
    public final int maxProgress;
    public final String title;
    public final String description;

    public SimpleQuests(
            CompMaterial block,
            int maxProgress,
            String title,
            String description
    ) {
        this.block = block;
        this.maxProgress = maxProgress;
        this.title = title;
        this.description = description;
    }
}