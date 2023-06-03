package com.antonio32a.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum GlobalModel {
    ARROW_LEFT(Material.DIAMOND, 1),
    ARROW_LEFT_DISABLED(Material.DIAMOND, 2),
    ARROW_RIGHT(Material.DIAMOND, 3),
    ARROW_RIGHT_DISABLED(Material.DIAMOND, 4),
    CHECKMARK_TRUE(Material.DIAMOND, 5),
    CHECKMARK_FALSE(Material.DIAMOND, 6);

    @Getter private final Material material;
    @Getter private final int customModelData;

    @NotNull
    public ItemStack createStack() {
        ItemStack stack = new ItemStack(material, 1);
        stack.editMeta(meta -> {
            meta.setCustomModelData(customModelData);
            meta.displayName(Component.text("§r"));
        });
        return stack;
    }
}
